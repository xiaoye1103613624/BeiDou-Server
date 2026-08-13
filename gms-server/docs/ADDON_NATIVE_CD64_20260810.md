# 原生扩（Route A / CD64）— 2026-08-10 状态与剩余问题

> **Date:** 2026-08-10
> **Parent:** `BeiDou-ijl15/docs/NATIVE_EXTEND_PLAN_CLIENT1_20260807.md` · `ADDON_NATIVE_CD_GROW.md`
> **Live:** EXE `ACF10F63` (vanilla CD) · ijl15 sidecar 模式 · 服务端 `NATIVE_CD64_LIVE=false`

---

## 1. 本轮已落地（2026-08-10）

| # | 修改 | 文件 | 说明 |
|---|------|------|------|
| 1 | 服务端原生开关 | `ExtendedEquipRegistry.java` → `NATIVE_CD64_LIVE`（默认 **false**） | 原生扩上线后客户端会原生折叠 −52..−62（EXE 长数组 walker），服务端盲区 `STAT_CHANGED` 注入必须翻转，否则双算。`isClientBlindEquipSlot` 已按此开关分流。**默认 false = 现状不变** |
| 2 | 客户端 DetectCd64 三标记加固 | `shoulders.cpp` `DetectCd64Exe()` | 原只查 slab `push 0x700 @778F02`；现要求三标记齐备：slab 0x700 **+** GetItem bound `0xC1`（−63）**+** apply-max `0x3E`（62）。防 vanilla 假阳进入原生库存路径（avoid S2/S7）。半截 EXE（仅 slab）回落 shadow/sidecar，安全 |
| 3 | 客户端 Lite 检测同步 | `equipaddon.cpp` `DetectCd64ExeLite()` | 仅日志/标记用途，与 shoulders 主检测对齐，防日志误导 |

**验证：** 服务端 `mvn -o compile` → BUILD SUCCESS；客户端插件已由用户打包（本回合不部署 live）。

---

## 2. 剩余问题 → 责任人 / 闸门（原生扩路线）

| # | 问题 | 责任人 | 闸门 / 前置 |
|---|------|--------|-------------|
| P0 | **开装备栏 CreateLayer 闪退（DX9 proxy）** | 用户 | Gr2D A/B：回滚 `Gr2D_DX8.dll.bak_before_DX9proxy_*` 冷启，或 IDA 查 `dword_BF14EC`/CreateLayer 坏 COM。**未绿不开 CD 补丁** |
| P1 | EXE Route A 补丁（ctor×4 / bound×5 / slab / stack / login clear / cash lea / apply-max） | 代码+用户 | IDA → bak → ExpectBytes → 补丁 → **enter A/B**。禁 FULL×318（boot-red） |
| P2 | 配对 DLL：CD64 时 sidecar 整段 OFF | 已具备（`NativeCd64Inventory()` / `UseNativeCd64Slots()`） | 随 P1 的 EXE 一起部署 |
| P3 | **服务端 flip `NATIVE_CD64_LIVE=true`** | 代码 | P1 在 Client_1 enter-green 后+用户确认 → 盲区注入关闭（防双算） |
| P4 | T2（classic+cash ≥30s）/ T3（relog） | 用户 | P1 后 CD64_TEST 或 Client_1 直接跑 |
| P5 | −62/aux wire（`GREEN_ENTER_OMIT_AUX62`） | 保持 false | 已放行；进图后有效 |

## 3. 服务端 flip 检查清单（P3 执行时）

1. 确认 Client_1 EXE = CD64（`DetectCd64` 三标记为真，slab `0x700` + bound `0xC1` + apply-max `0x3E`）。
2. 将 `ExtendedEquipRegistry.NATIVE_CD64_LIVE` 置 `true` 并重编服务端。
3. 验证：−52..−62 穿着时服务端**不再**对盲区注入 STAT_CHANGED（四维与客户端原生折叠一致，无双算）。
4. 回归：经典槽（帽子/武器）四维与之前一致；−54..−62 穿/卸/重登座位与物品 ID 逐格一致。
5. 出问题回滚：改回 `false` 重编（双端代码均为开关门控，无残留路径）。