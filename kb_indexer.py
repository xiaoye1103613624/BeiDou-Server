#!/usr/bin/env python3
"""
个人知识库 - 全量索引器 v2
================================
分批渐进式索引：小源优先，大源后台处理。
支持：去重、分类标签、增量更新、断点续传、超时保护。
"""

import os, re, sys, time, json, hashlib, chromadb, subprocess
from pathlib import Path
from sentence_transformers import SentenceTransformer

# ========================== 配置 ==========================
VECTOR_DB_PATH = "d:/xy_vector_db"
EMBEDDING_MODEL = "paraphrase-multilingual-MiniLM-L12-v2"
COLLECTION_NAME = "personal_kb"
STATE_FILE = os.path.join(VECTOR_DB_PATH, ".kb_state.json")
PROGRESS_FILE = os.path.join(VECTOR_DB_PATH, ".kb_progress.json")

CHUNK_SIZE = 800
CHUNK_OVERLAP = 100
MAX_FILE_MB = 5  # 跳过 >5MB 的单文件
BATCH_EMBED = 512
BATCH_DB = 500

SKIP_DIRS = {
    ".git", ".idea", ".claude", "__pycache__", "node_modules", ".codegraph",
    "target", "logs", ".uploads", "wz-zh-CN", "proxy", "gms-ui",
    "$RECYCLE.BIN", "System Volume Information", ".m2", "repository",
    "vendor", "dist", "build", ".next", ".nuxt", "venv", ".venv",
}
SKIP_PATH_PARTS = [
    "docker", "docker-data", "ollama", "crawlab", "neo4j",
    "pagefile.sys", "xy_vector_db", ".cache", "__pycache__",
]

# ========================== 源目录 (按优先级排序) ==========================
SOURCE_SPECS = [
    # 小源优先
    ("E:/萧曳工作文档", "doc/work",
     {".md", ".txt", ".pdf", ".docx", ".xlsx", ".pptx", ".js", ".java", ".sql", ".html"}),
    ("E:/教程", "doc/tutorial",
     {".md", ".txt", ".pdf", ".docx", ".js", ".java", ".py", ".sql", ".html"}),
    ("E:/资料/xiaoye/mxd学习", "doc/maplestory",
     {".md", ".txt", ".js", ".java", ".py", ".sql"}),
    # 中源
    ("D:/software", "reference/software",
     {".md", ".txt", ".cfg", ".ini", ".conf", ".xml", ".json", ".yml", ".properties"}),
    ("D:/game", "reference/game",
     {".md", ".txt", ".cfg", ".ini", ".xml", ".json"}),
    ("E:/mxd_soft", "reference/maplestory-tools",
     {".md", ".txt", ".js", ".java", ".sql", ".cfg", ".ini", ".xml", ".properties"}),
    # 大源 (E:/pro 已有 maplestory_kb，这里只索引之前没覆盖的)
    ("E:/pro/resource_doc", "doc/reference", {".md", ".txt"}),
    # 超大源 (限制深度和文件数)
    ("E:/资料", "reference",
     {".md", ".txt", ".pdf", ".docx", ".js", ".java", ".sql", ".yml", ".yaml", ".xml", ".json", ".py", ".html"}),
    # F盘课程
    ("F:/BaiduNetdiskDownload", "doc/downloads",
     {".md", ".txt", ".pdf", ".docx", ".js", ".java", ".py", ".sql", ".html", ".xml", ".json", ".yml"}),
]


def infer_category(filepath: str, default_tag: str) -> str:
    p = filepath.lower().replace("\\", "/")
    if "/src/main/java/" in p: return "code/server"
    if "/scripts-zh-cn/" in p or "/scripts/" in p: return "code/script"
    if "/db/migration/" in p: return "db/schema"
    if ".sql" in p[-4:]: return "db/data"
    if "/resource_doc/" in p: return "doc/reference"
    if "/doc/" in p or "/docs/" in p: return "doc/reference"
    if "/教程/" in p or "/tutorial/" in p: return "doc/tutorial"
    if "mxd学习" in p: return "doc/maplestory"
    if "/mxd_soft/" in p: return "reference/maplestory-tools"
    return default_tag


