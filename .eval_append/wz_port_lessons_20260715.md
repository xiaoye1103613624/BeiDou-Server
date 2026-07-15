# WZ 高版本移植经验总结（2026-07-15）

> 入库：`D:\xy_vector_db` → collection `maplestory_kb`（project=GMS083）  
> 过程日志：`E:\pro\BeiDou-Server_xy\.eval_append\`

## 1. 源与目标

| 角色 | 路径/来源 |
|------|-----------|
| 源 A | V095 PLUS2 客户端（CMS 密） |
| 源 B | 「适用于083时装」时装差分包 |
| 源 C | 要爱 V104 客户端（CMS 密） |
| 目标 | `BeiDou-Client_1/Data`（083 GMS 散 IMG） |

原则：**只写 Client_1 Data**，根目录 `*.wz` 覆盖包勿用；服务端同步 `gms-server/wz-zh-CN`（及必要时 `wz/`）。

## 2. 工具链与转密

- **Append-only**：目标已存在的非空 `.img` 绝不整文件覆盖；只补缺失文件，或对同文件做**节点级 SKIP 粘贴**。
- **CMS → GMS 转密**：经 **OrzRepacker / orange-wz HTTP MCP `:10002`**（`create_img` + copy/paste + 以 GMS key/IV 保存）。
- 当日 Cursor **beidou-build MCP 不可用**，改用本机 Orz `:10002` 完成转密与节点操作。
- GMS key：`083-GMS` + IV `TSPHKw==`；CMS IV 常见 `b97d63e9`（`uX1j6Q==`）。

## 3. 已完成的安全移植面

| 类别 | 做法 | 结果 |
|------|------|------|
| 时装 Character | 节点/整文件 append | 完成 |
| 武器节点 | 缺口扫描 + 节点同步 | 完成 |
| Map / Mob | 清理后按批 append | 完成（文件级） |
| Skill（文件级） | 仅缺文件补齐 | 部分 |
| UI | 试合并后清理 | 已隔离 |
| Skill / Effect 高版本 | 发现 schema 不兼容后隔离 | 见下 |

## 4. 致命坑：EC_INVALID_GAME_DATA

- **性质**：本地 WZ/IMG **语义/schema** 问题，**不是**服务端不同步。
- **处置**：将有问题的 UI / Effect / Skill 隔成 `.bad` / `.bad2`，保留对照，勿删审计痕迹。
- **禁止**：整包端口径移植高版本 **Skill 根**、巨型 **SetEff**、**Direction4+** 等易触发 EC_INVALID 的资源。
- **高版本 Effect/Skill**：节点结构不兼容（如 `common` vs `level`/`N`）。**不要整包搬**；当前可行性低，仅文件级缺项可谨慎评估。

## 5. error 38（ExpDecode8）

- **症状**：客户端 ExpDecode8 不匹配（error 38）。
- **修复**：更换/部署匹配的新版 **`ijl15.dll`**（与当前 hook 链一致），非 WZ 回滚。

## 6. 刻意跳过 / 已知缺口

| 项 | 原因 |
|----|------|
| Cash `05010156`–`05010189` | 异常/未稳节点，跳过 |
| Direction4 相关 Sound | 高版本易踩 schema/音效坑 |
| Radio placeholder | 占位资源，无意义移植 |
| Skill/Effect 高版本整树 | schema 不兼容，EC_INVALID 风险 |

## 7. 推荐工作流（可复用）

1. **盘点**：源缺、目标已有、加密（CMS/GMS）、是否整文件可 copy。
2. **缺文件且密钥已是 GMS**：`append_whole_files` / `shutil.copy2`，严格 skip-exists。
3. **CMS 或 packed WZ**：Orz `:10002` 转密后写入 Data。
4. **双方均有巨型包**（String/Item/Cash/部分 Map）：节点 paste **SKIP**，禁止整文件覆盖。
5. **进图/登录失败**：先查本地 `.bad/.bad2` 与近期合并面，再查服务器。
6. **双端**：客户端 Data + `wz-zh-CN` Map/Mob/Npc/String/Item 同步；勿只改一端。
7. **日志**：JSON/console 落在 `.eval_append/`，便于回溯批次。

## 8. 遗忘山谷 / Item XML 闭合（补记）

- 山谷 Map/Mob/Npc/String 以 **append-only** 同步 `wz-zh-CN`；入口图 `101020000` / `105040305` 仅补 portal。
- **0400 / 0403** 等大 XML：append 后必须检查根 `</imgdir>`；截断会表现为 truncated / 道具加载失败（详见 `error_ops_lessons_20260715.md` §3）。
- 报告：`.eval_append/valley_port_report_20260715.md`。

## 9. 检索关键词

`append-only` `CMS2GMS` `OrzRepacker` `10002` `EC_INVALID_GAME_DATA` `.bad2` `ExpDecode8` `ijl15` `V095` `V104` `时装` `Direction4` `05010156` `BeiDou-Client_1` `0400` `0403` `遗忘山谷` `HigherShopList` `avatar flag 100`
