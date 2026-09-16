# 任务管理（Quest Management）

> 状态：可演示闭环（列表/详情/完成态/CRUD/发布/脚本/客户端同步入口）  
> 适用：GMS v83 / BeiDou-Server  
> 日期：2026-09-15  
> 产物：`docs/features/quest-management/`

## 1. 已锁定决策

| 项 | 结论 |
| --- | --- |
| 主源 | **A 纯 WZ**（`wz/Quest.wz` + `wz-zh-CN/Quest.wz`） |
| 客户端同步 | **一并交付**：开发态可写服务端 WZ 并可选同步客户端 `.img`（`Data`←zh，`EN`←en） |
| 生产写客户端 | `allow_quest_client_write=false`（默认关） |
| 未做 | 默认「从未接取」语义；UI 可切「未完成 / 已完成 / 全部有记录」 |
| 删除定义 | **不**级联清 `queststatus` |
| 脚本 | Monaco 读写 `{questId}.js`；Say/复杂 Act 警告 |
| 实时 | 管理端轮询；`publish` = `Quest.reloadFromWz` + reloadQuestScripts；SSE 预留 |
| 链路 | 表格式上下游（DAG 预留） |

## 2. 架构要点

- 运行时定义：`org.gms.server.quest.Quest` 读 WZ（非 `questactions`/`questrequirements` 表）。
- 进度：`queststatus` / `questprogress` / `medalmaps`。
- 管理 API：`QuestController` → `QuestAdminService` + `QuestWzXmlStore` + `QuestClientSyncService`。
- 热重载：必须 `Quest.reloadFromWz()`（仅 `clearCache` 不够，static Data 会陈旧）。
- 客户端路径：复用 `ClientDataPath`（`window_cashshop_client_data_path` / `-Dgms.client.data`）。

## 3. API（`/quest/v1`）

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/getQuestList` | 分页搜索 |
| POST | `/getQuestDetail` | 详情 |
| PUT | `/addQuest` | 新建 WZ |
| POST | `/updateQuest` | 更新 WZ |
| DELETE | `/deleteQuest/{id}` | 删定义，保留进度 |
| POST | `/publish` | 热重载定义+脚本 |
| POST | `/getCompletions` | 按任务查进度 |
| POST | `/getCharacterQuests` | 按角色查进度 |
| POST | `/forceStart` `/forceComplete` `/reset` | 强制态 |
| POST | `/script/read` `/script/write` | 脚本 |
| GET | `/syncClient/status` | 路径/开关/patcher |
| POST | `/syncClient` | dryRun 默认 true；实写需开关 |

## 4. 配置键

| code | 默认 | 说明 |
| --- | --- | --- |
| `window_cashshop_client_data_path` | 空 | 客户端 Data 根（已有） |
| `allow_quest_client_write` | false | 是否允许实写客户端 |
| `quest_client_en_path` | 空 | EN 根；空则尝试 Data 同级 `EN` |
| `quest_xml_img_patcher_path` | 空 | patcher 绝对路径 |

## 5. 客户端同步行为

1. **dry-run（默认）**：导出 XML 快照到 `docs/features/quest-management/patches/<时间戳>/` + MANIFEST。  
2. **实写**：要求 ClientPath + `allow_quest_client_write=true`。  
3. **有 patcher**：尝试 `batch --full-xml-dir=... --target=.../Quest`（zh→Data，en→EN）。  
4. **无 patcher（本仓现状）**：降级导出清单到客户端旁 `quest-management-patches/`，**不改 .img**；人工用 `xml-img-patcher`（见 CLAUDE.md）。

> 注意：patch **ADD 是合并**；客户端占用时写失败需关客户端或手工替换。

## 6. DB 迁移

- `V1.11.54__quest_management_indexes_menu.sql`：索引 + 菜单 `GameQuest`  
- `V1.11.55__quest_client_sync_config.sql`：同步相关 game_config

## 7. 前端

- 路由：`/game/quest`（`admin`/`operator`）  
- 页：`gms-ui/src/views/game/quest/index.vue`  
- API：`gms-ui/src/api/quest.ts`

## 8. 验证

```bash
# JDK 21（示例：本机 .jdks）
set JAVA_HOME=C:\Users\11036\.jdks\ms-21.0.12.1
cd gms-server && mvn -DskipTests compile   # 已通过 2026-09-15

cd gms-ui && yarn type:check               # 已通过 2026-09-15
```

手工：启动服（跑 Flyway V1.11.54/55）→ 管理端「游戏管理 → 任务管理」→ 列表/详情 → 改名称 → 发布 → 完成态查询 → sync 干跑。  
开发态实写客户端：配置 ClientPath，并将 `allow_quest_client_write=true`；有 patcher 时配置 `quest_xml_img_patcher_path`。

## 9. 已实现 / 未实现

### 已实现
- 索引+菜单、读列表/详情、WZ CRUD、publish、完成态、强制态、脚本读写、sync 状态/干跑/降级实写骨架、gms-ui 全页、i18n

### 预留 / 降级
- 真·打 `.img`：依赖缺失的 `xml-img-patcher.exe`（可配置路径接入）
- DAG 可视化、SSE、「从未接取」= 无行角色全量反查（当前 never≈status=0 行）
- Say.img 结构化编辑、孤儿进度清理工具

## 10. 关键文件

- `gms-server/.../controller/QuestController.java`
- `gms-server/.../service/QuestAdminService.java`
- `gms-server/.../service/quest/QuestWzXmlStore.java`
- `gms-server/.../service/quest/QuestClientSyncService.java`
- `gms-server/.../server/quest/Quest.java`（`reloadFromWz`）
- `gms-ui/src/views/game/quest/*`
- `docs/features/quest-management/notes/research.md`
