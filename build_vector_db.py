#!/usr/bin/env python3
"""
冒险岛知识库向量化脚本 v2 — 优化版
- 使用轻量中文模型 paraphrase-multilingual-MiniLM-L12-v2（更快）
- 先嵌入后存储（批量编码，速度更快）
- 聚焦文档 + 脚本，跳过 Java 源码（太大）
- 实时刷新输出，进度可见

目标目录: d:/xy_vector_db
"""

import os
import re
import sys
import time
import chromadb
import shutil
from pathlib import Path
from sentence_transformers import SentenceTransformer

# ========================== 配置 ==========================
PROJECT_ROOT = Path("E:/pro/BeiDou-Server_xy")
VECTOR_DB_PATH = "d:/xy_vector_db"

# 用更轻量更快的中文模型 (110MB vs 400MB)
EMBEDDING_MODEL = "paraphrase-multilingual-MiniLM-L12-v2"

# 文本块大小
CHUNK_SIZE = 800
CHUNK_OVERLAP = 100

# 扫描范围 — 聚焦最有价值的内容
SCAN_SPECS = [
    # (目录, 文件类型, 描述)
    ("resource_doc", {".md", ".txt"}, "参考文档"),
    ("gms-server/scripts-zh-CN", {".js"}, "NPC/任务脚本"),
    ("gms-server/src/main/resources/db/migration", {".sql"}, "数据库结构"),
]

ROOT_FILES = ["CLAUDE.md", "README.md"]

# 跳过的子目录
SKIP_DIRS = {
    ".git", ".idea", ".claude", "__pycache__", "node_modules", ".codegraph",
    "target", "logs", "images", ".uploads", "wz-zh-CN", "proxy", "gms-ui",
}


def collect_files(root: Path) -> list:
    """收集文件"""
    files = []

    # 根目录文件
    for fname in ROOT_FILES:
        fp = root / fname
        if fp.exists():
            try:
                content = fp.read_text(encoding="utf-8", errors="replace")
                if len(content.strip()) >= 10:
                    files.append((fname, content))
                    print(f"  [ROOT] {fname}", flush=True)
            except Exception as e:
                print(f"  [SKIP] {fname}: {e}", flush=True)

    # 扫描指定目录
    for scan_dir, exts, desc in SCAN_SPECS:
        scan_path = root / scan_dir
        if not scan_path.exists():
            print(f"  [WARN] 目录不存在: {scan_dir}", flush=True)
            continue

        count = 0
        for dirpath, dirnames, filenames in os.walk(scan_path):
            dirnames[:] = [d for d in dirnames if d not in SKIP_DIRS]
            for fname in filenames:
                ext = os.path.splitext(fname)[1].lower()
                if ext not in exts:
                    continue
                full_path = Path(dirpath) / fname
                rel_path = full_path.relative_to(PROJECT_ROOT)
                try:
                    content = full_path.read_text(encoding="utf-8", errors="replace")
                    if len(content.strip()) >= 10:
                        files.append((str(rel_path), content))
                        count += 1
                except Exception as e:
                    print(f"  [SKIP] {rel_path}: {e}", flush=True)

        print(f"  [{desc}] {scan_dir}: {count} 个文件", flush=True)

    return files


def split_text(text: str) -> list:
    """分割文本为重叠块"""
    chunks = []
    paragraphs = re.split(r"\n\s*\n", text)
    current = ""

    for para in paragraphs:
        para = para.strip()
        if not para:
            continue
        if len(current) + len(para) + 1 <= CHUNK_SIZE:
            current = (current + "\n\n" + para).strip()
        else:
            if len(current) >= 50:
                chunks.append(current)
            current = para
        while len(current) > CHUNK_SIZE:
            chunks.append(current[:CHUNK_SIZE])
            current = current[CHUNK_SIZE - CHUNK_OVERLAP:]

    if len(current) >= 50:
        chunks.append(current)
    return chunks


