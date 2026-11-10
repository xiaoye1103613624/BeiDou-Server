-- ============================================================
-- Flyway 迁移：为 inventoryequipment 增加 095 潜能/魔方的 5 个列
-- 表：inventoryequipment —— 装备扩展属性表（建表见 V1.0.17__create_inventory.sql）
-- 业务背景：095 潜能/魔方功能，一件装备可带 3 条主潜能 + 2 条附加(bonus)潜能。
--           值语义为「潜能条目 id」，0 = 无潜能/空槽；当前实现由魔方重洗
--           （CashOperationHandler action 0x40）随机生成 1..50，经
--           ItemFactory.saveItems 回写，并在 PacketCreator 的 equip 包尾部
--           以 5 个 writeShort(int16) 下发给客户端。
--           注意：仓库内没有 PotentialOption 表/枚举/WZ 数据，取值目前仅是
--           服务端占位实现，故备注不写「关联某表」。
-- 列顺序约束：必须紧跟在 ringid 之后，对应 ItemFactory 主存档 INSERT 的
--           第 24-28 位；裸 SQL 按位置写入，顺序错了会串列。
--           InventoryequipmentDO 的字段声明顺序与物理列顺序不同，勿按 DO 推算。
-- 说明：本脚本只新增 5 个潜能列及其备注，不重定义其他列；
--       全库备注脚本 V1.9.4 不含这 5 列（避免对尚未建列的库报 Unknown column）。
--       执行前请备份，InnoDB 大表 ALTER 建议在低峰期进行。
-- ============================================================

ALTER TABLE `inventoryequipment`
  ADD COLUMN `potential1`      INT NOT NULL DEFAULT 0 COMMENT '主潜能 1：潜能条目 id，0=无潜能；当前魔方重洗随机取 1..50' AFTER `ringid`,
  ADD COLUMN `potential2`      INT NOT NULL DEFAULT 0 COMMENT '主潜能 2：潜能条目 id，0=无潜能；当前魔方重洗随机取 1..50' AFTER `potential1`,
  ADD COLUMN `potential3`      INT NOT NULL DEFAULT 0 COMMENT '主潜能 3：潜能条目 id，0=无潜能；当前魔方重洗随机取 1..50' AFTER `potential2`,
  ADD COLUMN `bonusPotential1` INT NOT NULL DEFAULT 0 COMMENT '附加(bonus)潜能 1：潜能条目 id，0=无潜能；当前魔方重洗随机取 1..50' AFTER `potential3`,
  ADD COLUMN `bonusPotential2` INT NOT NULL DEFAULT 0 COMMENT '附加(bonus)潜能 2：潜能条目 id，0=无潜能；当前魔方重洗随机取 1..50' AFTER `bonusPotential1`;

-- 表级备注（原建表脚本未写表注释；若线上已有自定义表注释会被覆盖）
ALTER TABLE `inventoryequipment` COMMENT = '装备扩展属性表：一件装备一行，存强化次数/附加属性/装备等级/戒指 id 及 095 潜能（3 主 + 2 附加）';
