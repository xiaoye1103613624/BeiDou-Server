-- 装备破界系统：破界+N（N=使用装备强化卷2439102 / 时装强化卷2439101的次数，0~50）
-- 普通装备每次消耗 2439102*1；点装装备每次消耗 2439101*1；成功时 N+1（封顶 50）
-- 每次破界成功重掷 13 属性池：每条独立激活(+固定值)或 +0，本次激活结果【覆盖】上一次，不累计。
-- breakthroughPool：13 位掩码 bit i=1 表示该条当前激活（+其固定值）。
ALTER TABLE inventoryequipment
    ADD COLUMN breakthrough TINYINT NOT NULL DEFAULT 0 COMMENT '破界等级 0~50（使用强化卷次数）' AFTER gemTypes,
    ADD COLUMN breakthroughPool SMALLINT NOT NULL DEFAULT 0 COMMENT '破界13属性激活掩码(bit0~12)' AFTER breakthrough;
