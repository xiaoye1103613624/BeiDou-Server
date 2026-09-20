# 人偶系统（character-doll）

本地「人偶合成」：用仓库 `Character.wz` 结构数据（origin / map 锚点 / zmap 层序）+ 客户端 `Character/*.img` 像素，服务端按真实 WZ 1:1 坐标合成为一张透明人偶 PNG，从根本上替代原来 maplestory.io CDN 整体渲染图，彻底消除人偶偏上/偏下/偏左右与比例失真，并可作为复用组件支持**装备试穿**。座椅设置与坐骑设置共用同一套人偶与舞台（`DollStage`）。

## 为什么不再用 CDN 人偶

- CDN 渲染图（maplestory.io `navelCenter`+`sit`，约 49×91）与本仓库 WZ 1:1 画布（`00002000.img.xml` sit 实测约 39×59）比例不一致 → 缩放时人偶与椅子各自为政，比例失真。
- CDN 不能按本服自定义外观/装备渲染 → 试穿不可用。
- CDN 的「肚脐居中」假设所有坐姿一致，实际不同姿势 `map/navel` 不同 → 偏上偏下偏左右。

本地合成的人偶与任何 `effect`/`stand` 帧天然同像素单位，且返回 `bodyOrigin`/`navel` 两个真实锚点，前端据此精确落位（删除原 `posePreviewLayout` 里硬编码的 `SIT_BODY_NAVEL_X/Y`）。

## 架构

```
UI(DollStage / 人偶页) ──render──▶ /characterDoll/v1
        │                              │
        │                    CharacterDollService
        │                        ├─ CharacterWzPartStore   (读 wz/Character.wz XML：部位/节点/origin/map锚点/zmap)
        │                        ├─ PoseFrameExtractService.ensureCharacterPartFrame (orange-wz DumpPoseFrame 抽客户端 .img 像素)
        │                        └─ DollCompositor        (按锚点对齐 + zmap 升序叠加 → WZ 1:1 透明 PNG)
        │                              │
        └──◀ dollUrl / width / height / bodyOriginX,Y / navelX,Y / zOrder / missingParts ── PoseFrameFiles 缓存
```

## 坐标模型（与游戏内绘制一致）

> 精灵左上 = attach − origin

- **椅子**：`attach` = 椅子 effect canvas 的 origin 对应点 = 角色身体原点。前端把人偶 `bodyOrigin` 钉在舞台中心，effect 的 `origin` 钉在同一中心 → 人偶脚底对齐椅子座面。
- **坐骑**：`attach` = 坐骑 `map/navel` 对应点。前端把人偶 `navel` 钉在「中心 + 坐骑 navel」 → 角色肚脐坐在坐骑上。
- 合成 PNG 自身记录 `bodyOrigin` 与 `navel` 相对 PNG 左上的坐标，所有缩放只作用于整层 CSS transform，不破坏像素级相对关系。

## 后端接口

`POST /characterDoll/v1/render`

请求 `CharacterDollReqDTO`：`skinId`、`faceId`、`hairId`、`equipIds[]`、`pose`(sit/stand1/walk1…)、`frame`、`refresh`。

返回 `CharacterDollRtnDTO`：

| 字段 | 含义 |
| --- | --- |
| `mode` | `DOLL` 合成本地人偶；`NONE` 无法合成（缺客户端像素） |
| `imageUrl` | WZ 1:1 合成人偶 PNG（`/game-assets/pose-frame/doll/...`） |
| `width`/`height` | 合成画布尺寸（WZ 像素） |
| `bodyOriginX/Y` | 身体原点（脚底锚）相对 PNG 左上 |
| `navelX/Y` | 肚脐锚点相对 PNG 左上（坐骑挂载点） |
| `zOrder` | 实际叠加顺序（节点名 + z），校验/排障 |
| `missingParts` | 缺像素的部位节点路径（多为对应客户端 `.img` 缺失） |
| `lookKey` | 外观缓存键（`skin-face-hair-equips` 稳定摘要） |

`mode=NONE` 时仍返回 `width/height/bodyOriginX/Y/navelX/Y`：几何只依赖结构数据，缺像素也能算出来，前端在降级时仍保持正确的对齐与比例（只缺像素图，显示剪影）。

## 关键代码

- 后端：`CharacterDollController`、`CharacterDollService`、`CharacterWzPartStore`、`DollCompositor`（`service/doll/`）
- 抽取扩展：`PoseFrameExtractService.ensureCharacterPartFrame` + `PoseFrameFiles.dollPngPath/dollPartPngPath`
- 默认外观：`CharacterWzPartStore.DEFAULT_*`；前端 `renderDefaultCharacterDoll`（skin 2000 / hair 30000 / face 20000 / 默认装备 1040036·1060026·1070003）
- 前端：`api/characterDoll.ts`、`components/pose/DollStage.vue`、`utils/posePreviewLayout.ts`、`views/client/doll/`

## 性能 / 可靠性

- 合成在服务端一次性完成并缓存（热路径仅为文件读 + 几何一致性校验），避免每帧重抽像素（`DumpPoseFrame` 起 java 进程有超时，必须靠缓存规避）。
- 缓存键含外观摘要；改过 WZ 后加 `refresh=true` 强制重合成；几何（尺寸）变化也触发重合成。
- 客户端 `Character.wz` 像素缺失 → 降级 `NONE`/剪影，不阻断编辑。

## 手工验收

1. 启动服务端并配好「客户端路径」，前端「客户端 → 人偶 / 外观预览」可见。
2. 默认外观（皮肤 2000 / 发型 30000 / 脸型 20000 / 默认装备）合成出 WZ 1:1 人偶；左下角读数显示 `WZ <w>×<h> · origin(x, y)`，十字线为身体原点。
3. 切换姿势（sit/stand1/walk1）人偶随之变；滚轮缩放，人偶像素与棋盘格保持 1:1（无模糊、无拉伸）。
4. 在「装备」里输入 `1040036` 等 itemId 点「添加」→ 人偶实时穿上（试穿）；`missingParts` 非空时显示缺像素部位清单。
5. 座椅设置椅子 Tab：人偶 `bodyOrigin` 对齐椅子 effect 挂载点，拖拽改 origin 时人偶与椅子相对位移正确（不再偏上/下/左右）。
6. 坐骑设置 Tab：人偶 `navel` 对齐坐骑 `map/navel`，调 navel 时人偶坐姿随坐骑起伏正确。
7. 删 `game-assets/pose-frame/doll/` 缓存后再次请求会重新合成（验证 `refresh` 与缓存命中）。

## 已知限制

- 人偶外观严格受限于本仓库 `Character.wz` XML 与客户端 `.img` 是否齐备；缺件只缺像素，锚点坐标仍来自结构数据。
- 装备覆盖规则按 `zmap` 层序 + `group`，与官方绘制顺序近似；个别特殊装备的层级可能需要后续微调 `zRank`。
- 复合发型/脸/装备的 uol 引用解析按本仓库 XML 实测结构（`../`、`../../`），遇到非常规 uol 路径可能漏抽对应精灵。
