-- 管理后台怪物/物品图标缓存（从小册子 maplestory.io 按版本同步后持久化）
CREATE TABLE IF NOT EXISTS `xy_game_icon` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT,
    `category`     VARCHAR(16)  NOT NULL COMMENT 'mob / item / npc',
    `object_id`    INT          NOT NULL COMMENT '怪物或物品 ID',
    `version`      INT          NOT NULL DEFAULT 227 COMMENT '同步所用小册子版本号',
    `region`       VARCHAR(16)  NOT NULL DEFAULT 'GMS' COMMENT '地区，如 GMS',
    `icon_data`    MEDIUMBLOB   NOT NULL COMMENT 'PNG 二进制',
    `content_type` VARCHAR(64)  NOT NULL DEFAULT 'image/png',
    `update_time`  DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_xy_game_icon_cat_obj` (`category`, `object_id`),
    KEY `idx_xy_game_icon_version` (`version`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='游戏图标缓存（爆率等管理页）';
