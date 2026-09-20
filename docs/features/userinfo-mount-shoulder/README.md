# 个人情报坐骑栏：图标 / 护肩错位 / 名称

> 状态：已闭环（待手工进游确认）  
> 适用：GMS v083 / BeiDou S9  
> 项目：BeiDou-Server_s9 + BeiDou-ijl15 + BeiDou-Client_S9  
> 日期：2026-09-17  
> 产物目录：`docs/features/userinfo-mount-shoulder/`

## 1. 背景与目标

- **问题**：个人情报（`CUIUserInfo` 坐骑页）坐骑预览无图；护肩（115）出现在坐骑装备栏；名称显示「坐骑1902242」。
- **目标**：护肩进情报道具列表；只要穿了坐骑就显示预览（无鞍/不适配也画）；名称统一为「小浣猪」。
- **不做**：不迁护肩存档槽；不改装备栏 `CUIEquip` GetSlotXY（见旧功能）；不引入巨坐骑 canvas 缩放（本坐骑帧不大）。

相关旧功能（**仅管装备栏**）：[`../mount-shoulder-pet-ui/`](../mount-shoulder-pet-ui/)。本功能管 **CUIUserInfo**。

## 2. 为何背包能显示「小浣猪」而情报曾显示「坐骑1902242」

两条 UI 都最终读客户端 `String/Eqp.img`，但节点按 **道具 ID** 分开：

| ID | 客户端 `Eqp/Taming/{id}/name`（改前） | 说明 |
| --- | --- | --- |
| **1902000** | 小浣猪 | 经典赤色野猪坐骑，String 里一直有真名 |
| **1902242** | 坐骑1902242 | 后端口径现金坐骑，占位名；`tamingMob=10` 与经典猪同族 |

- **个人情报坐骑页**：`CUIUserInfo::SetTamingMobInfo`（`0x00903DB7`）用当前坐骑 ID（`+0x6CC`，即装备的 1902242）调 `CItemInfo::GetEquipItem`，拷贝 `EQUIPITEM+0x10` 名称 → 必然是占位「坐骑1902242」。
- **背包/tooltip**：同样走 `GetItemName` / `GetEquipItem`，按**悬停那件道具的真实 ID**取名。若背包看到的是「小浣猪」，常见情况是：
  1. 实际看的是 **1902000**（或同名另一件），不是 1902242；或  
  2. 图标相似 / `tamingMob=10` 同族造成「这就是小浣猪」的认知，但情报页严格按 **1902242** 取 String。
- **服务端** `wz-zh-CN` 曾把 1902242 写成「飞天坐骑1902242」，游戏内 UI **不读**服务端 String XML，只读客户端 img；故服务端改名 alone 不会修情报显示。
- **EN 覆盖层** `EN/String/Eqp.img` 体积小、未作为中文名来源；中文客户端以 `Data/String/Eqp.img` 为准。
- **插件**：未发现仅改背包名的 hook；名称差来自 String 节点不一致，不是插件分流。

**本版处理**：按用户指定，将 **1902242** 的客户端与服务端名称统一为「小浣猪」（与 1902000 同名；用户知情）。

## 3. 最终实现逻辑

### 3.1 护肩踢出坐骑页（插件）

- `SetAvatarInfo` @`0x903E90`：`cmp [ebp-10h], 14h` 把 BP20 写入 `+0x6D4`。
- `Memory::WriteByte(0x00903E93, 0x7F)`：BP20 永不命中 → 走 default 进 ITEM LIST（`+0x704`）。
- BP18/19（坐骑/鞍）不变。

### 3.2 放宽坐骑预览门闩（插件）

原版要求坐骑+鞍具且 `IsItemSuitedForTamingMob`（1902242 → bitmask bit 42）。失败则跳过 `SetRidingVehicle` → 无预览。

补丁：

| 地址 | 原行为 | 新行为 |
| --- | --- | --- |
| `0x904058` | 无鞍 → fail | 无鞍 → 创建预览 `0x904087` |
| `0x90406c` | GetEquipItem(鞍) 空 → fail | → 创建预览 |
| `0x904081` | 不适配 → fail | NOP jz，落入创建预览 |

坐骑 id==0 仍跳过（`0x904044`）。

### 3.3 名称「小浣猪」

- 客户端：`Data/String/Eqp.img` → `Eqp/Taming/1902242/name` = 小浣猪（orange-wz `083-GMS` / `TSPHKw==`）。
- 服务端：`wz-zh-CN/.../Eqp.img.xml` = 小浣猪；`wz/.../Eqp.img.xml` = Hog Mount。

## 4. 影响范围

| 层级 | 路径 | 变更 |
| --- | --- | --- |
| 插件 | `BeiDou-ijl15/ezorsia/userinfomount/` | 新建；DeferredBoot 调用 |
| 客户端 | `ijl15.dll`、`Data/String/Eqp.img` | stamp + 名称 |
| 服务端 WZ XML | `wz-zh-CN` / `wz` `String.wz/Eqp.img.xml` | 1902242 名称 |

## 5. 验证门禁

1. 穿护肩 + 坐骑 1902242 → 情报坐骑页右侧无护肩；护肩在道具列表。
2. 仅穿坐骑（可不装鞍）→ 左下预览有图。
3. 情报/背包/tooltip 名称均为「小浣猪」（需**重启客户端**以重载 String）。
4. 回归：装备栏护肩仍在红⑧，坐骑行无护肩。

## 6. 回滚

- 还原 `ijl15.dll`；还原 `Eqp.img`（备份 `Eqp.img.bak_before_xiaohuanzhu_20260917`）。
- 还原服务端 Eqp 字符串；去掉 `UserInfoMount::ApplyPatches()` 调用即可禁用插件侧。
