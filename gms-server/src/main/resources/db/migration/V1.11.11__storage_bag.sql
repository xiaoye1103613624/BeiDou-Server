-- V1.11.9: 收纳背包（须在 V1.11.8 xy_table_prefix 之后）
-- 物品存 inventoryitems type 10-13，无独立 xy_storagebag 表

ALTER TABLE `characters`
    ADD COLUMN `autoOreStorage` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '矿石包自动收纳',
    ADD COLUMN `autoScrollStorage` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '卷轴包自动收纳',
    ADD COLUMN `autoChairStorage` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '椅子包自动收纳',
    ADD COLUMN `autoMountStorage` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '坐骑包自动收纳';

DELETE FROM `inventoryitems` WHERE `type` IN (10, 11, 12, 13) AND `characterid` IS NULL;

DROP TABLE IF EXISTS `orestorages`;
