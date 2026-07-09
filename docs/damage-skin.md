# 伤害皮肤（Damage Skin）实现说明

北斗 v083 全栈伤害皮肤功能，协议与 Kaentake/Cosmic v83 参考实现一致。

## 功能概览

| 能力 | 说明 |
|------|------|
| 商城目录 | 服务端从 WZ 扫描 BasicEff.img/damageSkin 写入 damageskin_catalog |
| 个人库存 | damageskin_inventory 记录已拥有皮肤 |
| 当前装备 | characters.activeDamageSkin（0 = 默认数字） |
| 打开选择器 | 使用现金道具 5910000（itemType 591，不消耗） |
| 客户端渲染 | 插件 hook Effect_HP / Effect_Miss，按皮肤 ID 替换伤害数字贴图 |
| 地图同步 | 进图/换图时广播当前皮肤给其他玩家 |

## 封包协议

### 客户端 -> 服务端

| Opcode | 值 | 说明 |
|--------|-----|------|
| DAMAGE_SKIN_APPLY | 0x0110 | 装备皮肤（int skinId） |
| DAMAGE_SKIN_PURCHASE | 0x0111 | 购买皮肤（int skinId） |

### 服务端 -> 客户端

| Opcode | 值 | 说明 |
|--------|-----|------|
| DAMAGE_SKIN_CATALOG | 0x0170 | 商店目录 |
| DAMAGE_SKIN_INVENTORY | 0x0171 | 库存 |
| DAMAGE_SKIN_RESULT | 0x0172 | 操作结果 |
| DAMAGE_SKIN_BROADCAST | 0x0173 | 广播 charId + skinId |

客户端插件通过 PacketDispatcher 仅吞掉 0x170-0x173（及已有的 0x178 世界地图）。

## 服务端改动（gms-server）

### 数据库

gms-server/src/main/resources/db/migration/V1.11.4__damage_skin.sql

### 新增类

- org.gms.client.DamageSkinCatalog
- org.gms.client.DamageSkinInventory
- DamageSkinApplyHandler / DamageSkinPurchaseHandler

### 修改

RecvOpcode, SendOpcode, PacketProcessor, PacketCreator, Character, Server, PlayerLoggedinHandler, UseCashItemHandler, MapleMap, WZFiles

### 构建

cd E:\pro\BeiDou-Server_xy
mvn package -pl gms-server -DskipTests

产物: gms-server/target/BeiDou.jar

## 客户端插件（ijl15.dll）

目录: E:\pro\BeiDou-ijl15\ezorsia\damageskin\

构建 Release Win32 后自动部署到 BeiDou-Client_1\ijl15.dll

需 detours.lib (x86) 放在 ezorsia\detours\

## WZ 资源（需手动）

客户端路径: Effect/BasicEff.img/damageSkin/<id>/

079 源: E:\mxd_soft\2.客户端\083\20大陆_079整合版\20dalu\wz\Effect.wz\DamageSkin.img.xml

1. 服务端: 合并到 gms-server/wz/Effect.wz/BasicEff.img.xml 的 damageSkin 节点
2. 客户端: Data\Effect\BasicEff.img\damageSkin\ 散文件（OrzRepacker/orange-wz）
3. 添加商城道具 5910000 到 Item.wz + String.wz

未合并 WZ 时 catalog 为空，可用 SQL 手动插入库存测试。

## 测试

1. 启动服务端确认 Flyway V1.11.4
2. 登录看 DamageSkinCatalog 日志
3. !item 5910000 或 SQL 插入 damageskin_inventory
4. 打怪看伤害数字；换图/传送确认不崩
5. F12 伤害排行仍可用

## 参考

E:\资料\xiaoye\mxd学习\伤害皮肤\DAMAGE_SKIN_SERVER.md