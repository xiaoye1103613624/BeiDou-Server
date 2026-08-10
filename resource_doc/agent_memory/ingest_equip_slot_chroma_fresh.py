# -*- coding: utf-8 -*-
"""Create a fresh local Chroma for equip-slot lessons (maplestory_kb write path is unhealthy)."""
from __future__ import annotations

import json
import shutil
from pathlib import Path

from sentence_transformers import SentenceTransformer
import chromadb

JSONL = Path(r"E:\pro\BeiDou-Server_xy\resource_doc\agent_memory\equip_slot_lessons.jsonl")
STORE = Path(r"E:\pro\BeiDou-Server_xy\resource_doc\agent_memory\chroma")
XIAOYE_STORE = Path(r"E:\资料\xiaoye\mxd学习\萧曳冒险岛\agent_memory\chroma")
COLLECTION = "equip_slot_lessons"
MODEL = "paraphrase-multilingual-MiniLM-L12-v2"


def fmt(e):
    return (
        f"标题: {e['summary']}\n种类: {e['kind']}\n"
        f"标签: {','.join(e.get('tags') or [])}\n详情: {e['detail']}\n"
        f"戳记: {e.get('stamp','')}\n日期: {e.get('date','')}\n"
        f"项目: GMS083\n主题: 装备栏/第二坠/六戒\n"
    )


def ingest_into(path: Path, entries: list) -> None:
    if path.exists():
        shutil.rmtree(path)
    path.mkdir(parents=True, exist_ok=True)
    client = chromadb.PersistentClient(path=str(path))
    col = client.create_collection(COLLECTION)
    docs = [fmt(e) for e in entries]
    ids = [e["id"] for e in entries]
    metas = [
        {
            "category": "equip_slot",
            "title": e["summary"][:180],
            "kind": e["kind"],
            "project": "GMS083",
            "source": "equip_slot_lessons.jsonl",
            "type": "mistake_lesson" if e["kind"] == "wrong" else "correct_lesson",
            "stamp": str(e.get("stamp") or ""),
        }
        for e in entries
    ]
    model = SentenceTransformer(MODEL)
    emb = [x.tolist() for x in model.encode(docs, normalize_embeddings=True)]
    col.add(ids=ids, documents=docs, metadatas=metas, embeddings=emb)
    got = col.get(ids=ids, include=["metadatas"])
    assert len(got["ids"]) == len(ids), got["ids"]
    # query smoke
    r = col.query(query_texts=["E开装备栏卡死 ebp-14"], n_results=3)
    print(path, "count", col.count(), "top", r["ids"][0])


def main() -> int:
    entries = [json.loads(l) for l in JSONL.read_text(encoding="utf-8").splitlines() if l.strip()]
    print("entries", len(entries))
    ingest_into(STORE, entries)
    # mirror for xiaoye notes tree
    if XIAOYE_STORE.parent.exists():
        ingest_into(XIAOYE_STORE, entries)
    # also place under D:\xy_vector_db as sibling healthy store
    ingest_into(Path(r"D:\xy_vector_db\equip_slot_chroma"), entries)
    print("FRESH_CHROMA_OK")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
