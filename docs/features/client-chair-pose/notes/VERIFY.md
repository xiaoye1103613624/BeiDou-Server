# 验收记录 — client-chair-pose

日期：2026-09-17（预览比例修复：禁止 icon 入舞台，接 PoseFrameExtract）

## 自动检查

- `yarn type:check`（gms-ui）：通过
- eslint `chairPose/index.vue` + `posePreviewLayout.ts` + `mapleStoryAPI.ts`：通过（`--fix`）
- `mvn -pl gms-server -DskipTests compile`：本机 PATH 无 mvn，未跑；Java 改动为 `ChairPosePreviewService` 注入已有 `PoseFrameExtractService`

## 预览验收要点

1. 打开「客户端 · 座椅设置」，选一把椅子（如 `3010047`）：
   - 模式标签应为 `EFFECT_PNG`（已配置客户端 Data + orange-wz DumpPoseFrame），否则 `ICON_FALLBACK`（舞台**不**显示小图标）。
   - `EFFECT_PNG` 时椅子贴图像素尺寸应等于 WZ `effect/0` 的 `width×height`（3010047 → **122×105**，origin 82,55），与坐姿人偶（CDN ≈49×91）同像素坐标系。
   - 人偶不应「漂」在椅子上方；椅子不应显成玩具比例。
   - 红色十字线在舞台中心；人偶身体原点落在十字线（navelCenter 偏 sit navel ≈ -2,-17）；椅子 origin 锚在中心。
2. 骑宠：贴图 origin 在中心；人偶/十字线在 `map/navel`。
3. 刷新前端即可；后端改动需重启 Spring（首次 Dump 可能稍慢，之后走 pose-frame 缓存）。

## 手工检查（需本地 MySQL + 服务启动）

见 [README.md](../README.md)「手工验收」清单。
