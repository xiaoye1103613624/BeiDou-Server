-- 七彩棱镜染色记录表：按背包物品实例 inventoryitemid 持久化每个装备的 HSL 染色参数
CREATE TABLE IF NOT EXISTS `coloring_prism_dye` (
    `inventoryitemid` INT   NOT NULL COMMENT '背包物品实例ID，主键，对应 inventoryitems.inventoryitemid',
    `characterid`     INT   NOT NULL COMMENT '角色ID',
    `itemid`          INT   NOT NULL COMMENT '物品模板ID',
    `hue`             FLOAT NOT NULL DEFAULT 0 COMMENT '色相 -180~180',
    `sat`             FLOAT NOT NULL DEFAULT 0 COMMENT '饱和度 -1~1',
    `light`           FLOAT NOT NULL DEFAULT 0 COMMENT '亮度 -1~1',
    PRIMARY KEY (`inventoryitemid`),
    INDEX `idx_characterid` (`characterid`),
    INDEX `idx_char_item` (`characterid`, `itemid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='七彩棱镜染色记录';