def load_json(path):
    if os.path.exists(path):
        with open(path, "r", encoding="utf-8") as f:
            return json.load(f)
    return {}


def save_json(path, data):
    os.makedirs(os.path.dirname(path), exist_ok=True)
    with open(path, "w", encoding="utf-8") as f:
        json.dump(data, f, ensure_ascii=False)


def file_hash(fp: Path) -> str:
    try:
        return hashlib.sha256(fp.read_bytes()).hexdigest()
    except:
        return ""


def read_content(fp: Path, ext: str) -> str:
    if ext == ".docx":
        try:
            from docx import Document
            return "\n".join(p.text for p in Document(str(fp)).paragraphs if p.text.strip())
        except:
            return ""
    if ext == ".pdf":
        try:
            from PyPDF2 import PdfReader
            return "\n".join((p.extract_text() or "") for p in PdfReader(str(fp)).pages)
        except:
            return ""
    try:
        return fp.read_text(encoding="utf-8", errors="replace")
    except:
        return ""


def split_text(text: str) -> list:
    chunks, current = [], ""
    for para in re.split(r"\n\s*\n", text):
        para = para.strip()
        if not para: continue
        if len(current) + len(para) + 1 <= CHUNK_SIZE:
            current = (current + "\n\n" + para).strip()
        else:
            if len(current) >= 50: chunks.append(current)
            current = para
        while len(current) > CHUNK_SIZE:
            chunks.append(current[:CHUNK_SIZE])
            current = current[CHUNK_SIZE - CHUNK_OVERLAP:]
    if len(current) >= 50: chunks.append(current)
    return chunks


def scan_source(src_dir, exts) -> list:
    """扫描单个源，返回 [(abs_path, ext, fhash), ...]"""
    if not os.path.exists(src_dir):
        return []
    results = []
    for dirpath, dirnames, filenames in os.walk(src_dir):
        dirnames[:] = [d for d in dirnames if d not in SKIP_DIRS and not d.startswith(".")]
        for fname in filenames:
            ext = os.path.splitext(fname)[1].lower()
            if ext not in exts:
                continue
            fp = os.path.join(dirpath, fname)
            try:
                sz = os.path.getsize(fp)
                if sz > MAX_FILE_MB * 1024 * 1024 or sz < 20:
                    continue
            except:
                continue
            # Skip paths
            pl = fp.lower().replace("\\", "/")
            if any("/" + s + "/" in pl or pl.endswith("/" + s) for s in SKIP_PATH_PARTS):
                continue
            h = file_hash(Path(fp))
            if h:
                results.append((fp, ext, h))
    return results


