# 客户端·座椅设置 — 实现笔记

## 决策摘要

| 项 | 选择 |
| --- | --- |
| A1 | 改 wz XML + wz-patch 打客户端 img |
| B1→3A | 娃娃 = **本地 Character.wz 合成**（`/characterDoll/v1`，见人偶系统）：`CharacterWzPartStore` 读 WZ 部位节点+origin+z，`PoseFrameExtractService.ensureCharacterPartFrame` 抽客户端 `.img` 像素，`DollCompositor` 按 `zmap` 叠合成 WZ 1:1 透明人偶，返回 `bodyOrigin`/`navel` 锚点；`posePreviewLayout`/`DollStage` 据此精确挂载；禁止 inventory icon 入舞台；失败回退剪影（坐标/比例仍正确）；预览可缩放、可拖拽 |
| C123 | Install 301xxxx + effect2/特殊椅 + Character.wz/TamingMob 坐姿锚点 |
| D1 | 无插件 |
| E1 | 读+调+写+同步客户端全闭环 |

## 节点语义

### 椅子 `Item.wz/Install`

- 道具 ID：`301xxxx`（XML `imgdir` 名多为 `0301xxxx`）
- 可调：`effect` / `effect2` 的 `origin`(x/y)、`pos`、`z`（层与首帧 canvas 均可）
- 名称：`String.wz/Ins.img.xml`，键为无前导零 ID（如 `3010000`）

### 骑宠视觉 `Character.wz/TamingMob`

- **不改** `TamingMob.wz` 的 speed/jump/fs 等移动属性
- 可调：各动作帧 canvas 下 `map/navel` 以及必要的 `origin` / `z`
- 本仓库无独立 `sit` 动作；角色挂点以 `stand1`（或首个含 navel 的动作）为主编辑目标

### 双语映射

- `wz/` → 客户端 `EN/`
- `wz-zh-CN/` → 客户端 `Data/`

## 开关

- `allow_chair_editor_write` / `gms.dev.chair-editor.enabled`：写服务端 XML
- `allow_chair_client_write`：客户端 patch apply（默认 false）

## Patcher

优先 `xml-img-patcher.exe`；缺失时降级导出到 `docs/features/client-chair-pose/patches/`（与 Quest/Skill 一致）。

## 迁移

`V1.11.59__chair_pose_config.sql`（文档早期写的 V1.11.57 已被商城占用，以 59 为准）。

## 预览链路（2026-09-17 修复）

1. `ChairPosePreviewService` → `PoseFrameExtractService.ensureChairEffectFrame` / `ensureTamingFrame`
2. 成功 → mode=`EFFECT_PNG`，`imageUrl=/game-assets/pose-frame/...`（尺寸须等于 XML canvas）
3. 失败 → mode=`ICON_FALLBACK`，**不返回 imageUrl**（避免 34×33 icon + 122×105 origin）
4. 前端 `resolveEffectStageUrl` + `isWzScaleImage` 双重门禁；人偶走 `DollStage`（组件内 `posePreviewLayout` 几何：椅子=bodyOrigin 对齐 effect attach，坐骑=navel 对齐 map/navel）
