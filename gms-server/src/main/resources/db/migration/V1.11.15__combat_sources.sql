-- V1.11.15: 服务端伤害重算开关 + 强化规则 + 携带物属性

INSERT INTO `game_config`(`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`, `update_time`)
SELECT 'server', 'Game Mechanics', 'java.lang.Boolean', 'use_server_damage_calc', 'false', 'use_server_damage_calc', NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM `game_config` WHERE `config_code` = 'use_server_damage_calc'
);

INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'zh-CN', 'game_config', 'use_server_damage_calc', '是否启用服务端伤害重算（含防御率/无视防御）', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'zh-CN' AND `lang_code` = 'use_server_damage_calc'
);

INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'en-US', 'game_config', 'use_server_damage_calc', 'Enable server-side damage recalculation (PDR/ignore)', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'en-US' AND `lang_code` = 'use_server_damage_calc'
);

CREATE TABLE IF NOT EXISTS `xy_equip_enhance_rule` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `rule_name` VARCHAR(128) NOT NULL,
    `equip_type` VARCHAR(32) NOT NULL DEFAULT 'ALL',
    `min_level` INT NOT NULL DEFAULT 0,
    `max_level` INT NOT NULL DEFAULT 25,
    `stats_json` LONGTEXT DEFAULT NULL,
    `enabled` INT NOT NULL DEFAULT 1,
    `sort_order` INT NOT NULL DEFAULT 0,
    `remark` VARCHAR(255) DEFAULT NULL,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS `xy_carry_item_stat` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `item_id` INT NOT NULL,
    `item_name` VARCHAR(128) DEFAULT NULL,
    `stats_json` LONGTEXT DEFAULT NULL,
    `require_equipped` INT NOT NULL DEFAULT 0,
    `enabled` INT NOT NULL DEFAULT 1,
    `remark` VARCHAR(255) DEFAULT NULL,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_xy_carry_item_stat_item_id` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
