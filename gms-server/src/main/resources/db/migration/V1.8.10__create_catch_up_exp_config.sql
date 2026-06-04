CREATE TABLE IF NOT EXISTS `xy_catch_up_exp_config`
(
    `id`             BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
    `level_min`      INT            NOT NULL COMMENT '等级下限（包含）',
    `level_max`      INT            NOT NULL COMMENT '等级上限（包含）',
    `exp_multiplier` DECIMAL(5, 2)  NOT NULL DEFAULT 1.00 COMMENT '经验倍率（1.00=原始经验）',
    `enabled`        TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用（0=禁用 1=启用）',
    `create_time`    TIMESTAMP            DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`    TIMESTAMP            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    UNIQUE KEY `uk_level_range` (`level_min`, `level_max`)
) COMMENT '追赶机制经验倍率配置表' ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 预填充示例数据
INSERT INTO `xy_catch_up_exp_config` (`level_min`, `level_max`, `exp_multiplier`, `enabled`)
VALUES (1, 30, 5.00, 1),
       (31, 70, 3.00, 1),
       (71, 120, 1.00, 1);
