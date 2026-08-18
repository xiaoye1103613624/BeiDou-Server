-- 白金锤永久升级次数（还原卷轴不清零）
ALTER TABLE inventoryequipment
    ADD COLUMN platinum TINYINT NOT NULL DEFAULT 0 COMMENT '白金锤已用次数(永久加槽)' AFTER vicious;
