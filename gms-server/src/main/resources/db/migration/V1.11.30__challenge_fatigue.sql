-- 三种挑战副本独立每日次数 + 角色挑战日志
CREATE TABLE IF NOT EXISTS `xy_character_challenge_fatigue` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `character_id` INT NOT NULL COMMENT '角色ID',
    `challenge_type` TINYINT NOT NULL COMMENT '1=普通 2=进阶 3=团队',
    `remaining` INT NOT NULL DEFAULT 3 COMMENT '剩余次数，当日可用恢复剂叠加',
    `last_reset_date` DATE NOT NULL COMMENT '上次跨天重置日期，跨天时重置为3',
    `create_time` DATETIME NOT NULL COMMENT '创建时间',
    `update_time` DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_char_type` (`character_id`, `challenge_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色挑战副本次数（按种类独立，跨天重置为3）';

CREATE TABLE IF NOT EXISTS `xy_character_challenge_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `character_id` INT NOT NULL COMMENT '角色ID',
    `account_id` INT NOT NULL COMMENT '账号ID',
    `challenge_type` TINYINT NOT NULL COMMENT '1=普通 2=进阶 3=团队',
    `action_type` VARCHAR(16) NOT NULL COMMENT 'ENTER=进入挑战 RESTORE=使用恢复剂',
    `boss_name` VARCHAR(64) DEFAULT NULL COMMENT 'Boss名称（进入时）',
    `map_id` INT DEFAULT NULL COMMENT '地图ID',
    `mob_ids` VARCHAR(128) DEFAULT NULL COMMENT '怪物ID列表',
    `item_id` INT DEFAULT NULL COMMENT '恢复剂物品ID',
    `remaining_after` INT NOT NULL COMMENT '操作后剩余次数',
    `create_time` DATETIME NOT NULL COMMENT '记录时间',
    PRIMARY KEY (`id`),
    KEY `idx_char_time` (`character_id`, `create_time`),
    KEY `idx_type_time` (`challenge_type`, `create_time`),
    KEY `idx_account_time` (`account_id`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色挑战副本操作日志';
