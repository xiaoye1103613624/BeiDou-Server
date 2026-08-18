-- 装备宝石镶嵌（梅兹 · 宝石镶嵌）：宝1~宝16
-- gemInlay   = 当前镶嵌等级 0~16
-- gemTypes   = 每位(level) 2 bit 记录该级水晶类型：0=力量/1=敏捷/2=智慧/3=幸运
ALTER TABLE inventoryequipment
    ADD COLUMN gemInlay TINYINT NOT NULL DEFAULT 0 COMMENT '宝石镶嵌等级 0~16（宝X）' AFTER infusion,
    ADD COLUMN gemTypes INT NOT NULL DEFAULT 0 COMMENT '每级2bit水晶类型 0力量/1敏捷/2智慧/3幸运' AFTER gemInlay;