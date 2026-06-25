-- =====================================================
-- 额外伤害配置表（全局单行配置）
-- 参考079破功版"额外伤害"机制：按角色全身装备属性总和×可配置比例系数计算出一笔额外伤害，
-- 在普通伤害结算之后叠加到怪物身上，并可选择以悬浮提示/系统喇叭的方式展示给玩家
-- =====================================================
CREATE TABLE IF NOT EXISTS `xy_extra_damage_config`
(
    `id`               BIGINT       NOT NULL COMMENT '主键，固定为1（全局单行配置）',
    `enabled`          TINYINT(1) NOT NULL DEFAULT 0 COMMENT '额外伤害总开关（0=禁用 1=启用）',
    `damage_threshold` BIGINT       NOT NULL DEFAULT 0 COMMENT '触发阈值：本次普通伤害达到该值才触发额外伤害（与道具触发为"或"关系）',
    `trigger_item_id`  INT          NOT NULL DEFAULT 0 COMMENT '道具触发开关对应的物品ID，玩家持有该物品即可触发（0=不启用道具触发）',
    `str_ratio`        INT          NOT NULL DEFAULT 0 COMMENT '装备总力量加成比例系数',
    `dex_ratio`        INT          NOT NULL DEFAULT 0 COMMENT '装备总敏捷加成比例系数',
    `int_ratio`        INT          NOT NULL DEFAULT 0 COMMENT '装备总智力加成比例系数',
    `luk_ratio`        INT          NOT NULL DEFAULT 0 COMMENT '装备总运气加成比例系数',
    `watk_ratio`       INT          NOT NULL DEFAULT 0 COMMENT '装备总物攻加成比例系数',
    `matk_ratio`       INT          NOT NULL DEFAULT 0 COMMENT '装备总魔攻加成比例系数',
    `wdef_ratio`       INT          NOT NULL DEFAULT 0 COMMENT '装备总物防加成比例系数',
    `mdef_ratio`       INT          NOT NULL DEFAULT 0 COMMENT '装备总魔防加成比例系数',
    `hp_ratio`         INT          NOT NULL DEFAULT 0 COMMENT '装备总血量加成比例系数',
    `mp_ratio`         INT          NOT NULL DEFAULT 0 COMMENT '装备总魔量加成比例系数',
    `show_hint`        TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否以头顶悬浮提示展示额外伤害（0=不展示 1=展示，仅本人可见）',
    `show_message`     TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否以系统喇叭(黄字)展示额外伤害（0=不展示 1=展示）',
    `create_time`      TIMESTAMP             DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      TIMESTAMP             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    PRIMARY KEY (`id`)
) COMMENT '额外伤害全局配置表' ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 初始化默认配置行（默认禁用，避免未配置时误生效）
INSERT INTO `xy_extra_damage_config` (`id`, `enabled`)
VALUES (1, 0);
