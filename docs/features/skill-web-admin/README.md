# 技能 Web 管理（skill-web-admin）

管理端「客户端 → 技能资源」：按职业路线浏览技能书（Vue Flow 连线），开发态可双写服务端 WZ、热重载、同步客户端。

## 怎么用（最短路径）

1. **浏览（只读，无需开关）**  
   打开「客户端 → 技能资源」→ 选系列（冒险家/骑士团/战神）→ 左侧职业线 → 顶部「一转/二转/… · 职业名」Tab → 画布点技能看详情。
2. **开启服务端编辑（改 WZ / 文案 / 热重载）**  
   - `gms-server/src/main/resources/application.yml`：`gms.dev.skill-editor.enabled: true`  
   - **或** `game_config` 表：`allow_skill_editor_write = true`（可热开，OR 关系）  
   - 刷新页面，顶部「编辑器」标签变绿后，抽屉内可保存。
3. **改完技能数值/文案**  
   点技能 → 改字段 →「保存技能数值」/「保存文案」→ 建议再点「重载技能内存」（保存技能数值时会尝试自动重载）。
4. **写客户端（可选）**  
   - `game_config.allow_skill_client_write = true`  
   - 「客户端路径」配好 Data；EN 默认同级 `EN/`，或 `skill_client_en_path`  
   - 先「客户端 Dry-run」，再「客户端 Apply」（游戏客户端占用时可能失败，需关端重试）。
5. **图标**  
   加载技能书后会静默补图标；也可点「补齐本页图标」。CDN/本地失败时节点显示灰占位，不破图。

## 范围（MVP）

| 能力 | 说明 |
| --- | --- |
| 浏览 | 冒险家 / 骑士团 / 战神；龙神·GM·Mob 菜单预留灰显 |
| 画布 | Vue Flow + dagre；边来自 `req`；外置前置虚线 |
| 图标 | `AssetService` → `static/game-assets/skill/{id}.png`（CDN maplestory.io） |
| 写服务端 | `SkillWzXmlStore` 双写 `wz` + `wz-zh-CN`（Skill + String） |
| 热重载 | `POST /clientSkill/v1/reloadSkills` → `SkillFactory.loadAllSkills()` |
| 客户端同步 | dry-run / apply；Data↔zh-CN、EN↔wz；优先 `xml-img-patcher.exe` |

## 开关（生产默认关写）

1. **写服务端 + reload**（任一为 true 即可）  
   - `application.yml`：`gms.dev.skill-editor.enabled: true`  
   - 或 `game_config.allow_skill_editor_write = true`（可热开）
2. **写客户端 patch apply**  
   - `game_config.allow_skill_client_write = true`  
   - 需已配置「客户端路径」（Data）；EN 默认同级 `EN/`，或 `skill_client_en_path`
3. **Patcher（可选）**  
   - `skill_xml_img_patcher_path` / `quest_xml_img_patcher_path`  
   - 或仓库 `.claude/skills/wz-patch-java/xml-img-patcher.exe`  
   - 找不到时：dry-run/apply 仍导出 XML 到 `docs/features/skill-web-admin/patches/`，并提示降级

## API（`/clientSkill/v1`）

- 只读：`lineages` / `jobLines` / `skillBook` / `skillDetail` / `ensureIcons` / `editorStatus`
- 写：`skill/write` / `string/write` / `reloadSkills`
- 同步：`patch/dryRun` / `patch/apply`

目录 DTO（`SkillLineageDTO` / `SkillJobLineDTO` / `SkillJobStageDTO`）含服务端已解析的 `name` 字段，前端优先展示 `name`。

## 手工验收

1. Flyway 跑过 `V1.11.56`；侧栏出现「技能资源」。
2. 选冒险家→英雄线→四转 Tab：画布出现 112 技能与 req 边；Tab 显示「四转 · 英雄」类文案。
3. 点技能（如 1001）打开 Drawer，无「未找到技能节点」；只读可见名称/等级表。
4. 开编辑器开关后：改 masterLevel/文案并保存；点「重载技能内存」。
5. Dry-run：生成 `docs/features/skill-web-admin/patches/<时间戳>/`；Apply 需客户端写开关 + ClientPath。
6. 魂骑士（1112）、战神（2112）同样抽样。

## 已知限制

- 服务端 Skill XML 的 `<canvas>` 通常无像素；图标依赖 CDN/本地 game-assets，非直接 WZ 解码。
- 无 patcher 时不会改客户端 `.img`，只导出 XML。
- 龙神多 skillBook / GM / Mob 未做完整视图。
- 在线 `reloadSkills` 对战斗中角色有风险，仅开发态使用。
- 客户端文件占用时 patcher 可能失败；需关闭客户端后重试（沿用任务同步策略）。
- Skill WZ 节点名可能为 `0001001` 等形式；`findSkillElement` / String 查找已兼容无补零 / `%07d` / `%08d`（抽样：1001→`0001001`，10001001→`10001001`）。
- 异常文案参数经 `I18nUtil` 统一转字符串，避免 MessageFormat 千分位（如 `1,001`）。

## 关键代码

- 后端：`ClientSkillController`、`ClientSkillService`、`SkillWzXmlStore`、`SkillClientSyncService`、`SkillJobCatalog`
- 前端：`gms-ui/src/views/client/skillResources/`、`api/clientSkill.ts`
- 迁移：`V1.11.56__skill_web_admin_config.sql`
