# 收纳背包（Storage Bag）

> 适用：**GMS v083** 北斗服务端 + **BeiDou-ijl15** 客户端插件  
> **完整文档**：[萧曳冒险岛/收纳背包-完整实现指南.md](file:///E:/资料/xiaoye/mxd学习/萧曳冒险岛/收纳背包-完整实现指南.md)  
> **表命名**：[数据库命名规范.md](file:///E:/资料/xiaoye/mxd学习/萧曳冒险岛/数据库命名规范.md)（`V1.11.8` `xy_` 前缀）

## 1. 概要

| 项 | 说明 |
|----|------|
| 入口 | 物品栏 **BAG** 按钮 → 四标签（矿石/卷轴/椅子/坐骑） |
| 容量 | 每类 **200** 格，按角色 `inventoryitems` `type` 10–13 |
| Auto | `characters.auto*Storage` 列（V1.11.9）拾取自动收纳 |

## 2. 服务端要点

| 路径 | 说明 |
|------|------|
| `org.gms.server.OreStorage` | 收纳/取出/整理/Auto |
| `org.gms.net.server.channel.handlers.BagWindowHandler` | Recv `0x3724` |
| `org.gms.util.PacketCreator.bagWindowSnapshot` | Send `0x3725` |
| `org.gms.server.StorageInventory` | `slotLimit` 改为 `int`（>127 堆叠） |
| `db/migration/V1.11.8__xy_table_prefix.sql` | beauty/damageskin 表 → `xy_*` |
| `db/migration/V1.11.9__storage_bag.sql` | Auto 列、清理旧 `orestorages` |

## 3. 协议（Case C）

| 方向 | Opcode |
|------|--------|
| C→S | **0x3724** (`RecvOpcode.BAG_WINDOW`) |
| S→C | **0x3725** (`SendOpcode.BAG_WINDOW`) |

## 4. 客户端

- 模块：`ezorsia/storagebag/` + `ModRegistry` / `LazyCompatInit`
- `compat/ztl/zalloctex.cpp`：`ZAllocStrSelector<wchar_t>` @ `0x00BF0BA8`
- WZ：Case C APPEND `Data/UI/UIWindow.img` → `Bag`（合并后 **12,219,630 B**）
- Release `ijl15.dll` **629,248 B**

## 5. 构建验证

```bash
mvn compile -pl gms-server -am
```

Flyway 顺序：`V1.11.8` → `V1.11.9`；与美容院/每日签到/伤害皮肤无 opcode 冲突。
