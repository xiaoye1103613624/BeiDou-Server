# GMS083 近期新模组汇总（2026-07-15）

> 入库 ID 前缀：`gms083_recent_mods_20260715_*`  
> 仓库：Server `BeiDou-Server_xy` / Plugin `BeiDou-ijl15`

## 服务端（Java + SQL + WZ/脚本）

| 模组 | 要点 |
|------|------|
| **灵韵觉醒 Spirit** | `org.gms.spirit.*`；装备附加技字段；交易清空灵韵；NPC「灵韵觉醒」；`V1.11.18`；Item 0402/0446 |
| **天赋 Talent** | `org.gms.talent.*`；闪避/减伤/刷怪倍率；NPC 桥接；`V1.11.17` |
| **Level 300 / EXP bigint** | `ExpTable.MAX_LEVEL=300`；角色 exp `AtomicLong`；包 `writeShort(level)` + `writeLong(exp)`；`V1.11.20` |
| **队伍 Buff / Tracker** | `Party`/`World` 快照；`CUSTOM_PACKET 0x3713`；`V1.11.16` PartyTracker 命令 |
| **槽位锁 SlotLock** | `InventoryMerge/SortHandler` 解析锁定槽 |
| **商店槽 32** | `HiredMerchant`/`PlayerShop` 16→32 |
| **角色槽 30** | 现金购槽提示对齐客户端 CharSlots |
| **怪物卡** | `V1.11.19/21/22` + Consume `0238` / String |
| **遗忘山谷** | Map0 `010006xxx`、Mob/Npc/String、0400/0403 闭合修复 |

## 客户端插件（ijl15 / ezorsia）

| 模组 | 要点 |
|------|------|
| **HigherShopList** | 商店列表 5→9；勿改 tab WIDTH、勿改 avatar flag 100 |
| **Level300** | Decode2 等级 + Decode8 EXP；`ReplacementFuncs` EXP 表对齐 300 |
| **MaxHpMp** | HP/MP Decode2→Decode4 |
| **PartyBuffs** | 队伍 Buff 图标 / HP% / EXP·金币 Tracker |
| **BuffTimer** | `LevelNo` / `LevelNo/number` 字体回退 + SEH 画数字 |
| **PersonalShop / CharSlots / EquipCompare / SlotLock** | 个人店槽、选角 30 槽、装备对比、背包槽锁 |
| **UserInfoDetail** | 解包灵韵 `equipSkillId/Level/Expire` |

## 工具与文档

- WZ lessons / error_ops：`.eval_append/wz_port_lessons_20260715.md`、`error_ops_lessons_20260715.md`
- Ingest：`gms-server/tools/_ingest_wz_port_lessons_20260715.py`、`_ingest_recent_mods_20260715.py`
- 商店背景扩展：`merge_beauty_img` PatchStorageBg extend-shop（配合 HigherShopList +160）
