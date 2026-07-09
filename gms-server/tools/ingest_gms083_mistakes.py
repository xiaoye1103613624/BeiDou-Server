# -*- coding: utf-8 -*-
"""Ingest structured GMS083 mistake/lesson entries into Chroma maplestory_kb."""
from __future__ import annotations

import argparse
import sys
from typing import Any

import chromadb
from chromadb.utils import embedding_functions
from sentence_transformers import SentenceTransformer

EMBEDDING_MODEL = "paraphrase-multilingual-MiniLM-L12-v2"

DB_PATH = r"D:\xy_vector_db"
COLLECTION = "maplestory_kb"
PROJECT = "GMS083"
SOURCE = "GMS083错误修改总结.md"

ENTRIES: list[dict[str, Any]] = [
    {
        "id": "gms083_mistake_01",
        "category": "伤害皮肤/WZ资源",
        "title": "78MB BasicEff 与 70MB DamageSkin.img 并存导致 E_FAIL",
        "wrong": "合并参考 WZ 使 BasicEff.img 约 78MB，同时保留独立 DamageSkin.img 约 70MB（Case F）。",
        "correct": "Case C：客户端 BasicEff 保持原版约 4MB（3992551B），单独 DamageSkin.img 约 70MB；服务端 catalog 读 gms-server/wz BasicEff.img.xml 的 damageSkin。",
        "symptom": "启动或进图 E_FAIL；bisect 矩阵 78MB+70MB 必 FAIL。",
        "detect": "核对 BasicEff≈3992551B、DamageSkin≈70058716B，勿双巨型 Effect 并存。",
    },
    {
        "id": "gms083_mistake_02",
        "category": "伤害皮肤/WZ资源",
        "title": "BeiDou 083 根目录覆盖 *.wz 补丁",
        "wrong": "在客户端根目录部署 Effect.wz/UI.wz/Item.wz/String.wz 覆盖包（Kaentake 做法）。",
        "correct": "北斗 083 只改 Data/*.img 节点；根目录 WZ 删除或改名为 *.removed，保留 List.wz。",
        "symptom": "加载冲突、与 Data IMG 双轨不一致、体积异常。",
        "detect": "根目录无活跃 Effect.wz 等；资源仅在 Data/ 目录。",
    },
    {
        "id": "gms083_mistake_03",
        "category": "伤害皮肤/WZ资源",
        "title": "Data/manifest.json 不应存在（Case C）",
        "wrong": "在 Data/ 部署 manifest.json，或 encryption/version 与 083 不符。",
        "correct": "Case C baseline 不部署 manifest.json；若必须存在则 encryption=GMS、version=083。",
        "symptom": "客户端读 IMG 行为异常、与原版 scatter 加载路径冲突。",
        "detect": "Case C 清单要求 manifest 不存在；对比 deploy_features.bat 校验项。",
    },
    {
        "id": "gms083_mistake_04",
        "category": "伤害皮肤/WZ资源",
        "title": "整文件覆盖 IMG 而非节点 APPEND",
        "wrong": "用 WzImg/orange-wz 整文件替换 UIWindow.img 或 BasicEff.img。",
        "correct": "BasicEff 只 deepClone damageSkin 子树；UIWindow APPEND DamageSkin/*；DamageSkin.img 可整文件复制；用 WzImg MCP 节点级操作。",
        "symptom": "UI 缺失、选择器资源丢失、或再次 E_FAIL。",
        "detect": "对比合并前后字节与 DamageSkin 子节点数（UIWindow 约 12_160_040B）。",
    },
    {
        "id": "gms083_mistake_05",
        "category": "伤害皮肤/WZ资源",
        "title": "缺少 String 5910000（含 wz-zh-CN）",
        "wrong": "只加 Item/Cash/0591.img，未同步 String/Cash.img 5910000；服务端 wz-zh-CN 漏项。",
        "correct": "Data/String/Cash.img 与 gms-server/wz-zh-CN/String.wz/Cash.img.xml 同步 5910000 名称/描述。",
        "symptom": "!item 5910000 失败或道具无名称。",
        "detect": "MCP/XML 搜索 5910000；重启服务端后 !item 5910000。",
    },
    {
        "id": "gms083_mistake_06",
        "category": "伤害皮肤/WZ资源",
        "title": "服务端 WZ 未合并 damageSkin 导致 catalog 空",
        "wrong": "只改客户端 IMG，未更新 gms-server/wz/Effect.wz/BasicEff.img.xml。",
        "correct": "服务端 XML 合并约 597 个 damageSkin 节点供 DamageSkinCatalog 扫描。",
        "symptom": "商店目录空、有库存无展示。",
        "detect": "启动日志 DamageSkinCatalog 数量；检查 WZ damageSkin 节点。",
    },
    {
        "id": "gms083_mistake_07",
        "category": "伤害皮肤/WZ资源",
        "title": "服务端 catalog 与客户端实际 IMG 不一致",
        "wrong": "服务端 catalog 扫 597 皮肤，客户端 Case C 只有 DamageSkin.img 独立文件，未验证 368/597 可见性。",
        "correct": "服务端 catalog 只读 WZ XML；客户端渲染读 Data/Effect/DamageSkin.img/<id>；两边 ID 集合分别验证。",
        "symptom": "服务端有目录、客户端列表空或部分皮肤无 canvas。",
        "detect": "对比 catalog 日志条数与客户端 catalog preload N/368 日志。",
    },
    {
        "id": "gms083_mistake_08",
        "category": "客户端插件/Hook",
        "title": "错误版本 ijl15.dll（Debug 1.35MB / ultra 302KB）",
        "wrong": "部署 Debug 约 1352192B、ultra-minimal 约 300544B、或旧 467456B 无 EnsureHooks 链。",
        "correct": "Release Win32 lazy 约 520704–525824B（Case C baseline）；PostBuild 自动 xcopy 到 BeiDou-Client_1。",
        "symptom": "启动/进图/开选择器 COM E_FAIL 或 hook 不稳定。",
        "detect": "(Get-Item ijl15.dll).Length；禁止 Debug 联调长期部署。",
    },
    {
        "id": "gms083_mistake_09",
        "category": "客户端插件/Hook",
        "title": "get_screen_height 地址 0x9F7082 读到代码字节",
        "wrong": "Hook get_screen_height 于错误地址 0x9F7082，读到非高度数据。",
        "correct": "使用 Client::m_nGameHeight / m_nGameWidth（compat/wvs/util.h）计算选择器居中坐标。",
        "symptom": "双击 5910000 有日志但 UI 不可见；Y=619981559 等荒谬坐标。",
        "detect": "damageskin_picker.txt 中 CreateWnd 坐标是否在客户区内（1280×720 下 Y≈180）。",
    },
    {
        "id": "gms083_mistake_10",
        "category": "客户端插件/Hook",
        "title": "Hook 错误双击路径（get_consume 对 5910000 返回 0）",
        "wrong": "只 hook get_consume(0x4863D5)/SendConsumeCash，以为 v083 会走消耗路径。",
        "correct": "在 CDraggableItem::OnDoubleClick @ 0x004EFD25 识别现金栏 5910000 并打开选择器；get_consume 作辅助。",
        "symptom": "双击 5910000 完全无反应；日志无 OnDoubleClick intercept。",
        "detect": "IDA 验证 0x004EFD25；damageskin_picker.txt 应有 OnDoubleClick intercept。",
    },
    {
        "id": "gms083_mistake_11",
        "category": "客户端插件/Hook",
        "title": "DamageSkin.img 未载入 g_mDamageSkinProp",
        "wrong": "磁盘有 70MB DamageSkin.img 但未 GetObjectA 加载，catalog 过滤后列表空。",
        "correct": "EnsureDamageSkinLoaded + GetObjectA 按路径加载 Effect/DamageSkin.img/<id>；进 CField 后 EnsureHooks。",
        "symptom": "选择器窗口出现但皮肤列表为空；catalog preload 0/368。",
        "detect": "damageskin_bridge.txt 应有 LoadDamageSkin: root resolved 和 preload N>0。",
    },
    {
        "id": "gms083_mistake_12",
        "category": "客户端插件/Hook",
        "title": "DamageSkinBridge 解析 0x172 未跳过 2 字节 opcode",
        "wrong": "DamageSkinBridge 直接读 0x172 payload，未先消费 2 字节 opcode 头。",
        "correct": "先 skip 2 字节 opcode，再解析 result；购买成功后服务端额外推 0x171 刷新库存。",
        "symptom": "购买成功但列表不更新；result 被误判为失败。",
        "detect": "damageskin_bridge.txt 应有 packet 0x172 result op=2 ok=1。",
    },
    {
        "id": "gms083_mistake_13",
        "category": "客户端插件/Hook",
        "title": "DllMain 同步 Attach 或重复 detour set_stage",
        "wrong": "DllMain 直接 AttachDamageSkinMod；DamageSkinBridge 与 DamageRankStage 重复 hook set_stage @ 0x777347。",
        "correct": "LazyCompatInit 进 CField 后 EnsureHooks；DamageSkin::AttachHooks 空操作，由 DamageRankStage 统一触发。",
        "symptom": "启动 E_FAIL 或进图崩溃。",
        "detect": "对比 Client-v83 SEH 延迟 Attach；检查 set_stage 仅一处 detour。",
    },
    {
        "id": "gms083_mistake_14",
        "category": "客户端插件/Hook",
        "title": "SendConsume 与 OnDoubleClick 重复 Open 选择器",
        "wrong": "双击时 SendConsume 和 OnDoubleClick 各 Open 一次，首次双击立刻 toggle 关闭。",
        "correct": "OnDoubleClick 路径 Open；去掉 SendConsume 重复 Open；或只保留一条打开路径。",
        "symptom": "双击闪一下即消失；日志有 use+picker 但窗口不可见。",
        "detect": "damageskin_picker.txt 单次 Open 记录；CreateWnd 只出现一次。",
    },
    {
        "id": "gms083_mistake_15",
        "category": "服务端",
        "title": "未注册伤害皮肤 opcode 0x110/0x111",
        "wrong": "只实现发包未加 RecvOpcode 与 Handler。",
        "correct": "DamageSkinApplyHandler/PurchaseHandler 并在 PacketProcessor 注册。",
        "symptom": "客户端申请/购买无响应。",
        "detect": "抓包或日志 0x0110、0x0111。",
    },
    {
        "id": "gms083_mistake_16",
        "category": "服务端",
        "title": "登录换图未广播 DAMAGE_SKIN 0x173",
        "wrong": "未在 PlayerLoggedinHandler/MapleMap 同步 activeDamageSkin。",
        "correct": "登录与地图内广播 0x173 charId+skinId。",
        "symptom": "自己或他人看不到皮肤；换图丢失。",
        "detect": "换线进图抓 0x0173 包。",
    },
    {
        "id": "gms083_mistake_17",
        "category": "服务端",
        "title": "Flyway V1.11.4 伤害皮肤表未迁移",
        "wrong": "使用旧 JAR 或 DB 无 damageskin 表与 active_damage_skin 列。",
        "correct": "部署含 V1.11.4__damage_skin.sql 的 BeiDou.jar 并完成迁移。",
        "symptom": "SQL 异常或功能静默失败。",
        "detect": "检查表 damageskin_catalog/inventory 与 Flyway 历史。",
    },
    {
        "id": "gms083_mistake_18",
        "category": "DPT/世界地图/兼容层",
        "title": "每次 stage 变化都 ResetOnStageChange 导致 DPT 换图隐藏",
        "wrong": "set_stage hook 对所有 stage 变化调用 ResetOnStageChange，字段内换图也重置面板。",
        "correct": "进 CField 时 OnMapTransition 重建 layer 保留数据；仅离开字段时 ResetOnStageChange + 清空 CDamageRankData。",
        "symptom": "地图 warp 后 F12 伤害面板自动隐藏或数据丢失。",
        "detect": "字段内换图面板应保留；离开城镇/频道才重置。",
    },
    {
        "id": "gms083_mistake_19",
        "category": "DPT/世界地图/兼容层",
        "title": "PacketDispatcher 使用 __fastcall 而非 __thiscall",
        "wrong": "ProcessPacket hook trampoline 用 __fastcall，与 v83 成员函数 __thiscall 不符。",
        "correct": "Hook 0x004965F1 使用 __thiscall trampoline（compat/PacketDispatcher.cpp）。",
        "symptom": "进图或特定 opcode 后栈破坏崩溃。",
        "detect": "IDA 核对 ProcessPacket 原函数 calling convention。",
    },
    {
        "id": "gms083_mistake_20",
        "category": "DPT/世界地图/兼容层",
        "title": "0x178 吞包规则错误",
        "wrong": "未吞 0x178 导致世界地图 crash；或把 0x170–0x173 误吞/误转发。",
        "correct": "0x178 专供 WorldMapInfo 吞包；0x170–0x173 由 DamageSkinBridge 消费；除 0x178 外禁止随意吞包。",
        "symptom": "开世界地图闪退或皮肤包解析错乱。",
        "detect": "开 W 地图同时操作皮肤 UI；config.ini disableWorldMap 二分。",
    },
    {
        "id": "gms083_mistake_21",
        "category": "DPT/世界地图/兼容层",
        "title": "世界地图 MapInfo 与服务端地图/portal 不一致",
        "wrong": "只改 WZ MapInfo 未改 MapleMap/portal 脚本。",
        "correct": "MapInfo 节点与 MapleMap、portal 脚本一致。",
        "symptom": "世界地图可点但无法进入或黑屏。",
        "detect": "逐图点击世界地图验证传送。",
    },
    {
        "id": "gms083_mistake_22",
        "category": "部署与调试",
        "title": "PostBuild/部署到错误客户端路径",
        "wrong": "java -jar 不在 gms-server/；dll 未拷到实际运行的 BeiDou-Client_1；改错客户端副本。",
        "correct": "JAR 从 gms-server/ 启动；Release dll PostBuild 到 canonical BeiDou-Client_1；单一测试客户端。",
        "symptom": "脚本/WZ/插件改动不生效、测试结果不可复现。",
        "detect": "核对工作目录、ijl15 路径与大小、Data 文件字节。",
    },
    {
        "id": "gms083_mistake_23",
        "category": "部署与调试",
        "title": "matrix bisect 反复覆盖客户端文件",
        "wrong": "efail_matrix_bisect 每轮覆盖 dll/IMG 无 baseline 记录；WzImg MCP 占用文件。",
        "correct": "用 deploy_features.bat/rollback_baseline.bat；每轮记录 dll+IMG 字节到 matrix_results.jsonl；改文件前 taskkill BeiDou.exe。",
        "symptom": "bisect 结论不可复现、IMG 损坏、文件被锁。",
        "detect": "对照 tools/_bisect_logs/matrix_results.jsonl Case C PASS 行。",
    },
    {
        "id": "gms083_mistake_24",
        "category": "部署与调试",
        "title": "bisect 脚本未经同意自动启动 BeiDou.exe",
        "wrong": "efail_launch_test / matrix 脚本自动启动客户端，干扰用户操作。",
        "correct": "bisect 只复制文件并校验字节；由用户手动启动验证；NO auto launch。",
        "symptom": "测试过程中客户端被意外拉起、文件仍被占用。",
        "detect": "脚本中搜索 ShellExecute/Start-Process BeiDou.exe。",
    },
    {
        "id": "gms083_mistake_25",
        "category": "部署与调试",
        "title": "Chroma 检索默认查 personal_kb（空库）",
        "wrong": "mcp_vector_search.py CLI 默认查 personal_kb，GMS083 条目在 maplestory_kb。",
        "correct": "查询时显式指定 collection=maplestory_kb 和 where project=GMS083；用 Python 3.10。",
        "symptom": "向量检索返回空或无关结果。",
        "detect": "client.list_collections() 确认 maplestory_kb 有 gms083_mistake_* ids。",
    },
    {
        "id": "gms083_mistake_26",
        "category": "伤害皮肤/WZ资源",
        "title": "WZ/IMG/XML 资源 CRUD 须客户端服务端同步",
        "wrong": "只改客户端 Data/*.img（如 0592.img），未同步 gms-server/wz/ 与 wz-zh-CN/ 的 Item.wz、String.wz；或只改服务端未改客户端。",
        "correct": "新增/改/删资源时双端同步：服务端 wz/ + wz-zh-CN/（Item.wz、String.wz 等）；客户端 BeiDou-Client_1/Data/*.img 散图 APPEND（Case C 禁止根目录 .wz）；String 英文+中文各一份。见 WZ-IMG-XML资源同步规范.md。",
        "symptom": "!item 5920000 失败（客户端有 0592.img 但服务端缺 5920000）；道具无名称；UI 缺失。",
        "detect": "MCP/XML 搜索 Item ID 于 wz/、wz-zh-CN/、Data/Item/、Data/String/；重启服务端后 !item {id}。",
    },
]


