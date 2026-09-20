# 融合外观（Fusion Anvil / 幻化）

> 适用：BeiDou-Server_S9 + BeiDou-ijl15 + BeiDou-Client_S9  
> 现金道具：`5900000` 融合外观锻造锤（窗口商城「XY玩法」页）  
> 参考源：`E:\MXD\扩展改动\融合外观`

## 目标

把外观源装备的外形写到基底装备上（`anvilItemId`），保留基底属性；支持还原；自己与他人立即看见正确 AvatarLook。

## 数据流

| 环节 | 位置 |
|------|------|
| DB | `V1.11.16__fusion_anvil.sql` → `inventoryequipment.anvilItemId` |
| 模型/存档 | `Equip#anvilItemId`；`ItemFactory` 读写 |
| 使用 | `UseCashItemHandler` `itemType == 590` |
| 外观广播 | `PacketCreator#addCharEquips` 用 `anvilItemId` 替换 visualId |
| 自身刷新 | `refreshAnvilEquip`：`modifyInventory` + `equipChanged` + **`updateCharLook` 给自己** |
| CharInfo 尾字段 | `addItemInfo` 在 `writeInt(-1)` 后 `writeInt(anvilItemId)`（与 ijl15 Decode 对齐） |
| 查看他人 | `USER_INFO_EX` / `userInfoExEquip` 带 anvil |
| 插件 | `ezorsia/fusionanvil/`：UI、封包、Decode、AvatarLook；还原按钮 `skinPos=-1` |

## 协议（C→S，opcode `0x4F` USE_CASH_ITEM）

```
short position   // 锤子现金槽
int   itemId     // 5900000
short skinPos    // 左槽外观源；-1 = 还原
short basePos    // 右槽基底（可负=已穿戴）
```

校验：两件均为 Equip、不同 itemId、同大类（`itemId/10000`）、外观源须在装备栏未穿戴。成功后消耗外观源与锤子。还原只清 `anvilItemId` 并消耗锤子；无幻化则提示不扣锤。

## 客户端资源

| 资源 | 路径 |
|------|------|
| 融合窗口 UI | `Custom.wz` → `UI/UIWindow.img/Synthesizing/*`（自 Anvil `UI.wz` 同步） |
| 确认音效 | `Data/Sound/UI.img/anvil` |
| 锤子道具 | `Data/Item/Cash/0590.img/05900000`（已有） |
| 插件 | `ijl15.dll`（PostBuild 部署到 `E:\MXD\BeiDou-Client_S9`） |

## 已知限制（本版显式预留）

- **FusionAnvilTip** soft-disable（进图 `0x8007000D` bisect）；主 tip「外观 : xxx (幻化)」可能不显示。对比 tip 仍可由 EquipCompare 部分展示。
- **背包角标 hook** 关闭（历史崩背包）。
- 背包/交易栏图标仍是基底 ID；交易/商店包不带 anvil；还原仅窗口「还原」按钮。

## 相关文档

- 验收：[`notes/VERIFY.md`](notes/VERIFY.md)
- 旧入口（指向本目录）：[`docs/融合外观-武器幻化.md`](../../融合外观-武器幻化.md)
- 选角闪退与 anvil 尾字段：[`docs/features/char-select-crash/`](../char-select-crash/)
