# 选角进图 CANVAS 闪退（Growth tip / quickSlot）

> 状态：已闭环（C：WZ 核对 + EN StatusBar 补齐 + ijl15 nullguard）  
> 日期：2026-09-17  
> 客户端：`E:\MXD\BeiDou-Client_S9`  
> 插件：`E:\project\BeiDou-ijl15`

## 现象

选角色进入游戏后约数秒无提示闪退。日志：

- `0xC0000005` @ `CANVAS.DLL+0xED28`（`ECX=this=0`）
- `stage=set_stage END`
- 崩前大量 `GetObjectA growth-tip ... GrowthEnabled|Disabled`（path-spy 仅记路径，不代表失败）
- `beidou-wz-last`：`suspect_last=UI/StatusBar.img/base/quickSlot`，`getobj_fail` 空

## 根因（校正）

| 先前假设 | 校正结论 |
| --- | --- |
| Data/EN `UIWindow` 缺 `GrowthEnabled/Disabled` | **否**。加密 img 明文 UTF-16 搜不到；orange-wz 解密后 Data/EN **均有**节点且含 PNG |
| Growth GetObject 失败导致空指针 | **否**。崩溃当日 `getobj_fail_hr=0`；Growth 路径在 RecentWZ 中成功出现 |
| 真崩点 | `sub_8DDB30`（KeyConfig / StatusBar quickSlot 绘制）对 **空源画布** 调 `IWzCanvas::Copy`（vtable+0x80）；Copy 内对 null source 调 getter → `CANVAS+0xED28` |
| EN StatusBar | 缺 `quickSlot32/08/26`（Data 有）；已从 Data 拷入 EN |

## 修复（方案 C + Data/EN）

1. **WZ**
   - 确认 Data/EN `UIWindow.img` 已有 `ToolTip/Equip/GrowthEnabled|Disabled`
   - EN `StatusBar.img`：从 Data 追加 `base/quickSlot32|08|26`（备份 `StatusBar.img.bak_growth_20260917`）
2. **插件 nullguard**（IDA 校准 Angel.exe / BeiDou.exe）
   - 新文件 `gamedata/EnterGameCanvasNullGuards.cpp`
   - 在 `0x8DE0EC`：源画布 `[ebp-14]==0` 时 `add esp,10` 跳到 `0x8DE124`，否则重放原 Copy 序列
   - `DeferredBootPatches` 中 `AttachEnterGameCanvasNullGuards()`（并启用此前未挂上的 `AttachSkillTipCrashGuards`）
3. **部署**：`build_once.bat` → `E:\MXD\BeiDou-Client_S9\ijl15.dll`（含字符串 `EnterGameCanvasNullGuards`）

## 验证

1. 启动客户端，控制台应见 `[EnterGameCanvasNullGuards] QuickSlotCopy @ 8DE0EC => e9 OK`
2. 选角进图：不应再出现 `CANVAS.DLL+0xED28`
3. 进图后装备 tip / 快捷栏正常；`beidou-crash.log` 无同址 CRASH

## 回滚

- EN StatusBar：用 `EN\UI\StatusBar.img.bak_growth_20260917` 覆盖
- 插件：用 `ijl15.dll.bak_*` 或旧构建覆盖
- Data/EN `UIWindow.img.bak_growth_20260917`（本轮未改写主文件内容，仅备份）

## 产物

| 路径 | 说明 |
| --- | --- |
| `docs/features/enter-game-growth-tip-crash/` | 本文档 |
| `evidence/2026-09-17-crash-summary.md` | 崩溃摘要 |
| ijl15 `gamedata/EnterGameCanvasNullGuards.*` | nullguard 源码 |
| 客户端 `EN\UI\StatusBar.img`（103347 B） | 已含扩展 quickSlot |