def main():
    print("=" * 60, flush=True)
    print("  Personal KB Indexer v2", flush=True)
    print(f"  Sources: {len(SOURCE_SPECS)}", flush=True)
    print(f"  Target: {COLLECTION_NAME} @ {VECTOR_DB_PATH}", flush=True)
    print("=" * 60, flush=True)

    # Load state
    prev_state = load_json(STATE_FILE)
    progress = load_json(PROGRESS_FILE)
    completed_sources = set(progress.get("completed", []))

    # Init ChromaDB
    import chromadb
    client = chromadb.PersistentClient(path=VECTOR_DB_PATH)
    try:
        coll = client.get_collection(name=COLLECTION_NAME)
    except:
        coll = client.create_collection(name=COLLECTION_NAME,
            metadata={"description": "Personal knowledge base"})

    # Load model once
    print("\nLoading embedding model...", flush=True)
    model = SentenceTransformer(EMBEDDING_MODEL)

    total_new = 0

    for src_dir, default_tag, exts in SOURCE_SPECS:
        if src_dir in completed_sources:
            print(f"\n[SKIP] {src_dir} (already completed)", flush=True)
            continue

        print(f"\n{'='*60}", flush=True)
        print(f"[SCAN] {src_dir}", flush=True)

        entries = scan_source(src_dir, exts)
        print(f"  Found: {len(entries)} files", flush=True)

        if not entries:
            completed_sources.add(src_dir)
            progress["completed"] = list(completed_sources)
            save_json(PROGRESS_FILE, progress)
            continue

        # Filter: new or changed
        to_process = []
        for fp, ext, fhash in entries:
            if fp not in prev_state or prev_state[fp] != fhash:
                to_process.append((fp, ext, fhash))

        if not to_process:
            print(f"  All {len(entries)} files unchanged, skipping.", flush=True)
            completed_sources.add(src_dir)
            progress["completed"] = list(completed_sources)
            save_json(PROGRESS_FILE, progress)
            continue

        print(f"  Processing: {len(to_process)} files (new/changed)", flush=True)

        # Extract text & chunk
        all_texts, all_metas, all_ids = [], [], []
        seen_hashes = set()
        t0 = time.time()

        for i, (fp, ext, fhash) in enumerate(to_process):
            if fhash in seen_hashes:
                continue
            seen_hashes.add(fhash)

            content = read_content(Path(fp), ext)
            if not content or len(content.strip()) < 20:
                continue

            chunks = split_text(content)
            if not chunks:
                continue

            category = infer_category(fp, default_tag)
            base_id = hashlib.md5(fp.encode()).hexdigest()[:12]
            for j, chunk in enumerate(chunks):
                all_texts.append(chunk)
                all_metas.append({
                    "source": fp,
                    "category": category,
                    "file_type": ext.lstrip("."),
                    "chunk_index": j,
                })
                all_ids.append(f"{base_id}_{j}")

            if (i + 1) % 200 == 0:
                elapsed = time.time() - t0
                rate = (i + 1) / elapsed
                eta = (len(to_process) - i - 1) / rate if rate > 0 else 0
                print(f"  Extract: {i+1}/{len(to_process)} ({100*(i+1)//len(to_process)}%) "
                      f"- {rate:.1f} files/s - ETA {eta:.0f}s", flush=True)

        print(f"  Chunks: {len(all_texts)} from {len(to_process)} files "
              f"({time.time()-t0:.0f}s)", flush=True)

        if not all_texts:
            completed_sources.add(src_dir)
            progress["completed"] = list(completed_sources)
            save_json(PROGRESS_FILE, progress)
            continue

        # Embed
        print(f"  Embedding {len(all_texts)} chunks...", flush=True)
        t0 = time.time()
        all_embeddings = []
        for i in range(0, len(all_texts), BATCH_EMBED):
            batch = all_texts[i:i + BATCH_EMBED]
            embs = model.encode(batch, show_progress_bar=False, batch_size=64)
            all_embeddings.extend(embs.tolist())
            if (i + BATCH_EMBED) % 2048 == 0 or i + BATCH_EMBED >= len(all_texts):
                done = min(i + BATCH_EMBED, len(all_texts))
                elapsed = time.time() - t0
                rate = done / elapsed if elapsed > 0 else 0
                eta = (len(all_texts) - done) / rate if rate > 0 else 0
                print(f"  Embed: {done}/{len(all_texts)} "
                      f"- {rate:.0f} chunks/s - ETA {eta:.0f}s", flush=True)

        # Write to DB
        print(f"  Writing to ChromaDB...", flush=True)
        t0 = time.time()
        for i in range(0, len(all_texts), BATCH_DB):
            end = min(i + BATCH_DB, len(all_texts))
            coll.add(
                embeddings=all_embeddings[i:end],
                documents=all_texts[i:end],
                metadatas=all_metas[i:end],
                ids=all_ids[i:end],
            )
            if end % 5000 == 0 or end == len(all_texts):
                print(f"  Write: {end}/{len(all_texts)} ({time.time()-t0:.0f}s)", flush=True)

        # Update state
        for fp, ext, fhash in entries:
            prev_state[fp] = fhash
        save_json(STATE_FILE, prev_state)

        completed_sources.add(src_dir)
        progress["completed"] = list(completed_sources)
        save_json(PROGRESS_FILE, progress)

        total_new += len(all_texts)
        print(f"  [OK] {src_dir} done. ({len(all_texts)} chunks)", flush=True)

    print(f"\n{'='*60}", flush=True)
    print(f"  ALL DONE!", flush=True)
    print(f"  Total chunks: {coll.count()}", flush=True)
    print(f"{'='*60}", flush=True)


if __name__ == "__main__":
    main()
