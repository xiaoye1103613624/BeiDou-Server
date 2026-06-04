-- 勋章强化配置主表（全局配置，对所有勋章生效）
CREATE TABLE IF NOT EXISTS `xy_medal_enhance_config`
(
    `id`          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
    `max_enhance` INT          NOT NULL DEFAULT 10 COMMENT '最大强化等级',
    `enabled`     TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用（0=禁用 1=启用）',
    `create_time` TIMESTAMP             DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` TIMESTAMP             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间'
) COMMENT '勋章强化配置表（全局）' ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 勋章强化等级配置
CREATE TABLE IF NOT EXISTS `xy_medal_enhance_level`
(
    `id`              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
    `config_id`       BIGINT NOT NULL COMMENT '关联 medal_enhance_config.id',
    `enhance_level`   INT    NOT NULL COMMENT '强化等级（1~N）',
    `success_rate`    INT    NOT NULL DEFAULT 100 COMMENT '成功率（0~100）',
    `destroy_on_fail` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '失败是否销毁勋章',
    `meso_cost`       INT    NOT NULL DEFAULT 0 COMMENT '金币消耗',
    `str_add`         INT    NOT NULL DEFAULT 0 COMMENT '力量加成',
    `dex_add`         INT    NOT NULL DEFAULT 0 COMMENT '敏捷加成',
    `int_add`         INT    NOT NULL DEFAULT 0 COMMENT '智力加成',
    `luk_add`         INT    NOT NULL DEFAULT 0 COMMENT '运气加成',
    `hp_add`          INT    NOT NULL DEFAULT 0 COMMENT 'HP加成',
    `mp_add`          INT    NOT NULL DEFAULT 0 COMMENT 'MP加成',
    `watk_add`        INT    NOT NULL DEFAULT 0 COMMENT '物理攻击加成',
    `matk_add`        INT    NOT NULL DEFAULT 0 COMMENT '魔法攻击加成',
    `wdef_add`        INT    NOT NULL DEFAULT 0 COMMENT '物理防御加成',
    `mdef_add`        INT    NOT NULL DEFAULT 0 COMMENT '魔法防御加成',
    `acc_add`         INT    NOT NULL DEFAULT 0 COMMENT '命中加成',
    `avoid_add`       INT    NOT NULL DEFAULT 0 COMMENT '回避加成',
    `speed_add`       INT    NOT NULL DEFAULT 0 COMMENT '速度加成',
    `jump_add`        INT    NOT NULL DEFAULT 0 COMMENT '跳跃加成',
    `create_time`     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     TIMESTAMP       DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    UNIQUE KEY `uk_config_level` (`config_id`, `enhance_level`),
    FOREIGN KEY (`config_id`) REFERENCES `xy_medal_enhance_config` (`id`) ON DELETE CASCADE
) COMMENT '勋章强化等级配置表' ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 勋章强化道具消耗（每级可配多个道具）
CREATE TABLE IF NOT EXISTS `xy_medal_enhance_cost`
(
    `id`          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
    `level_id`    BIGINT NOT NULL COMMENT '关联 medal_enhance_level.id',
    `item_id`     INT    NOT NULL COMMENT '消耗道具ID',
    `count`       INT    NOT NULL DEFAULT 1 COMMENT '消耗数量',
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    FOREIGN KEY (`level_id`) REFERENCES `xy_medal_enhance_level` (`id`) ON DELETE CASCADE
) COMMENT '勋章强化道具消耗表' ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 初始数据：1~5级示例配置
INSERT INTO `xy_medal_enhance_config` (`id`, `max_enhance`, `enabled`) VALUES (1, 5, 1);

INSERT INTO `xy_medal_enhance_level` (`config_id`, `enhance_level`, `success_rate`, `destroy_on_fail`, `meso_cost`,
                                   `str_add`, `dex_add`, `int_add`, `luk_add`, `hp_add`, `mp_add`,
                                   `watk_add`, `matk_add`, `wdef_add`, `mdef_add`, `acc_add`, `avoid_add`)
VALUES (1, 1, 100, 0, 100000, 1, 1, 1, 1, 50, 50, 1, 1, 10, 10, 1, 1),
       (1, 2, 90, 0, 200000, 2, 2, 2, 2, 100, 100, 2, 2, 20, 20, 2, 2),
       (1, 3, 80, 0, 500000, 3, 3, 3, 3, 150, 150, 3, 3, 30, 30, 3, 3),
       (1, 4, 70, 0, 1000000, 4, 4, 4, 4, 200, 200, 4, 4, 40, 40, 4, 4),
       (1, 5, 60, 1, 2000000, 5, 5, 5, 5, 300, 300, 5, 5, 50, 50, 5, 5);

INSERT INTO `xy_medal_enhance_cost` (`level_id`, `item_id`, `count`)
VALUES (1, 4000082, 1),
       (2, 4000082, 2),
       (3, 4000082, 3),
       (4, 4000082, 4),
       (5, 4000082, 5);