def build_database():
    print("=" * 60, flush=True)
    print("  冒险岛知识库向量化构建 v2", flush=True)
    print(f"  模型: {EMBEDDING_MODEL}", flush=True)
    print(f"  目标: {VECTOR_DB_PATH}", flush=True)
    print("=" * 60, flush=True)

    # 1. 收集文件
    print("\n[1/5] 收集文件...", flush=True)
    all_files = collect_files(PROJECT_ROOT)
    print(f"  总计: {len(all_files)} 个文件\n", flush=True)

    if not all_files:
        print("[ERROR] 无文件!", flush=True)
        return

    # 2. 分割文本
    print("[2/5] 分割文本块...", flush=True)
    texts, metadatas, ids = [], [], []
    for rel_path, content in all_files:
        chunks = split_text(content)
        for i, chunk in enumerate(chunks):
            texts.append(chunk)
            metadatas.append({"source": rel_path, "chunk_index": i})
            ids.append(f"{rel_path}__{i}")
    print(f"  生成 {len(texts)} 个文本块", flush=True)

    # 3. 加载模型
    print(f"\n[3/5] 加载 embedding 模型 ({EMBEDDING_MODEL})...", flush=True)
    t0 = time.time()
    model = SentenceTransformer(EMBEDDING_MODEL)
    print(f"  模型加载完成 ({time.time() - t0:.1f}s)", flush=True)

    # 4. 批量向量化 (encode 批量处理比逐条快 10x+)
    print(f"\n[4/5] 批量向量化 {len(texts)} 条...", flush=True)
    t0 = time.time()
    BATCH = 512  # 每批 512 条，充分利用 CPU
    all_embeddings = []
    for i in range(0, len(texts), BATCH):
        batch = texts[i:i + BATCH]
        embeddings = model.encode(batch, show_progress_bar=False, batch_size=64)
        all_embeddings.extend(embeddings.tolist())
        if (i + BATCH) % 2048 == 0 or i + BATCH >= len(texts):
            pct = min(i + BATCH, len(texts)) * 100 // len(texts)
            elapsed = time.time() - t0
            rate = min(i + BATCH, len(texts)) / elapsed
            eta = (len(texts) - min(i + BATCH, len(texts))) / rate if rate > 0 else 0
            print(f"  进度: {min(i+BATCH, len(texts))}/{len(texts)} ({pct}%) "
                  f"— {rate:.0f}条/秒 — 预计剩余 {eta:.0f}秒", flush=True)

    print(f"  向量化完成 ({time.time() - t0:.1f}s)", flush=True)

    # 5. 写入 ChromaDB
    print(f"\n[5/5] 写入 ChromaDB...", flush=True)
    if os.path.exists(VECTOR_DB_PATH):
        print(f"  清理旧库: {VECTOR_DB_PATH}", flush=True)
        shutil.rmtree(VECTOR_DB_PATH)

    client = chromadb.PersistentClient(path=VECTOR_DB_PATH)
    collection = client.create_collection(
        name="maplestory_kb",
        metadata={"description": "冒险岛(MapleStory)服务器开发知识库"},
    )

    # 手动传入 embeddings，不再使用 ChromaDB 的内置 embedding function
    t0 = time.time()
    DB_BATCH = 500
    for i in range(0, len(texts), DB_BATCH):
        end = min(i + DB_BATCH, len(texts))
        collection.add(
            embeddings=all_embeddings[i:end],
            documents=texts[i:end],
            metadatas=metadatas[i:end],
            ids=ids[i:end],
        )
        if end % 5000 == 0 or end == len(texts):
            print(f"  写入: {end}/{len(texts)} ({time.time() - t0:.0f}s)", flush=True)

    print(f"\n{'=' * 60}", flush=True)
    print("  [OK] Build complete!", flush=True)
    print(f"  文档块: {collection.count()}", flush=True)
    print(f"  存储路径: {VECTOR_DB_PATH}", flush=True)
    print(f"{'=' * 60}", flush=True)


if __name__ == "__main__":
    build_database()
