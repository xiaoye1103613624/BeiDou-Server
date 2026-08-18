-- 装备成长 tip 管理（对齐套装 xy_set_item 范式；DB 覆盖 WZ）
CREATE TABLE IF NOT EXISTS `xy_equip_growth` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `item_id` INT NOT NULL COMMENT '装备物品ID',
    `item_name` VARCHAR(255) DEFAULT NULL COMMENT '展示名（可空，Web 可查 WZ）',
    `enabled` INT NOT NULL DEFAULT 1 COMMENT '0=停用成长 tip/自定义加成',
    `max_level` INT NOT NULL DEFAULT 0 COMMENT '成长上限；0=跟 WZ',
    `sort_order` INT NOT NULL DEFAULT 0,
    `remark` VARCHAR(255) DEFAULT NULL,
    `levels_json` LONGTEXT DEFAULT NULL COMMENT '等级属性/技能 JSON（schemaVersion=1）',
    `skills_json` LONGTEXT DEFAULT NULL COMMENT '全局技能配置 JSON（可选）',
    `source` VARCHAR(32) DEFAULT NULL COMMENT 'WZ/DB/WZ+DB',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_xy_equip_growth_item_id` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='装备成长自定义配置';
