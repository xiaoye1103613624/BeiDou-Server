-- 师徒系统：拜师/收徒/出师
CREATE TABLE IF NOT EXISTS `xy_mentor` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `mentor_id` INT NOT NULL COMMENT '师傅角色ID',
    `mentee_id` INT NOT NULL COMMENT '徒弟角色ID',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0=在读 1=已出师 2=已解除',
    `mentee_join_level` INT NOT NULL DEFAULT 0 COMMENT '拜师时徒弟等级',
    `mentee_coins_earned` INT NOT NULL DEFAULT 0 COMMENT '徒弟通过升级给师傅赚的师徒币',
    `mentor_coins_earned` INT NOT NULL DEFAULT 0 COMMENT '师傅通过出师获得的师徒币',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '拜师时间',
    `graduate_time` DATETIME NULL COMMENT '出师/解除时间',
    PRIMARY KEY (`id`),
    INDEX `idx_mentor` (`mentor_id`),
    INDEX `idx_mentee` (`mentee_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='师徒关系表';

-- 师傅等级加成配置
CREATE TABLE IF NOT EXISTS `xy_mentor_config` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `config_key` VARCHAR(64) NOT NULL COMMENT '配置键',
    `config_value` VARCHAR(256) NOT NULL COMMENT '配置值',
    `remark` VARCHAR(128) COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_key` (`config_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='师徒系统配置';

-- 默认配置
INSERT INTO `xy_mentor_config` (`config_key`, `config_value`, `remark`) VALUES
('max_mentees', '3', '师傅最多收徒数'),
('mentee_max_level', '120', '徒弟最高等级(超过则出师)'),
('mentor_min_level', '150', '师傅最低等级'),
('exp_bonus_pct', '10', '徒弟经验加成(%)'),
('mentee_levelup_coins', '5', '徒弟每升1级给师傅的师徒币'),
('graduate_reward_coins', '500', '出师奖励师徒币'),
('coins_item_id', '4001126', '师徒币物品ID(复用匠人币)');
