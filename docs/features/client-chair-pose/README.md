# 客户端·座椅设置（client-chair-pose）

管理端「客户端 → 座椅设置」：浏览/调整椅子 `effect`/`effect2` 坐姿锚点与骑宠 `map/navel`，开发态双写服务端 WZ，并可 dry-run/apply 同步到客户端 Data/EN。预览 = **WZ 1:1 effect/stand 画布**（`PoseFrameExtractService` / `DumpPoseFrame` → `pose-frame` 缓存，模式 `EFFECT_PNG`）+ **本地 Character.wz 合成人偶**（`/characterDoll/v1`，见 [人偶系统](../character-doll/README.md)，按真实 `bodyOrigin` / `navel` 锚点精确挂载）+ 十字线；支持 +/- / 滚轮缩放，可拖拽改 origin/navel。人偶像素缺失时回退剪影（坐标/比例仍正确）。**禁止**把背包图标叠进舞台（`ICON_FALLBACK` 仅人偶+十字线）。旧 maplestory.io CDN 人偶已不再作为主路径。

## 范围（E1 闭环）

| 能力 | 说明 |
| --- | --- |
| 椅子列表/详情 | `Item.wz/Install` 下 `301xxxx`（含 `effect2`） |
| 骑宠列表/详情 | `Character.wz/TamingMob` 视觉锚点（**不改** `TamingMob.wz` 移动属性） |
| 预览 | 仅 `EFFECT_PNG`（pose-frame 1:1）叠本地合成人偶（`/characterDoll/v1`，WZ 1:1）；失败 `ICON_FALLBACK`（不叠 icon）；画布可缩放、可拖拽 |
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

1. 启动服务端，确认 Flyway 跑过 `V1.11.59__chair_pose_config.sql`，侧栏「客户端 → 座椅设置」可见。
2. 打开页面：顶部「编辑器」应为绿（`gms.dev.chair-editor.enabled=true` 或 `allow_chair_editor_write=true`）。
3. 椅子 Tab：搜索 `3010000` → 点开 → 预览出现椅子图 + 本地合成人偶（sit，按 `bodyOrigin` 对齐 effect 挂载点）与十字线 → 可缩放 → 拖拽或改 origin →「保存到服务端 WZ」。
4. 骑宠 Tab：选任一 mob → 预览为人偶挂在 `navel`（sit 近似骑乘，按 `navel` 对齐 `map/navel`）→ 调 navel → 保存（默认动作多为 `stand1`）。
5. 点「客户端 Dry-run」：应返回导出目录/警告；无 patcher 时提示降级导出到 `docs/features/client-chair-pose/patches/`。
6. （可选）配好客户端路径 + `allow_chair_client_write=true` 后 Apply；客户端占用失败则关客户端重试。

## 已知限制

- 服务端 Install/TamingMob XML 的 `<canvas>` 通常无像素；完整特效帧解码依赖客户端旁路 PNG 或后续接入 img 解码器，默认降级 icon+十字线。
- 无 `xml-img-patcher.exe` 时不会改客户端 `.img`，只导出 XML 清单。
- 骑宠无独立 `sit` 动作时，默认编辑 `stand1`（或首个含 navel 的动作）。
- Install 为整文件 `0301.img.xml`：筛选 `itemIds` 仍导出整份。
- 客户端文件占用时 patcher 可能失败，需关客户端后重试。

## 关键代码

- 后端：`ClientChairPoseController`、`ClientChairPoseService`、`ChairWzXmlStore`、`TamingMobPoseWzXmlStore`、`ChairPoseClientSyncService`、`ChairPosePreviewService`；人偶合成 `CharacterDollController`/`CharacterDollService`/`CharacterWzPartStore`/`DollCompositor`（见 [人偶系统](../character-doll/README.md)）
- 前端：`gms-ui/src/views/client/chairPose/`、`api/clientChairPose.ts`、`api/characterDoll.ts`、`components/pose/DollStage.vue`
- 迁移：`V1.11.59__chair_pose_config.sql`
- 笔记：`notes/IMPLEMENTATION.md`
