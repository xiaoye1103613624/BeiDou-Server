# -*- coding: utf-8 -*-
"""Append equip-slot wrong/correct lessons into D:\\xy_vector_db maplestory_kb.

Also mirrors JSONL under resource_doc/agent_memory and xiaoye agent_memory.
Uses same embedding model as ingest_gms083_mistakes.py.
"""
from __future__ import annotations

import argparse
import json
import shutil
import sys
from pathlib import Path
from typing import Any

from sentence_transformers import SentenceTransformer
import chromadb

EMBEDDING_MODEL = "paraphrase-multilingual-MiniLM-L12-v2"
DB_PATH = r"D:\xy_vector_db"
COLLECTION = "maplestory_kb"
PROJECT = "GMS083"
SOURCE = "equip_slot_lessons.jsonl"
STAMP = "RING_34_BIND_SAFE_20260726ac"

REPO_JSONL = Path(r"E:\pro\BeiDou-Server_xy\resource_doc\agent_memory\equip_slot_lessons.jsonl")
XIAOYE_JSONL = Path(r"E:\资料\xiaoye\mxd学习\萧曳冒险岛\agent_memory\equip_slot_lessons.jsonl")
INDEX_MD = Path(r"E:\pro\BeiDou-Server_xy\resource_doc\agent_memory\equip_slot_lessons_index.md")


def load_entries(path: Path) -> list[dict[str, Any]]:
    entries: list[dict[str, Any]] = []
    for line in path.read_text(encoding="utf-8").splitlines():
        line = line.strip()
        if not line:
            continue
        entries.append(json.loads(line))
    return entries


def format_document(e: dict[str, Any]) -> str:
    return (
        f"标题: {e['summary']}\n"
        f"种类: {e['kind']}\n"
        f"标签: {','.join(e.get('tags') or [])}\n"
        f"详情: {e['detail']}\n"
        f"戳记: {e.get('stamp', '')}\n"
        f"日期: {e.get('date', '')}\n"
        f"项目: {PROJECT}\n"
        f"主题: 装备栏/第二坠/六戒/红3红4\n"
    )


def write_index(entries: list[dict[str, Any]]) -> None:
    lines = [
        "# 装备槽错误/正确行为索引",
        "",
        f"戳记参考：`{STAMP}`  |  向量库：`{DB_PATH}` / `{COLLECTION}`",
        "",
        "| id | kind | summary | stamp |",
        "|----|------|---------|-------|",
    ]
    for e in entries:
        lines.append(
            f"| `{e['id']}` | {e['kind']} | {e['summary']} | `{e.get('stamp','')}` |"
        )
    lines.append("")
    INDEX_MD.write_text("\n".join(lines), encoding="utf-8")


def main() -> int:
    parser = argparse.ArgumentParser()
    parser.add_argument("--dry-run", action="store_true")
    parser.add_argument("--skip-chroma", action="store_true", help="Only mirror JSONL/MD")
    args = parser.parse_args()

    if not REPO_JSONL.exists():
        print("missing", REPO_JSONL, file=sys.stderr)
        return 1

    entries = load_entries(REPO_JSONL)
    XIAOYE_JSONL.parent.mkdir(parents=True, exist_ok=True)
    shutil.copy2(REPO_JSONL, XIAOYE_JSONL)
    write_index(entries)
    print(f"Loaded {len(entries)} lessons; mirrored to {XIAOYE_JSONL}")
    print(f"Wrote index {INDEX_MD}")

    if args.dry_run or args.skip_chroma:
        for e in entries:
            print(f"  {e['id']} [{e['kind']}] {e['summary']}")
        return 0

    documents = [format_document(e) for e in entries]
    ids = [e["id"] for e in entries]
    metadatas = [
        {
            "category": "装备栏/第二坠/六戒",
            "title": e["summary"][:200],
            "kind": e["kind"],
            "project": PROJECT,
            "source": SOURCE,
            "type": "mistake_lesson" if e["kind"] == "wrong" else "correct_lesson",
            "stamp": e.get("stamp") or STAMP,
            "tags": ",".join(e.get("tags") or []),
        }
        for e in entries
    ]

    print(f"Loading embedding model {EMBEDDING_MODEL}...")
    model = SentenceTransformer(EMBEDDING_MODEL)
    embeddings = model.encode(documents, show_progress_bar=True, normalize_embeddings=True)
    embeddings = [x.tolist() for x in embeddings]

    print(f"Connecting Chroma @ {DB_PATH}...")
    client = chromadb.PersistentClient(path=DB_PATH)
    col = client.get_collection(COLLECTION)
    col.upsert(ids=ids, documents=documents, metadatas=metadatas, embeddings=embeddings)
    print(f"Upserted {len(ids)}: {ids[0]} .. {ids[-1]}")

    got = col.get(ids=["equip_wrong_04", "equip_correct_04"], include=["metadatas"])
    for i, doc_id in enumerate(got["ids"]):
        print("spot-check", doc_id, got["metadatas"][i].get("title"))
    return 0


if __name__ == "__main__":
    sys.exit(main())