def format_document(entry: dict[str, Any]) -> str:
    return (
        f"标题: {entry['title']}\n"
        f"分类: {entry['category']}\n"
        f"错误做法: {entry['wrong']}\n"
        f"正确做法: {entry['correct']}\n"
        f"症状: {entry['symptom']}\n"
        f"检测: {entry['detect']}\n"
        f"项目: {PROJECT}\n"
    )


def main() -> int:
    parser = argparse.ArgumentParser(description="Ingest GMS083 mistakes into Chroma")
    parser.add_argument("--dry-run", action="store_true", help="Print counts only, no write")
    args = parser.parse_args()

    ids = [e["id"] for e in ENTRIES]
    documents = [format_document(e) for e in ENTRIES]
    metadatas = [
        {
            "category": e["category"],
            "title": e["title"],
            "project": PROJECT,
            "source": SOURCE,
            "type": "mistake_lesson",
        }
        for e in ENTRIES
    ]

    print(f"Prepared {len(ENTRIES)} entries for {COLLECTION} @ {DB_PATH}")
    if args.dry_run:
        for e in ENTRIES:
            print(f"  {e['id']} [{e['category']}] {e['title']}")
        return 0

    print(f"Loading embedding model {EMBEDDING_MODEL}...")
    model = SentenceTransformer(EMBEDDING_MODEL)
    print("Encoding documents...")
    embeddings = model.encode(documents, show_progress_bar=True, normalize_embeddings=True)
    embeddings = [e.tolist() for e in embeddings]

    print(f"Connecting Chroma @ {DB_PATH}...")
    client = chromadb.PersistentClient(path=DB_PATH)
    col = client.get_collection(COLLECTION)
    col.upsert(
        ids=ids,
        documents=documents,
        metadatas=metadatas,
        embeddings=embeddings,
    )
    print(f"Upserted {len(ids)} ids: {ids[0]} .. {ids[-1]}")

    got = col.get(ids=["gms083_mistake_10"], include=["metadatas", "documents"])
    print("spot-check:", got["metadatas"][0]["title"] if got["metadatas"] else None)
    return 0


if __name__ == "__main__":
    sys.exit(main())
