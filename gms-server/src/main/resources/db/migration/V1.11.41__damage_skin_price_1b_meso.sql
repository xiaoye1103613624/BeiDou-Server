-- 伤害皮肤商店：单价改为 10 亿金币（1_000_000_000 meso）
ALTER TABLE `xy_damageskin_catalog`
    MODIFY COLUMN `priceMesos` BIGINT NOT NULL DEFAULT 1000000000 COMMENT '商店价格（金币）';

UPDATE `xy_damageskin_catalog`
SET `priceMesos` = 1000000000
WHERE `priceMesos` <> 1000000000;
