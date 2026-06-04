-- 装备强化配置主表
CREATE TABLE IF NOT EXISTS `xy_equip_enhance_config`
(
    `id`              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
    `item_id`         INT          NOT NULL COMMENT '可强化的装备物品ID',
    `item_name`       VARCHAR(128) NOT NULL COMMENT '装备名称',
    `unique_per_char` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '每角色仅限强化一件（0=不限 1=限制）',
    `max_enhance`     INT          NOT NULL DEFAULT 10 COMMENT '最大强化等级',
    `enabled`         TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
    `create_time`     TIMESTAMP             DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     TIMESTAMP             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    UNIQUE KEY `uk_item_id` (`item_id`)
) COMMENT '装备强化配置表' ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 装备强化等级配置
CREATE TABLE IF NOT EXISTS `xy_equip_enhance_level`
(
    `id`              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
    `config_id`       BIGINT NOT NULL COMMENT '关联 equip_enhance_config.id',
    `enhance_level`   INT    NOT NULL COMMENT '强化等级（1~N）',
    `success_rate`    INT    NOT NULL DEFAULT 100 COMMENT '成功率（0~100）',
    `destroy_on_fail` TINYINT(1) NOT NULL DEFAULT 0 COMMENT '失败是否销毁装备',
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
    FOREIGN KEY (`config_id`) REFERENCES `xy_equip_enhance_config` (`id`) ON DELETE CASCADE
) COMMENT '装备强化等级配置表' ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 装备强化道具消耗（每级可配多个道具）
CREATE TABLE IF NOT EXISTS `xy_equip_enhance_cost`
(
    `id`          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
    `level_id`    BIGINT NOT NULL COMMENT '关联 equip_enhance_level.id',
    `item_id`     INT    NOT NULL COMMENT '消耗道具ID',
    `count`       INT    NOT NULL DEFAULT 1 COMMENT '消耗数量',
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    FOREIGN KEY (`level_id`) REFERENCES `xy_equip_enhance_level` (`id`) ON DELETE CASCADE
) COMMENT '装备强化道具消耗表' ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
