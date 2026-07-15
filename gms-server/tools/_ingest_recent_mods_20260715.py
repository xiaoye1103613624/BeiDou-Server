# -*- coding: utf-8 -*-
"""Ingest recent mods + error_ops lessons into Chroma maplestory_kb."""
from __future__ import annotations

import sys
from pathlib import Path

import chromadb
from sentence_transformers import SentenceTransformer

EMBEDDING_MODEL = "paraphrase-multilingual-MiniLM-L12-v2"
DB_PATH = r"D:\xy_vector_db"
COLLECTION = "maplestory_kb"
PROJECT = "GMS083"

ERROR_OPS_MD = Path(r"E:\pro\BeiDou-Server_xy\.eval_append\error_ops_lessons_20260715.md")
RECENT_MD = Path(r"E:\pro\BeiDou-Server_xy\.eval_append\recent_mods_summary_20260715.md")
WZ_MD = Path(r"E:\pro\BeiDou-Server_xy\.eval_append\wz_port_lessons_20260715.md")

ERROR_OPS = [
    {
        "id": "gms083_error_ops_20260715_01",
        "category": "错误操作/WZ",
        "title": "EC_INVALID 来自整包 dump V095/V104 Skill/Effect，隔离 .bad/.bad2",
        "wrong": "整包端口径移植高版本 Skill/Effect。",
        "correct": "时装/缺文件可 append；Skill/Effect 立即隔离 .bad/.bad2，勿当服务端不同步。",
        "symptom": "EC_INVALID_GAME_DATA",
        "detect": "回滚/改名近期 Effect/Skill；对照检疫文件。",
    },
    {
        "id": "gms083_error_ops_20260715_02",
        "category": "错误操作/封包",
        "title": "error 38 = ExpDecode8 服务端 writeLong 与客户端 Decode8 不对齐",
        "wrong": "当成 WZ 损坏大规模回滚 Data。",
        "correct": "服务端 writeLong(exp)+writeShort(level)；部署匹配的 ijl15 level300 Decode8。",
        "symptom": "error 38 / ExpDecode8 mismatch",
        "detect": "核对 PacketCreator 与 ezorsia/level300；DLL 与 Client_1 一致。",
    },
    {
        "id": "gms083_error_ops_20260715_03",
        "category": "错误操作/XML",
        "title": "Item XML 截断缺 </imgdir>（0400/0403）",
        "wrong": "append/sync 后不验闭合就开服。",
        "correct": "检查 0400/0403 等文件尾根闭合标签再同步双端。",
        "symptom": "truncated / 道具解析失败",
        "detect": "文件末行 imgdir；解析器日志。",
    },
    {
        "id": "gms083_error_ops_20260715_04",
        "category": "错误操作/插件",
        "title": "HigherShopList 误改 tab WIDTH；avatar flag 100≠127",
        "wrong": "把 CCtrlTab a7(WIDTH) 当高度；把 avatar Create flag 100 改成 127。",
        "correct": "只扩列表/滚动条高度；黑名单地址见 highershoplist.cpp 注释。",
        "symptom": "Tab 错位；卖栏角色漂浮",
        "detect": "IDA 核对 CCtrlTab/CAvatar 实参。",
    },
    {
        "id": "gms083_error_ops_20260715_05",
        "category": "错误操作/工具",
        "title": "Cursor beidou MCP down 时改用 Orz :10002",
        "wrong": "硬卡 beidou-build MCP。",
        "correct": "OrzRepacker/orange-wz HTTP http://127.0.0.1:10002；GMS IV TSPHKw==。",
        "symptom": "MCP 超时；移植停摆",
        "detect": "Orz initialize 200。",
    },
    {
        "id": "gms083_error_ops_20260715_06",
        "category": "错误操作/Skill",
        "title": "高版本 Skill common→level 不可自动移植",
        "wrong": "假设节点粘贴可自动迁 schema。",
        "correct": "schema 不兼容则 SKIP；仅文件级缺项谨慎评估。",
        "symptom": "EC_INVALID / 技能崩溃",
        "detect": "对比 Skill 根结构 common vs level/N。",
    },
]

RECENT_MODS = [
    {
        "id": "gms083_recent_mods_20260715_01",
        "category": "模组/服务端",
        "title": "灵韵觉醒 Spirit awaken + 装备附加技字段",
        "summary": "org.gms.spirit.*；Equip equipSkill*；V1.11.18；NPC 灵韵觉醒；0402/0446；交易清空灵韵。",
    },
    {
        "id": "gms083_recent_mods_20260715_02",
        "category": "模组/服务端",
        "title": "天赋 Talent 系统战斗挂钩",
        "summary": "org.gms.talent.*；闪避减伤刷怪倍率；NPCConversationManager 桥；V1.11.17。",
    },
    {
        "id": "gms083_recent_mods_20260715_03",
        "category": "模组/双端",
        "title": "Level300 / ExpDecode8 双端对齐",
        "summary": "服务端 ExpTable 300 + writeLong/writeShort；插件 level300 Decode8/2；V1.11.20。",
    },
    {
        "id": "gms083_recent_mods_20260715_04",
        "category": "模组/双端",
        "title": "PartyBuffs / Tracker + CUSTOM_PACKET 0x3713",
        "summary": "Party/World 快照；CustomPacketHandler；插件 PartyBuffs；V1.11.16。",
    },
    {
        "id": "gms083_recent_mods_20260715_05",
        "category": "模组/插件",
        "title": "HigherShopList 5→9 与 PatchStorageBg",
        "summary": "勿改 tab WIDTH / avatar flag 100；商店背板 +160。",
    },
    {
        "id": "gms083_recent_mods_20260715_06",
        "category": "模组/插件",
        "title": "MaxHpMp / BuffTimer / SlotLock / CharSlots / EquipCompare / PersonalShop",
        "summary": "HPMP Decode4；BuffTimer LevelNo 回退；槽锁合并；选角30；装备对比；个人店32。",
    },
    {
        "id": "gms083_recent_mods_20260715_07",
        "category": "模组/WZ",
        "title": "遗忘山谷 append + 0400/0403 闭合修复",
        "summary": "Map0 010006xxx、Mob/Npc/String；入口 portal；报告 valley_port_report_20260715.md。",
    },
    {
        "id": "gms083_recent_mods_20260715_08",
        "category": "模组/WZ",
        "title": "高版本怪物卡 Consume0238 与 SQL",
        "summary": "V1.11.19/21/22；Consume/String；缺失卡掉落补齐。",
    },
]


