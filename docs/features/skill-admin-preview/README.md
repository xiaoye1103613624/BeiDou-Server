# 技能管理：图标补齐 + 特效预览

Web「客户端 → 技能资源」补齐技能图标，并在技能详情中预览「人偶 stand1 + effect 多帧自动播放」。

## 已确认范围

- 图标：CDN（maplestory.io）+ 本地从客户端 `Data/Skill/{job}.img` 抽 `{skillId}/icon`
- 预览：仅 `effect` 层；按帧 `delay` 自动循环；人偶固定 `stand1`
- 本版不做：`hit`/`ball`、按 `action` 切姿势、afterimage、SkillSkin、变体选择器（嵌套 effect 取首个有 canvas 的变体）

## 图标链路

```
ensureIcons / skillBook.iconUrl
  → game-assets/skill/{id}.png（local）
  → DumpPoseFrame：Skill/{job}.img → {skillId}/icon（client）
  → maplestory.io …/skill/{id}/icon（cdn）
  → booklet（通常跳过 skill）
```

- 后端：`PoseFrameExtractService.ensureSkillIconBytes`、`AssetService.ensureIconBytesWithSource`
- API：`POST /clientSkill/v1/ensureIcons`（返回 `urls` + `sources`）
- UI：加载技能书后静默补图标；工具栏「补齐本页图标 / 强制重抽」；详情抽屉显示缩略图

## 特效预览

```
详情抽屉
  → POST /clientSkill/v1/effectPreview
       SkillWzXmlStore.readEffectFrames（扁平或一层变体）
       DumpPoseFrame → pose-frame/skill/{skillId}/{layer}/{frame}.png
  → POST /characterDoll/v1/render（默认外观，pose=stand1）
  → DollStage：bodyOrigin 对齐，按 delay 切帧循环
```

- 缺客户端路径或无 effect：空态 + i18n 文案
- 首次抽帧可能较慢（按帧起 DumpPoseFrame 进程）；磁盘缓存后秒开

## 关键代码

| 层 | 路径 |
| --- | --- |
| 抽取 | `PoseFrameExtractService`、`PoseFrameFiles.KIND_SKILL` |
| 资产/技能 | `AssetService`、`ClientSkillService`、`SkillWzXmlStore`、`ClientSkillController` |
| DTO | `SkillEffect*DTO`、`SkillEnsureIconsRtnDTO.sources` |
| UI | `gms-ui/.../skillResources/index.vue`、`api/clientSkill.ts`、locale |

## 手工验收

1. 配置客户端 Data 路径（及本机 orange-wz / DumpPoseFrame）。
2. 打开「客户端 → 技能资源」选职业线：节点出现图标（或 CDN/本地抽成功后出现）。
3. 断网且有客户端路径时，「强制重抽」仍能从 Skill.img 补图标。
4. 点有 `effect` 的技能：详情抽屉人偶 stand1 + 特效循环；关闭抽屉停止定时器。
5. 无 effect 技能：预览区空态文案。

## 限制

- 依赖客户端 `Skill/*.img` 与 DumpPoseFrame；缺则仅 CDN 图标、预览空态。
- 深层/非常规 effect 树只取首条可解析序列。
- 不改游戏内技能逻辑与 WZ 写盘语义。
