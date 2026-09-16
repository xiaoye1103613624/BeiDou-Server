# 客户端·座椅设置（client-chair-pose）

管理端「客户端 → 座椅设置」：浏览/调整椅子 `effect`/`effect2` 坐姿锚点与骑宠 `map/navel`，开发态双写服务端 WZ，并可 dry-run/apply 同步到客户端 Data/EN。

## 范围（E1 闭环）

| 能力 | 说明 |
| --- | --- |
| 椅子列表/详情 | `Item.wz/Install` 下 `301xxxx`（含 `effect2`） |
| 骑宠列表/详情 | `Character.wz/TamingMob` 视觉锚点（**不改** `TamingMob.wz` 移动属性） |
| 预览 | 优先客户端旁路 PNG / 本地 game-assets；失败降级 icon + 十字线/剪影仍可调数值 |
| 写服务端 | 双写 `wz` + `wz-zh-CN`（文件存在时） |
| 客户端同步 | dry-run / apply；Data↔zh-CN、EN↔wz；优先 `xml-img-patcher.exe` |

## 开关（生产默认关写）

1. **写服务端**：`gms.dev.chair-editor.enabled: true` **或** `game_config.allow_chair_editor_write = true`
2. **写客户端 apply**：`game_config.allow_chair_client_write = true` + 已配置「客户端路径」
3. **Patcher（可选）**：`chair_xml_img_patcher_path` / 共用 quest·skill 路径 / 仓库默认；缺失则降级导出 XML 到本目录 `patches/`

## API（`/clientChairPose/v1`）

- 状态：`status`
- 椅子：`chairs/list` · `chairs/detail` · `chairs/write` · `chairs/preview`
- 骑宠：`tamingMobs/list` · `tamingMobs/detail` · `tamingMobs/write` · `tamingMobs/preview`
- 同步：`patch/dryRun` · `patch/apply`

## 手工验收

见文末「手工验收」；迁移 `V1.11.57__chair_pose_config.sql`。

## 已知限制

- 服务端 Install/TamingMob XML 的 `<canvas>` 通常无像素；完整特效帧解码依赖客户端旁路 PNG 或后续接入 img 解码器，默认降级 icon+十字线。
- 无 `xml-img-patcher.exe` 时不会改客户端 `.img`，只导出 XML 清单。
- 骑宠无独立 `sit` 动作时，默认编辑 `stand1`（或首个含 navel 的动作）。
- 客户端文件占用时 patcher 可能失败，需关客户端后重试。

## 关键代码

- 后端：`ClientChairPoseController`、`ClientChairPoseService`、`ChairPoseWzXmlStore`、`TamingMobPoseWzXmlStore`、`ChairPoseClientSyncService`、`ChairPosePreviewService`
- 前端：`gms-ui/src/views/client/chairPose/`、`api/clientChairPose.ts`
- 迁移：`V1.11.57__chair_pose_config.sql`
- 笔记：`notes/IMPLEMENTATION.md`
