#!/usr/bin/env python3
"""
Incremental vector DB update script.
Checks for new/changed files since last build, only embeds delta.
Triggered automatically by PreCompact hook.

Usage:
  python update_vector_db.py          # Incremental update
  python update_vector_db.py --full   # Force full rebuild
"""

import os, re, sys, time, json, hashlib, chromadb, shutil
from pathlib import Path
from sentence_transformers import SentenceTransformer

# Configuration
PROJECT_ROOT = Path("E:/pro/BeiDou-Server_xy")
VECTOR_DB_PATH = "d:/xy_vector_db"
STATE_FILE = os.path.join(VECTOR_DB_PATH, ".kb_state.json")
EMBEDDING_MODEL = "paraphrase-multilingual-MiniLM-L12-v2"
COLLECTION_NAME = "maplestory_kb"

CHUNK_SIZE = 800
CHUNK_OVERLAP = 100

SCAN_SPECS = [
    ("resource_doc", {".md", ".txt"}, "docs"),
    ("gms-server/scripts-zh-CN", {".js"}, "scripts"),
    ("gms-server/src/main/resources/db/migration", {".sql"}, "db"),
]
ROOT_FILES = ["CLAUDE.md", "README.md"]
SKIP_DIRS = {
    ".git", ".idea", ".claude", "__pycache__", "node_modules", ".codegraph",
    "target", "logs", "images", ".uploads", "wz-zh-CN", "proxy", "gms-ui",
}


def get_file_hash(filepath):
    """SHA256 hash of file content (used to detect changes)."""
    try:
        return hashlib.sha256(filepath.read_bytes()).hexdigest()
    except Exception:
        return None


def collect_files():
    """Collect all source files with their hashes."""
    files = {}

    # Root files
    for fname in ROOT_FILES:
        fp = PROJECT_ROOT / fname
        if fp.exists():
            h = get_file_hash(fp)
            if h:
                files[fname] = h

    # Scan directories
    for scan_dir, exts, _desc in SCAN_SPECS:
        scan_path = PROJECT_ROOT / scan_dir
        if not scan_path.exists():
            continue
        for dirpath, dirnames, filenames in os.walk(scan_path):
            dirnames[:] = [d for d in dirnames if d not in SKIP_DIRS]
            for fname in filenames:
                ext = os.path.splitext(fname)[1].lower()
                if ext not in exts:
                    continue
                full_path = Path(dirpath) / fname
                rel_path = str(full_path.relative_to(PROJECT_ROOT))
                h = get_file_hash(full_path)
                if h:
                    files[rel_path] = h
    return files


def load_state():
    """Load last-known state {filepath: hash}."""
    if os.path.exists(STATE_FILE):
        with open(STATE_FILE, "r", encoding="utf-8") as f:
            return json.load(f)
    return {}


def save_state(state):
    """Persist state."""
    os.makedirs(os.path.dirname(STATE_FILE), exist_ok=True)
    with open(STATE_FILE, "w", encoding="utf-8") as f:
        json.dump(state, f, ensure_ascii=False, indent=2)


def split_text(text):
    """Split text into overlapping chunks."""
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


def incremental_update():
    """Main: detect changes and update only what's needed."""
    print("[kb-update] Scanning for changes...", flush=True)

    current_state = collect_files()
    prev_state = load_state()

    if not prev_state:
        # First run — do full build
        print("[kb-update] First run, triggering full rebuild...", flush=True)
        return full_rebuild()

    # Find added, modified, and deleted files
    added = {p: h for p, h in current_state.items() if p not in prev_state}
    modified = {p: h for p, h in current_state.items()
                if p in prev_state and prev_state[p] != h}
    deleted = [p for p in prev_state if p not in current_state]

    changed = list(added.keys()) + list(modified.keys())

    if not changed and not deleted:
        print("[kb-update] No changes detected, skipping.", flush=True)
        return

    print(f"[kb-update] New: {len(added)}, Modified: {len(modified)}, "
          f"Deleted: {len(deleted)}", flush=True)

    # Load ChromaDB
    client = chromadb.PersistentClient(path=VECTOR_DB_PATH)
    collection = client.get_collection(name=COLLECTION_NAME)

    # Remove deleted files' chunks
    if deleted:
        for path in deleted:
            try:
                # Find and delete chunks for this file
                existing = collection.get(
                    where={"source": path},
                    include=[],
                )
                if existing["ids"]:
                    collection.delete(ids=existing["ids"])
                    print(f"  [del] {path} ({len(existing['ids'])} chunks)", flush=True)
            except Exception as e:
                print(f"  [warn] Failed to delete {path}: {e}", flush=True)

    # Remove old chunks for modified files
    for path in modified:
        try:
            existing = collection.get(
                where={"source": path},
                include=[],
            )
            if existing["ids"]:
                collection.delete(ids=existing["ids"])
        except Exception:
            pass

    # Add new/modified files
    if changed:
        model = SentenceTransformer(EMBEDDING_MODEL)
        for path in changed:
            full_path = PROJECT_ROOT / path
            try:
                content = full_path.read_text(encoding="utf-8", errors="replace")
                if len(content.strip()) < 10:
                    continue
            except Exception as e:
                print(f"  [skip] {path}: {e}", flush=True)
                continue

            chunks = split_text(content)
            if not chunks:
                continue

            embeddings = model.encode(chunks, show_progress_bar=False).tolist()
            ids = [f"{path}__{i}" for i in range(len(chunks))]
            metadatas = [{"source": path, "chunk_index": i} for i in range(len(chunks))]

            collection.add(
                embeddings=embeddings,
                documents=chunks,
                metadatas=metadatas,
                ids=ids,
            )
            print(f"  [+{len(chunks)}] {path}", flush=True)

    # Save state
    save_state(current_state)
    print(f"[kb-update] Done. Total chunks: {collection.count()}", flush=True)


def full_rebuild():
    """Trigger a full rebuild (expensive, only on first run)."""
    import subprocess
    print("[kb-update] Running full rebuild...", flush=True)
    build_script = PROJECT_ROOT / "build_vector_db.py"
    subprocess.run([sys.executable, str(build_script)], check=False)


if __name__ == "__main__":
    if "--full" in sys.argv:
        full_rebuild()
    else:
        incremental_update()
