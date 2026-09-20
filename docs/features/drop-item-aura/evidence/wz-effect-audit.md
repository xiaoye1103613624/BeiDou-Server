# Effect.wz 实测记录（带光柱版）

> 工具：`orange-wz` MCP（`127.0.0.1:10012`），密钥来源 `E:\project\orange-wz\src\main\java\orange\wz\provider\WzAESConstant.java`
> 被测文件：`E:\MXD\扩展改动\掉落特效\Effect.wz`（120355 bytes）

## 密钥

| 项 | 值 |
| --- | --- |
| key name | `083-GMS` |
| IV | `0x4D 0x23 0xC7 0x2B` → base64 `TSPHKw==`（与项目既有文档一致） |
| userKey | `WzAESConstant.DEFAULT_KEY`（128 字节，32 个 int 的 LE 序列） |

> userKey 明文按项目约定**不入库**，需用时从 `WzAESConstant.java` 现算，见 `notes/mcp-integration.md`。

## 节点结构

```
Effect.wz
└── BasicEff.img
    ├── dropItemAura
    │   ├── Rare        ├ front ├ back
    │   ├── Epic        ├ front ├ back
    │   ├── Unique      ├ front ├ back
    │   └── Legendary   ├ front ├ back
    └── dropItemEffect
        └── 0 .. 5
```

## 关键实测数据

| 项 | 实测结果 |
| --- | --- |
| 档位数 | **4 档**：Rare / Epic / Unique / Legendary —— **无 Mythic** |
| 每档 front/back | 均存在；**各 6 帧**（`0..5`） |
| `dropItemEffect` | **6 帧**（`0..5`） |
| `MAX_FRAMES=16` | 实际最多 6 帧，**上限充足**，无需上调 |
| aura front 画布 | ≈ `72×33` ~ `73×34` |
| **dropItemEffect 画布** | 帧 3 = `177×145`，帧 4 = `115×128`，帧 5 = `31×130` —— **远大于此前 webp 预览的判断** |
| WZ `z` 属性 | aura 与 effect 节点**均为 `0`** |
| 每帧 `delay` | **`90` ms**（mod 里 `AURA_FRAME_MS = 120`，不一致） |
| 每帧 `origin` | 有，如 `Rare/front/0` origin = `(35, 20)`；**mod 完全未使用** |
| pngFormat | `ARGB8888` |

## 由实测引出的结论修正

1. **档位冲突（阻塞）**：资源只有 4 档，而已确认决策为 5 档（含 Mythic）。必须二选一 —— 服务端削到 4 档，或补齐 Mythic 资源。
2. **「不越 UI」口径 B 风险上调**：此前按 webp 预览（≤48px 高）判为低风险；实测 `dropItemEffect` 高达 **145px**（v83 画面 800×600），落地特效**会明显占据屏幕空间**，视觉侵入 UI 区域的风险为**中**，需评估裁剪/定位策略。
3. **「WZ 内嵌 z 太高会盖 UI」的说法在本资源上不成立**：实测 WZ `z` 已为 `0`，mod 强制 `z=0` 对这两个节点实际无改变。真正决定是否盖 UI 的是 `CAnimationDisplayer::LoadSingleLayer` 传入的 z 与 UI 层边界，**仍需 IDA 实测**（见 `ida-notes.md`）。
4. **新增缺陷：`origin` 未使用**。mod 直接用 `(dropX, dropY)` 定位，未按 WZ `origin` 做偏移 → 光环与道具不对齐。这是「挂载到道具上」的又一具体缺失。
5. **帧速不一致**：资源 `delay=90`，mod 用 `120`，播放偏慢，应改为读取 WZ `delay`。

## 复用发现

`E:\project\orange-wz\src\main\java\orange\wz\MergeDropItemAuraFromWz.java` 已存在现成的合并工具：
- 目标节点 `dropItemAura`、`dropItemEffect`
- 先移除同名旧节点再 `deepClone` 合并（避免重复子树）
- 自带 `BasicEff.img.bak_dropaura` 备份、`verify`（会校验 5 档，Mythic 报 MISSING）、目标文件被占用时落 `BasicEff.img.dropaura_pending` 并提示手工替换

→ 资源合入（计划 D1）**可直接复用该工具**，无需重写。
