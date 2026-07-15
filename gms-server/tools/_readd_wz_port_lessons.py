# -*- coding: utf-8 -*-
"""Force delete+add wzport lessons so they land in HNSW after queue repair."""
from __future__ import annotations

import sys
from pathlib import Path

import chromadb
from sentence_transformers import SentenceTransformer

# reuse entry definitions from ingest script
sys.path.insert(0, str(Path(__file__).resolve().parent))
from _ingest_wz_port_lessons_20260715 import (  # noqa: E402
    COLLECTION,
    DB_PATH,
    EMBEDDING_MODEL,
    ENTRIES,
    MD_PATH,
    format_document,
    PROJECT,
    SOURCE,
)


def main() -> int:
    ids = [e["id"] for e in ENTRIES] + ["gms083_wzport_20260715_doc"]
    full_text = MD_PATH.read_text(encoding="utf-8")
    documents = [format_document(e) for e in ENTRIES] + [
        f"全文经验文档\n项目: {PROJECT}\n路径: {MD_PATH}\n\n{full_text[:6000]}"
    ]
    metadatas = [
        {
            "category": e["category"],
            "title": e["title"],
            "project": PROJECT,
            "source": SOURCE,
            "type": "wz_port_lesson",
            "date": "20260715",
        }
        for e in ENTRIES
    ] + [
        {
            "category": "WZ移植/文档",
            "title": "wz_port_lessons_20260715 全文",
            "project": PROJECT,
            "source": SOURCE,
            "type": "wz_port_lesson_doc",
            "date": "20260715",
        }
    ]

    print("embed...")
    model = SentenceTransformer(EMBEDDING_MODEL)
    embeddings = model.encode(documents, show_progress_bar=True, normalize_embeddings=True)
    embeddings = [e.tolist() for e in embeddings]

    client = chromadb.PersistentClient(path=DB_PATH)
    col = client.get_collection(COLLECTION)
    print("count_before", col.count())
    try:
        col.delete(ids=ids)
        print("deleted", ids)
    except Exception as e:
        print("delete_warn", type(e).__name__, e)

    col.add(ids=ids, documents=documents, metadatas=metadatas, embeddings=embeddings)
    print("added", len(ids))
    got = col.get(ids=["gms083_wzport_20260715_03"], include=["metadatas", "documents"])
    print("got", got["ids"], (got.get("metadatas") or [{}])[0].get("title"))
    # try embeddings include
    try:
        ge = col.get(ids=["gms083_wzport_20260715_03"], include=["embeddings"])
        print("emb_dim", len((ge.get("embeddings") or [[]])[0]))
    except Exception as e:
        print("emb_get_fail", type(e).__name__, e)
    # ANN
    emb = model.encode(["ExpDecode8 mismatch ijl15.dll error 38"], normalize_embeddings=True)[0].tolist()
    res = col.query(query_embeddings=[emb], n_results=20, include=["metadatas"])
    hits = [i for i in res["ids"][0] if "wzport" in i]
    print("ann_hits", hits)
    print("count_after", col.count())
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
