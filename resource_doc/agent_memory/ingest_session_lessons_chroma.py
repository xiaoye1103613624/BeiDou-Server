# -*- coding: utf-8 -*-
"""Fresh Chroma for cross-session lessons (sibling of equip_slot_chroma; maplestory_kb write path unhealthy)."""
from __future__ import annotations

import json
import shutil
from pathlib import Path

from sentence_transformers import SentenceTransformer
import chromadb

JSONL = Path(r"E:\pro\BeiDou-Server_xy\resource_doc\agent_memory\session_lessons.jsonl")
STORE = Path(r"E:\pro\BeiDou-Server_xy\resource_doc\agent_memory\session_chroma")
XY_STORE = Path(r"D:\xy_vector_db\session_chroma")
XIAOYE_STORE = Path(r"E:\资料\xiaoye\mxd学习\萧曳冒险岛\agent_memory\session_chroma")
COLLECTION = "session_lessons"
MODEL = "paraphrase-multilingual-MiniLM-L12-v2"


def fmt(e: dict) -> str:
    return (
        f"标题: {e['summary']}\n种类: {e['kind']}\n"
        f"标签: {','.join(e.get('tags') or [])}\n详情: {e['detail']}\n"
        f"戳记: {e.get('stamp','')}\n日期: {e.get('date','')}\n"
        f"项目: GMS083\n主题: 会话修改/踩坑\n"
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
            "category": "session",
            "title": e["summary"][:180],
            "kind": e["kind"],
            "project": "GMS083",
            "source": "session_lessons.jsonl",
            "type": "mistake_lesson" if e["kind"] == "wrong" else "correct_lesson",
            "stamp": str(e.get("stamp") or ""),
        }
        for e in entries
    ]
    model = SentenceTransformer(MODEL)
    emb = [x.tolist() for x in model.encode(docs, normalize_embeddings=True)]
    col.add(ids=ids, documents=docs, metadatas=metas, embeddings=emb)
    assert len(col.get(ids=ids, include=[])["ids"]) == len(ids)
    r = col.query(query_texts=["1121015 双击没效果 brandish"], n_results=min(3, len(ids)))
    print(path, "count", col.count(), "top", r["ids"][0])


def main() -> int:
    entries = [json.loads(l) for l in JSONL.read_text(encoding="utf-8").splitlines() if l.strip()]
    print("entries", len(entries))
    ingest_into(STORE, entries)
    ingest_into(XY_STORE, entries)
    if XIAOYE_STORE.parent.exists():
        ingest_into(XIAOYE_STORE, entries)
    print("SESSION_CHROMA_OK")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
