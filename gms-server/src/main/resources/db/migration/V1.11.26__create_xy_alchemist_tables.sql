-- 药剂师（炼药）系统：账号级体力表 + 角色级炼药师等级/经验表
CREATE TABLE IF NOT EXISTS `xy_account_stamina` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `account_id` INT NOT NULL COMMENT '账号ID（账号级共享，同账号所有角色共用一份体力）',
    `stamina` INT NOT NULL DEFAULT 0 COMMENT '当前体力值，上限1000',
    `last_refill_date` DATE DEFAULT NULL COMMENT '上次每日体力发放日期，用于判断今日是否已领取',
    `create_time` DATETIME NOT NULL COMMENT '创建时间',
    `update_time` DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_account_id` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账号级体力表（炼药消耗体力，账号通用）';

CREATE TABLE IF NOT EXISTS `xy_character_alchemist` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `character_id` INT NOT NULL COMMENT '角色ID（炼药师等级/经验按角色隔离）',
    `exp` BIGINT NOT NULL DEFAULT 0 COMMENT '炼药师累计经验值，只增不减，升级不重置',
    `create_time` DATETIME NOT NULL COMMENT '创建时间',
    `update_time` DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_character_id` (`character_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色级炼药师（药剂师副职业）等级经验表';
