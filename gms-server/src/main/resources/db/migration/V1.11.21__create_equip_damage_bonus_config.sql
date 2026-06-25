-- =====================================================
-- 装备伤害加成配置表
-- 支持按单件装备配置：普通伤害百分比加成、Boss伤害百分比加成、固定段伤
-- 角色换装/登录时汇总当前穿戴装备的加成并缓存到角色身上，攻击时直接读缓存
-- =====================================================
CREATE TABLE IF NOT EXISTS `xy_equip_damage_bonus_config`
(
    `id`              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
    `item_id`         INT          NOT NULL COMMENT '装备物品ID',
    `item_name`       VARCHAR(128) NOT NULL COMMENT '装备名称',
    `damage_pct`      INT          NOT NULL DEFAULT 0 COMMENT '普通伤害加成百分比（如10表示+10%）',
    `boss_damage_pct` INT          NOT NULL DEFAULT 0 COMMENT 'Boss伤害加成百分比（仅对Boss类怪物生效）',
    `fixed_damage`    BIGINT       NOT NULL DEFAULT 0 COMMENT '固定段伤（加法叠加的固定伤害值）',
    `enabled`         TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用（0=禁用 1=启用）',
    `create_time`     TIMESTAMP             DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     TIMESTAMP             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    UNIQUE KEY `uk_item_id` (`item_id`)
) COMMENT '装备伤害加成配置表' ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
