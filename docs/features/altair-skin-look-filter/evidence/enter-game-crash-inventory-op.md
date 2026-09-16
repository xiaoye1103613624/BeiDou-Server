# 进游戏闪退定位（OnInventoryOperation / TSecType::GetData）

- 时间：2026-09-16 00:03:33
- 客户端：`E:\MXD\BeiDou-Client_S9`
- 角色：Admin，穿阿尔泰 `1009912`
- 服务端日志：`阿尔泰皮肤外观过滤已生效：角色=Admin 皮肤=1009912`（00:03:32）→ 约 1s 后客户端崩

## 崩溃摘要

| 项 | 值 |
| --- | --- |
| Exception | `0xC0000005` 读 `0x00000014`（`this=0xC`，`[ecx+8]`） |
| EIP | `BeiDou.exe+0x28743` → `TSecType<long>::GetData` |
| 调用栈 | `CWvsContext::OnInventoryOperation`（`0xA1ECD8` / `0xA1ECFA`） |
| 插件时序 | EquipAddon Init `00:03:32.789` → CRASH `00:03:33.099` |

## 根因

1. `getCharInfo` 已通过 `shouldOmitFromCharInfoEquip` **省略**身体外观槽。
2. `PlayerLoggedinHandler` 随即调用 `syncAltairSkinSelfLook()` → `hideBodyAppearanceOnClient` 对服务端仍装备的身体槽发 **MODIFY mode=3 REMOVE**。
3. 客户端这些槽位本就不存在 → `OnInventoryOperation` 对空/野指针调 `TSecType::GetData` → 闪退。

此前选频道 `00000000.img` 问题已修；本问题是登录路径多发幽灵卸下。

## 修复

- 登录：`syncAltairSkinSelfLook(false)` —— 只刷 AvatarLook，**不**幽灵 REMOVE；仍置 `altairClientBodyHidden`，卸皮肤时走 restore。
- 换装：`equipChanged` → `syncAltairSkinSelfLook()`（默认 true）—— 保留幽灵卸下 + 250ms 补发。
