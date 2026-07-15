# -*- coding: utf-8 -*-
"""Ingest 2026-07-15 WZ port lessons into Chroma maplestory_kb."""
from __future__ import annotations

import sys
from pathlib import Path

import chromadb
from sentence_transformers import SentenceTransformer

EMBEDDING_MODEL = "paraphrase-multilingual-MiniLM-L12-v2"
DB_PATH = r"D:\xy_vector_db"
COLLECTION = "maplestory_kb"
PROJECT = "GMS083"
SOURCE = "wz_port_lessons_20260715.md"
MD_PATH = Path(r"E:\pro\BeiDou-Server_xy\.eval_append\wz_port_lessons_20260715.md")

ENTRIES = [
    {
        "id": "gms083_wzport_20260715_01",
        "category": "WZ移植/工具链",
        "title": "CMS→GMS 转密经 Orz :10002，Cursor beidou-build 不可用时仍可 append",
        "wrong": "依赖 Cursor beidou-build MCP；或直接把 CMS packed WZ 覆盖到 GMS Data。",
        "correct": "Append-only：缺文件可转密后写入；同文件节点 SKIP 粘贴。工具：OrzRepacker/orange-wz HTTP MCP http://127.0.0.1:10002。GMS IV=TSPHKw==。",
        "symptom": "IMG 打不开、解密失败、进图异常。",
        "detect": "Orz initialize 200；用 083-GMS key 能 parse Data/*.img。",
    },
    {
        "id": "gms083_wzport_20260715_02",
        "category": "WZ移植/范围",
        "title": "V095 PLUS2 / 适用于083时装 / 要爱V104 → Client_1：时装与武器可搬，高版本 Skill/Effect 勿整包",
        "wrong": "整包端口径移植 Skill 根、巨型 SetEff、Direction4+、高版本 Effect。",
        "correct": "时装 Character、武器节点、Map/Mob 文件级缺项可 append；Skill/Effect 高版本因 common vs level/N schema 不兼容，可行性低，保持隔离。",
        "symptom": "EC_INVALID_GAME_DATA 或启动失败。",
        "detect": "对照 .eval_append 批次日志；检查 .bad/.bad2 隔离文件。",
    },
    {
        "id": "gms083_wzport_20260715_03",
        "category": "WZ移植/故障",
        "title": "EC_INVALID_GAME_DATA 是本地 WZ 语义问题，不是服务器不同步",
        "wrong": "当成服务端地图/掉落不同步去改服务器。",
        "correct": "排查近期合并的 UI/Effect/Skill；将问题包隔离为 .bad / .bad2；保留检疫痕迹，勿与服务器混为一谈。",
        "symptom": "客户端报 EC_INVALID_GAME_DATA。",
        "detect": "回滚/改名近期 IMG；对比隔离前后能否进图。",
    },
    {
        "id": "gms083_wzport_20260715_04",
        "category": "客户端插件/解码",
        "title": "error 38 ExpDecode8 mismatch 用新 ijl15.dll 修复",
        "wrong": "把 error 38 当成 WZ 损坏去大规模回滚 Data。",
        "correct": "部署匹配 hook 链的新版 ijl15.dll（与 BeiDou-Client_1 联调版本一致）。",
        "symptom": "error 38 / ExpDecode8 mismatch。",
        "detect": "核对 ijl15.dll 大小与构建时间；确认 PostBuild 到 Client_1。",
    },
    {
        "id": "gms083_wzport_20260715_05",
        "category": "WZ移植/跳过项",
        "title": "已知跳过：Cash 05010156-89、Direction4 Sound、Radio placeholder",
        "wrong": "强制补齐上述缺口导致二次污染。",
        "correct": "记录跳过并继续其它安全面；日志在 .eval_append/。全文见 wz_port_lessons_20260715.md。",
        "symptom": "无（主动跳过）。",
        "detect": "检索向量库 gms083_wzport_20260715_* 或读 .eval_append 文档。",
    },
]


def format_document(entry: dict) -> str:
    return (
        f"标题: {entry['title']}\n"
        f"分类: {entry['category']}\n"
        f"错误做法: {entry['wrong']}\n"
        f"正确做法: {entry['correct']}\n"
        f"症状: {entry['symptom']}\n"
        f"检测: {entry['detect']}\n"
        f"项目: {PROJECT}\n"
        f"文档: {MD_PATH}\n"
    )


def main() -> int:
    if not MD_PATH.is_file():
        print("missing md", MD_PATH)
        return 1

    # Also store a full-doc chunk for markdown retrieval
    full_text = MD_PATH.read_text(encoding="utf-8")
    ids = [e["id"] for e in ENTRIES] + ["gms083_wzport_20260715_doc"]
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

    print(f"Prepared {len(ids)} entries for {COLLECTION} @ {DB_PATH}")
    print(f"Loading embedding model {EMBEDDING_MODEL}...")
    model = SentenceTransformer(EMBEDDING_MODEL)
    embeddings = model.encode(documents, show_progress_bar=True, normalize_embeddings=True)
    embeddings = [e.tolist() for e in embeddings]

    client = chromadb.PersistentClient(path=DB_PATH)
    col = client.get_collection(COLLECTION)
    col.upsert(ids=ids, documents=documents, metadatas=metadatas, embeddings=embeddings)
    print(f"Upserted {len(ids)} ids into {COLLECTION}")
    got = col.get(ids=["gms083_wzport_20260715_03"], include=["metadatas"])
    print("spot-check:", (got.get("metadatas") or [{}])[0].get("title"))
    print(f"collection_count={col.count()} path={DB_PATH}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