def fix_typo_ids():
    for e in RECENT_MODS:
        if "tods" in e["id"]:
            e["id"] = "gms083_recent_mods_20260715_08"


def format_error(e: dict) -> str:
    return (
        f"标题: {e['title']}\n分类: {e['category']}\n"
        f"错误做法: {e['wrong']}\n正确做法: {e['correct']}\n"
        f"症状: {e['symptom']}\n检测: {e['detect']}\n项目: {PROJECT}\n"
    )


def format_mod(e: dict) -> str:
    return (
        f"标题: {e['title']}\n分类: {e['category']}\n"
        f"摘要: {e['summary']}\n项目: {PROJECT}\n日期: 20260715\n"
    )


def main() -> int:
    fix_typo_ids()
    for p in (ERROR_OPS_MD, RECENT_MD):
        if not p.is_file():
            print("missing", p)
            return 1

    ids: list[str] = []
    documents: list[str] = []
    metadatas: list[dict] = []

    for e in ERROR_OPS:
        ids.append(e["id"])
        documents.append(format_error(e))
        metadatas.append(
            {
                "category": e["category"],
                "title": e["title"],
                "project": PROJECT,
                "source": "error_ops_lessons_20260715.md",
                "type": "error_ops_lesson",
                "date": "20260715",
            }
        )

    ids.append("gms083_error_ops_20260715_doc")
    documents.append(
        f"全文错误操作经验\n项目: {PROJECT}\n路径: {ERROR_OPS_MD}\n\n"
        + ERROR_OPS_MD.read_text(encoding="utf-8")[:6000]
    )
    metadatas.append(
        {
            "category": "错误操作/文档",
            "title": "error_ops_lessons_20260715 全文",
            "project": PROJECT,
            "source": "error_ops_lessons_20260715.md",
            "type": "error_ops_doc",
            "date": "20260715",
        }
    )

    for e in RECENT_MODS:
        ids.append(e["id"])
        documents.append(format_mod(e))
        metadatas.append(
            {
                "category": e["category"],
                "title": e["title"],
                "project": PROJECT,
                "source": "recent_mods_summary_20260715.md",
                "type": "recent_mod",
                "date": "20260715",
            }
        )

    ids.append("gms083_recent_mods_20260715_doc")
    documents.append(
        f"全文近期模组汇总\n项目: {PROJECT}\n路径: {RECENT_MD}\n\n"
        + RECENT_MD.read_text(encoding="utf-8")[:6000]
    )
    metadatas.append(
        {
            "category": "模组/文档",
            "title": "recent_mods_summary_20260715 全文",
            "project": PROJECT,
            "source": "recent_mods_summary_20260715.md",
            "type": "recent_mod_doc",
            "date": "20260715",
        }
    )

    # Refresh valley note into wz lessons doc id if md extended
    if WZ_MD.is_file():
        ids.append("gms083_wzport_20260715_doc")
        documents.append(
            f"全文经验文档\n项目: {PROJECT}\n路径: {WZ_MD}\n\n"
            + WZ_MD.read_text(encoding="utf-8")[:6000]
        )
        metadatas.append(
            {
                "category": "WZ移植/文档",
                "title": "wz_port_lessons_20260715 全文",
                "project": PROJECT,
                "source": "wz_port_lessons_20260715.md",
                "type": "wz_port_lesson_doc",
                "date": "20260715",
            }
        )

    print(f"Prepared {len(ids)} entries for {COLLECTION} @ {DB_PATH}")
    print(f"Loading embedding model {EMBEDDING_MODEL}...")
    model = SentenceTransformer(EMBEDDING_MODEL)
    embeddings = model.encode(documents, show_progress_bar=True, normalize_embeddings=True)
    embeddings = [e.tolist() for e in embeddings]

    client = chromadb.PersistentClient(path=DB_PATH)
    col = client.get_collection(COLLECTION)
    col.upsert(ids=ids, documents=documents, metadatas=metadatas, embeddings=embeddings)
    print(f"Upserted {len(ids)} ids into {COLLECTION}")
    print("ids:", ", ".join(ids))
    print(f"collection_count={col.count()} path={DB_PATH}")
    return 0


if __name__ == "__main__":
    sys.exit(main())
