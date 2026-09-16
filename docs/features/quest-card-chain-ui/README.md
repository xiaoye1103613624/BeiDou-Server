# 任务卡片列表 + 完整链路画布

> 状态：已闭环  
> 适用客户端/协议版本：MapleStory v83（GMS）  
> 项目名称：BeiDou-Server  
> 作者/日期：2026-09-16  
> 产物目录：docs/features/quest-card-chain-ui/

## 1. 背景与目标

- 任务管理页原为纯表格：无 NPC/道具/怪物图标，关联 Tab 只有前置表与裸 `nextQuestId`，无法浏览整条任务链。
- 目标：列表改为卡片并展示接取 NPC 头像；详情补图；「链路」Tab 用 Vue Flow 展示以当前任务为种子的完整连通图，节点可进详情/编辑。
- 明确不做：独立全屏编辑页；按 `parentName` 额外造边；DTO 增加图标 URL 字段。

## 2. 影响范围

| 层级 | 路径/模块 | 变更类型 |
| --- | --- | --- |
| 服务端 | `QuestAdminService` / `QuestController` / Chain DTOs | 列表 nextQuest 修复 + 链路 API |
| 管理端/前端 | `gms-ui/src/views/game/quest/*`、`api/quest.ts` | 卡片列表、图标、链路画布 |
| i18n | 服务端 `message_*.properties`；前端 quest locale | 链路截断警告与 UI 文案 |
| 脚本 / WZ / 插件 / DB | — | 无 |

## 3. 最终实现逻辑

### 3.1 主流程

1. 列表：`getQuestList` 一次加载 Act.img，建 `questId → nextQuest` 索引，填入行的 `nextQuestId`；前端卡片网格展示 NPC（`ItemIcon category="npc"`）与下一任务。
2. 详情概览：接取/完成 NPC、完成材料/打怪/奖励表行内挂 `ItemIcon`（npc / item / mob）。
3. 链路 Tab：请求 `POST /quest/v1/getQuestChain`；后端对 Act.nextQuest + Check 前置双向 BFS（上限 80）；前端 dagre `LR` 布局 + Vue Flow；节点单击切详情（保持链路 Tab），「编辑」进编辑 Tab。

### 3.2 数据 / 接口

**`POST /quest/v1/getQuestChain`**（`SubmitBody<Integer>` = seed questId）

返回 `QuestChainRtnDTO`：

- `nodes[]`：`questId` / `name` / `parentName` / `startNpcId` / `startNpcName` / `minLevel` / `current`
- `edges[]`：`from` / `to` / `type`（`next` | `prereq`）
  - `next`：完成 Act.1 的 `nextQuest`（from → to）
  - `prereq`：Check.0 前置任务（前置 → 当前）
- `warnings`：超 80 节点截断时含 i18n 提示

边邻接用于 BFS：正向 next、逆向 next、upstream、以当前为前置的依赖方。同 `parentName` 仅展示，不造边。

### 3.3 模块与扩展点

- 后端索引：`buildNextQuestIndex` / `buildUpstreamIndex` / `buildMinLevelIndex`（单次请求内扫描 XML）。
- 前端节点：`QuestChainNode.vue`；回调经 node `data.onSelect` / `data.onEdit`。
- 图标：复用 `ItemIcon` + `mapleStoryAPI`，无后端 URL。

## 4. 资源与同步

- 不改 WZ/客户端 IMG；图标走本地 `/game-assets` 与 maplestory.io CDN 回退。
- 无多语言资源目录同步需求。

## 5. 编码与 i18n

- 服务端：`QuestAdminService.warning.chainTruncated`
- 前端：`quest.card.*` / `quest.chain.*` / `quest.links.chain` / `quest.icon` 等（zh-CN / en-US）

## 6. 产物清单

| 类型 | 路径 | 说明 |
| --- | --- | --- |
| 文档 | README.md | 本文件 |

## 7. 验证记录

- `gms-ui`：`yarn type:check` 通过（vue-tsc --noEmit --skipLibCheck）
- `gms-server`：`mvn -pl gms-server -DskipTests compile`（JDK 21 + 仓库 `.tools/apache-maven-3.9.6`）
- 手工建议：打开任务管理 → 卡片见 NPC/下一任务 → 详情概览见图标 → 链路 Tab 画布点击/编辑

## 8. 预留、风险与回滚

- 超大连通分量截断为 80；需更大图时可调 `CHAIN_NODE_LIMIT` 或做分页/按系列过滤。
- 列表每次扫 Act.img 建 next 索引，任务量极大时可能偏慢（可后续缓存）。
- 回滚：还原 quest 页与 `getQuestChain` 相关类即可，无 DB 迁移。

## 9. 对外交流摘要

任务管理改为卡片列表（含 NPC 头像与下一任务），详情补道具/怪物图；新增完整链路 API，关联 Tab 用 Vue Flow 横向展示 nextQuest + 前置连通图，节点可跳转详情与编辑。
