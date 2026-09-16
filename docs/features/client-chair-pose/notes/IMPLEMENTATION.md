# 客户端·座椅设置 — 实现笔记

## 决策摘要

| 项 | 选择 |
| --- | --- |
| A1 | 改 wz XML + wz-patch 打客户端 img |
| B1 | 娃娃 = 椅子图 + 坐姿剪影/十字线（非完整换装） |
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
