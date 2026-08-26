-- Shared admin icon cache (item / npc / mob). Replaces ad-hoc per-feature sync.
CREATE TABLE IF NOT EXISTS `xy_icon_cache` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `category` VARCHAR(16) NOT NULL COMMENT 'item / npc / mob',
    `object_id` INT NOT NULL COMMENT '物品/NPC/怪物 ID',
    `version` INT NULL DEFAULT NULL COMMENT '来源版本号（如 227）',
    `region` VARCHAR(16) NULL DEFAULT NULL COMMENT '来源区服（GMS/CMS 等）',
    `icon_data` LONGBLOB NOT NULL COMMENT 'PNG 二进制',
    `content_type` VARCHAR(64) NOT NULL DEFAULT 'image/png' COMMENT 'MIME',
    `source` VARCHAR(32) NULL DEFAULT NULL COMMENT 'maplestory.io / dvg / local',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_category_object` (`category`, `object_id`),
    KEY `idx_category` (`category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='后台共用图标缓存';

-- 兼容旧表名 xy_game_icon（若曾手工建过）
CREATE TABLE IF NOT EXISTS `xy_game_icon` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `category` VARCHAR(16) NOT NULL,
    `object_id` INT NOT NULL,
    `version` INT NULL,
    `region` VARCHAR(16) NULL,
    `icon_data` LONGBLOB NOT NULL,
    `content_type` VARCHAR(64) NOT NULL DEFAULT 'image/png',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_category_object` (`category`, `object_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='legacy icon cache（已迁移到 xy_icon_cache）';

INSERT IGNORE INTO `xy_icon_cache`
(`category`, `object_id`, `version`, `region`, `icon_data`, `content_type`, `source`, `update_time`)
SELECT `category`, `object_id`, `version`, `region`, `icon_data`, `content_type`, 'legacy', `update_time`
FROM `xy_game_icon`;
