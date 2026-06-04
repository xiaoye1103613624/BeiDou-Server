CREATE TABLE IF NOT EXISTS `xy_boss_config`
(
    `id`                BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
    `mob_id`            INT            NOT NULL COMMENT '怪物ID（对应Mob.wz中的怪物ID）',
    `boss_name`         VARCHAR(128) COMMENT 'BOSS名称（便于后台识别）',
    `hp_multiplier`     DECIMAL(5, 2)  NOT NULL DEFAULT 1.00 COMMENT 'HP倍率（1.00=原始血量）',
    `exp_multiplier`    DECIMAL(5, 2)  NOT NULL DEFAULT 1.00 COMMENT '经验倍率（1.00=原始经验）',
    `damage_multiplier` DECIMAL(5, 2)  NOT NULL DEFAULT 1.00 COMMENT '伤害倍率（1.00=原始伤害，影响PADamage和MADamage）',
    `enabled`           TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用（0=禁用 1=启用）',
    `create_time`       TIMESTAMP            DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`       TIMESTAMP            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    UNIQUE KEY `uk_mob_id` (`mob_id`)
) COMMENT 'BOSS属性倍率配置表' ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 预填充常见远征BOSS
INSERT INTO `xy_boss_config` (`mob_id`, `boss_name`, `hp_multiplier`, `exp_multiplier`, `damage_multiplier`, `enabled`)
VALUES (8800002, '扎昆', 1.00, 1.00, 1.00, 1),
       (8810018, '黑龙', 1.00, 1.00, 1.00, 1),
       (8820001, '品克缤', 1.00, 1.00, 1.00, 1),
       (9420544, '蝙蝠怪', 1.00, 1.00, 1.00, 1),
       (8500001, '蝙蝠王', 1.00, 1.00, 1.00, 1),
       (9400121, '女皇', 1.00, 1.00, 1.00, 1),
       (8800102, '进阶扎昆', 1.00, 1.00, 1.00, 1),
       (8810118, '进阶黑龙', 1.00, 1.00, 1.00, 1),
       (8820101, '进阶品克缤', 1.00, 1.00, 1.00, 1);
