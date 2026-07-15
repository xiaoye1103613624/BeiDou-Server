# 错误操作经验记录（2026-07-15）

> 目的：把当日踩坑固化，避免下一轮重复。  
> 入库：`D:\xy_vector_db` → `maplestory_kb`（`gms083_error_ops_20260715_*`）  
> 相关：`.eval_append/wz_port_lessons_20260715.md`

## 1. EC_INVALID_GAME_DATA ← 整包搬高版本 Skill/Effect

| 项 | 内容 |
|----|------|
| **错误做法** | 把 V095/V104 的 Skill、Effect（含巨型 SetEff / Direction4+）整包 dump 进 `BeiDou-Client_1/Data` |
| **正确做法** | 时装/武器/缺文件 Map·Mob 可 append；Skill/Effect 高版本 **勿整包**。出问题立即隔离为 `.bad` / `.bad2`，保留检疫痕迹 |
| **症状** | 客户端 `EC_INVALID_GAME_DATA`，进图/启动失败 |
| **检测** | 对照近期合并的 UI/Effect/Skill；改名隔离后能否进图 |

## 2. error 38 = ExpDecode8 与服务端 writeLong 不对齐

| 项 | 内容 |
|----|------|
| **错误做法** | 当成 WZ 损坏去大规模回滚 Data；或只改服务端/只改 DLL 一侧 |
| **正确做法** | 服务端 EXP `writeLong` + 等级 `writeShort`；客户端 `ijl15` level300 **Decode8 / Decode2** caves 同步部署 |
| **症状** | error 38 / ExpDecode8 mismatch |
| **检测** | 核对 `PacketCreator` 经验/等级写法与 `ezorsia/level300` hook；DLL 构建时间与 Client_1 一致 |

## 3. Item XML 截断缺 `</imgdir>`（0400 / 0403）

| 项 | 内容 |
|----|------|
| **错误做法** | append/sync 后不校验 XML 闭合，直接开服或导出 |
| **正确做法** | 对触碰过的 `Etc/0400.img.xml`、`0403.img.xml` 等做闭合检查；缺根 `</imgdir>` 立刻补齐再同步客户端 |
| **症状** | 道具解析失败、物品表加载异常、Orz/解析器报 truncated |
| **检测** | 文件尾部标签；解析器/开服日志 |

## 4. HigherShopList：误改 WIDTH / Avatar flag 100≠127

| 项 | 内容 |
|----|------|
| **错误做法** | 把 `CCtrlTab::Create` 的 **a7=WIDTH(222)** 当成 tab **高度**去改；或把 `CAvatar` Create flag imm8 **100** 改成 **127**（曾误标为 sell_sb_height） |
| **正确做法** | 只扩列表行数/滚动条高度（+160）；**不要** patch tab WIDTH；**不要**改 avatar flag（100→127 会让卖栏人物原点漂） |
| **症状** | Tab 错位；卖栏角色漂浮/图层错乱 |
| **检测** | IDA 核对 `CCtrlTab::Create` 与 `CAvatar::Create` 实参；对照 `highershoplist.cpp` 注释黑名单地址 |

## 5. Cursor beidou MCP 挂了仍硬用

| 项 | 内容 |
|----|------|
| **错误做法** | Cursor `beidou-build` MCP down 时反复重试、阻塞流水线 |
| **正确做法** | 改用本机 **OrzRepacker / orange-wz HTTP MCP `http://127.0.0.1:10002`**（GMS IV=`TSPHKw==`）做转密与节点 paste |
| **症状** | MCP 超时/不可用；移植停摆 |
| **检测** | Orz initialize 200；083-GMS key 可 parse Data `*.img` |

## 6. 高版本 Skill：`common` → `level` 不可自动移植

| 项 | 内容 |
|----|------|
| **错误做法** | 假设节点粘贴或脚本可把高版本 Skill 的 `common` 结构自动迁到 083 `level`/`N` |
| **正确做法** | schema 不兼容即跳过；仅文件级缺项可谨慎评估；记录 SKIP，勿强行 port |
| **症状** | EC_INVALID、技能读档崩溃、效果错乱 |
| **检测** | 对比源/目标 Skill 根结构；向量库 `gms083_wzport_*` / 本文 |

## 7. 其它顺带踩过（简记）

- **UTF-16 污染**：部分插件源被另存为 UTF-16/BOM，表现为 git「Bin」diff；提交前用字节检查，污染文件从 HEAD 还原。
- **双端不一致**：Client Data 与 `gms-server/wz-zh-CN` 只改一端 → 进图或道具对不上。
- **提交噪音**：勿 force-add `BeiDou.jar`、`.codegraph/codegraph.db`、`__pycache__`、大量 append 临时 log（除非有意归档的 lessons/report）。
