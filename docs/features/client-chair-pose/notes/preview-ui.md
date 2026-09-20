# Chair pose preview UI (2026-09-17, updated)

## Decisions applied

| # | Choice |
| --- | --- |
| 1A | Form column max-width ~380px, left-aligned |
| 2A | Preview dominates right grid column; stage ~70vh / 560px |
| 3A | Fixed default doll via maplestory.io `Character/navelCenter` + `sit` |
| 4A | Zoom +/- buttons, mouse wheel, reset; range 0.5–4 |
| 5B | **Client draw semantics**: sprite top-left = attach − origin（不再把道具图几何居中） |

## Doll / item positioning（与客户端一致）

MapleStory 画布：`draw(x, y)` 时左上角 = `(attachX - originX, attachY - originY)`。

### 椅子（重要）

- **挂点 = 角色身体原点**（客户端画角色/椅子共用的 position），**不是肚脐**。
- WZ 默认 `effect` 首帧 canvas `origin` 本身是对的；预览错位通常是人偶锚点用错。
- 默认人偶用 `navelCenter`（图心≈肚脐）。坐姿 `00002000/sit/0/body/map/navel = (-2,-17)`：
  - 人偶中心放在 `center + (-2,-17)`，使身体原点落在画布中心。
  - 椅子 PNG：`left/top = center - (originX, originY)`。
  - 十字线钉在中心（挂点）。
- 禁止给人偶加 `max-width/max-height`（会破坏与椅子 PNG 的像素坐标系）。
- `z < 0` → 椅子在人偶后；`z >= 0` → 椅子在人偶前。

### 骑宠

- 骑宠 PNG 按 **origin 钉在中心**。
- 人偶/十字线在 `center + (navelX, navelY)`（`map/navel` 相对 origin）——骑宠挂点才是肚脐。
- CDN 无独立 `ride` 时仍用 `sit` 近似。

## List performance

- 椅子：`0301.img.xml` / `Ins.img.xml` 按 mtime 缓存 DOM。
- 骑宠：ID 目录扫描结果缓存；**列表不再逐个 parse XML**（`hasNavel`/`defaultAction` 改详情再算）。语言包 TamingMob 可达数千文件，旧实现切 Tab 会转圈到「无数据」。
- 前端：搜索 280ms 防抖；列表请求序号丢弃过期响应。

## Limitations

- `ICON_FALLBACK` 时图标尺寸 ≠ effect 画布，默认坐标视觉仅近似；有客户端 effect 帧 PNG 时对齐才准。
- GMS/83 CDN 无独立 `ride`；骑宠用 `sit`。
- 人偶依赖 maplestory.io；失败回退剪影。若 CDN 渲染分辨率 ≠ WZ 1:1，坐姿肚脐偏移可能仍有少量误差。
- 本地 Character.wz 完整部件合成未接入本页。
