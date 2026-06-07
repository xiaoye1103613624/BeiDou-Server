-- 在库存装备表中增加强化等级字段，使强化等级随装备持久化
ALTER TABLE `inventoryequipment`
    ADD COLUMN `enhance_level` INT NOT NULL DEFAULT 0 COMMENT '强化等级（0=未强化）'
