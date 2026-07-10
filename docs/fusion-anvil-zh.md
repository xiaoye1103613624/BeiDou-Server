# 融合外观（Fusion Anvil）

> 适用：**GMS v083** 北斗服务端 + **BeiDou-ijl15** 客户端补丁  
> **完整文档**：[萧曳冒险岛/融合外观-完整实现指南.md](file:///E:/资料/xiaoye/mxd学习/萧曳冒险岛/融合外观-完整实现指南.md)  
> **数据库**：[数据库迁移规范](file:///E:/资料/xiaoye/mxd学习/萧曳冒险岛/数据库迁移规范.md) — `V1.11.10` 扩列（非 `xy_` 表）

## 1. 要点

| 项 | 说明 |
|----|------|
| 道具 | 现金 **`5900000`**，双击打开 `Synthesizing` UI |
| 协议 | C→S **`0x4F`** `USE_CASH_ITEM`，`itemType == 590` |
| DB | `inventoryequipment.anvilItemId`（Flyway `V1.11.10__fusion_anvil.sql`） |
| 客户端 | `ezorsia/fusionanvil/`；packet hooks @ DllMain，UI @ `LazyCompatInit` |
| WZ | Case C APPEND `UIWindow.img/Synthesizing`；`0590.img` via XmlToImg |

## 2. 服务端文件

| 路径 | 说明 |
|------|------|
| `org.gms.client.inventory.Equip` | `anvilItemId` |
| `org.gms.client.inventory.ItemFactory` | 24 列 equip INSERT/LOAD |
| `org.gms.net.server.channel.handlers.UseCashItemHandler` | 590 分支 |
| `org.gms.util.PacketCreator` | `addItemInfo` / `addCharEquips` |
| `wz/String.wz/Cash.img.xml` | `5900000` 英文 |

## 3. 客户端

- `FusionAnvil::EnsurePacketHooks()` — 结构体 patch + Decode（早）
- `FusionAnvil::EnsureHooks()` + `EnsureTooltipHooks()` — 对话框与 GBK tooltip「外观/幻化」
- `EnsureItemIconHooks()` — **disabled**（`kItemIconHookEnabled = false`）
- Release **`ijl15.dll` 649,216 B**（643584 → 651264 → 649216）

## 4. 构建

```bash
mvn compile -pl gms-server -am
```

Flyway：`V1.11.10` 在 `V1.11.9`（收纳袋）之后；与美容/签到/伤害皮肤 opcode 无冲突。
