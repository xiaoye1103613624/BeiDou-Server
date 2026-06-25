-- =====================================================
-- 套装伤害加成配置表
-- 支持按"套装ID + 穿戴件数档位"配置：普通伤害百分比加成、Boss伤害百分比加成
-- 与装备伤害加成(xy_equip_damage_bonus_config)同等结构，但作用对象是套装(SetItemInfo.img)而非单件装备
-- 角色穿戴件数达到tier_count时，该档位配置的加成生效；多档位可叠加(与WZ原生套装属性叠加规则一致)
-- =====================================================
CREATE TABLE IF NOT EXISTS `xy_set_damage_bonus_config`
(
    `id`              BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
    `set_item_id`     INT          NOT NULL COMMENT '套装ID(对应Etc.wz/SetItemInfo.img节点ID)',
    `set_name`        VARCHAR(128) NOT NULL COMMENT '套装名称',
    `tier_count`      INT          NOT NULL COMMENT '生效所需穿戴件数档位(达到该件数即生效，可与其他档位叠加)',
    `damage_pct`      INT          NOT NULL DEFAULT 0 COMMENT '普通伤害加成百分比（如10表示+10%）',
    `boss_damage_pct` INT          NOT NULL DEFAULT 0 COMMENT 'Boss伤害加成百分比（仅对Boss类怪物生效）',
    `enabled`         TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用（0=禁用 1=启用）',
    `create_time`     TIMESTAMP             DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`     TIMESTAMP             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    UNIQUE KEY `uk_set_tier` (`set_item_id`, `tier_count`)
) COMMENT '套装伤害加成配置表' ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
