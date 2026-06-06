-- 装备进阶路线配置主表（按职业群划分）
CREATE TABLE IF NOT EXISTS `xy_equip_advance_route`
(
    `id`          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
    `job_group`   VARCHAR(32)  NOT NULL COMMENT '职业群（warrior/archer/mage/thief/pirate）',
    `route_name`  VARCHAR(128) NOT NULL COMMENT '路线名称',
    `enabled`     TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用',
    `create_time` TIMESTAMP             DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` TIMESTAMP             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    UNIQUE KEY `uk_job_group` (`job_group`)
) COMMENT '装备进阶路线配置表' ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 装备进阶阶段配置（每个路线包含多个阶段，0=初始装备，1+=进阶阶段）
CREATE TABLE IF NOT EXISTS `xy_equip_advance_stage`
(
    `id`               BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
    `route_id`         BIGINT       NOT NULL COMMENT '关联 xy_equip_advance_route.id',
    `stage_order`      INT          NOT NULL COMMENT '阶段顺序（0=初始装备，1=一阶，2=二阶...）',
    `target_item_id`   INT          NOT NULL COMMENT '该阶段目标装备ID',
    `target_item_name` VARCHAR(128) NOT NULL COMMENT '目标装备名称',
    `meso_cost`        INT          NOT NULL DEFAULT 0 COMMENT '金币消耗',
    `cash_cost`        INT          NOT NULL DEFAULT 0 COMMENT '点卷消耗',
    `credit_cost`      INT          NOT NULL DEFAULT 0 COMMENT '抵用券消耗',
    `str_add`          INT          NOT NULL DEFAULT 0 COMMENT '力量加成',
    `dex_add`          INT          NOT NULL DEFAULT 0 COMMENT '敏捷加成',
    `int_add`          INT          NOT NULL DEFAULT 0 COMMENT '智力加成',
    `luk_add`          INT          NOT NULL DEFAULT 0 COMMENT '运气加成',
    `hp_add`           INT          NOT NULL DEFAULT 0 COMMENT 'HP加成',
    `mp_add`           INT          NOT NULL DEFAULT 0 COMMENT 'MP加成',
    `watk_add`         INT          NOT NULL DEFAULT 0 COMMENT '物理攻击加成',
    `matk_add`         INT          NOT NULL DEFAULT 0 COMMENT '魔法攻击加成',
    `wdef_add`         INT          NOT NULL DEFAULT 0 COMMENT '物理防御加成',
    `mdef_add`         INT          NOT NULL DEFAULT 0 COMMENT '魔法防御加成',
    `acc_add`          INT          NOT NULL DEFAULT 0 COMMENT '命中加成',
    `avoid_add`        INT          NOT NULL DEFAULT 0 COMMENT '回避加成',
    `speed_add`        INT          NOT NULL DEFAULT 0 COMMENT '速度加成',
    `jump_add`         INT          NOT NULL DEFAULT 0 COMMENT '跳跃加成',
    `create_time`      TIMESTAMP             DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      TIMESTAMP             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    UNIQUE KEY `uk_route_stage` (`route_id`, `stage_order`),
    FOREIGN KEY (`route_id`) REFERENCES `xy_equip_advance_route` (`id`) ON DELETE CASCADE
) COMMENT '装备进阶阶段配置表' ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 装备进阶材料消耗（每个阶段可配置多种材料）
CREATE TABLE IF NOT EXISTS `xy_equip_advance_cost`
(
    `id`          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
    `stage_id`    BIGINT NOT NULL COMMENT '关联 xy_equip_advance_stage.id',
    `item_id`     INT    NOT NULL COMMENT '消耗道具ID',
    `count`       INT    NOT NULL DEFAULT 1 COMMENT '消耗数量',
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    FOREIGN KEY (`stage_id`) REFERENCES `xy_equip_advance_stage` (`id`) ON DELETE CASCADE
) COMMENT '装备进阶材料消耗表' ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;