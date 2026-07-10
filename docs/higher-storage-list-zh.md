# 更高的商店列表（Higher Storage List）

> 适用：**GMS v083** 北斗客户端插件（**纯客户端**，无服务端改动）  
> **完整文档**：[萧曳冒险岛/更高的商店列表-完整实现指南.md](file:///E:/资料/xiaoye/mxd学习/萧曳冒险岛/更高的商店列表-完整实现指南.md)

## 1. 概要

| 项 | 说明 |
|----|------|
| 目标 | 仓库（CUIStorage / Trunk）与随身商人列表从约 **4～5 行** 扩至 **7～8 行** |
| 客户端 | `ezorsia/higherstoragelist/` — 16 处 `Memory::WriteByte`（DllMain） |
| WZ | `PatchStorageBg` 加长 `Trunk/backgrnd` → **463×478** |
| 服务端 | **无改动**；NPC 仓库默认 **4 格** 与 UI 行数无关 |

## 2. 客户端要点

| 路径 | 说明 |
|------|------|
| `higherstoragelist/higherstoragelist.cpp` | 补丁表 + `higher_shop_debug.txt` 日志 |
| `compat/ClientAddresses.h` → `CUIStorage` | IDA 验证地址（2026-07-10） |
| `dllmain.cpp` | `HigherStorageList::ApplyPatches()` @ DllMain |

## 3. WZ 工具

| 路径 | 说明 |
|------|------|
| `gms-server/tools/merge_beauty_img/_patch_storage_bg/` | `PatchStorageBg.exe` — `restore-trunk` / `extend-auto` / `inspect` |

**铁律**：

- **禁止** `shopcopy`（Shop 背景替换 Trunk）
- splice 用 **美术网格 122+40n**，非代码 107+40n
- 修改 `Data/UI/UIWindow.img` 后同步 **`EN/UI/UIWindow.img`**

```bat
PatchStorageBg restore-trunk UIWindow.img UIWindow.img.bak_higher_shop
PatchStorageBg extend-auto UIWindow.img _trunk_work
copy /Y Data\UI\UIWindow.img EN\UI\UIWindow.img
```

## 4. 验证

1. `higher_shop_debug.txt` → `ApplyPatches done: ok=16 fail=0`
2. 仓库 NPC：商人侧 ~8 行、玩家侧 ~7 行；Meso 按钮与滚动条正常
3. SwitchChinese 切英文后背景仍正确

## 5. 关联

- 评估：[更高的商店列表-移植评估.md](file:///E:/资料/xiaoye/mxd学习/萧曳冒险岛/更高的商店列表-移植评估.md)
- 避坑：[常见错误与避坑.md](file:///E:/资料/xiaoye/mxd学习/萧曳冒险岛/常见错误与避坑.md) §更高的商店列表
