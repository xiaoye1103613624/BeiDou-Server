-- 伤害体系扩展来源：强化规则 / 携带物 + 全局 cap / 服务端重算开关

CREATE TABLE IF NOT EXISTS `xy_equip_enhance_rule`
(
    `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `rule_name`   VARCHAR(128) NOT NULL DEFAULT '' COMMENT '规则名，如星力1~25',
    `equip_type`  VARCHAR(32)  NOT NULL DEFAULT 'ALL' COMMENT 'WEAPON/ARMOR/ACCESSORY/ALL',
    `min_level`   INT          NOT NULL DEFAULT 0 COMMENT '强化等级下限（含）',
    `max_level`   INT          NOT NULL DEFAULT 99 COMMENT '强化等级上限（含）',
    `stats_json`  LONGTEXT     NULL COMMENT 'perLevel/milestones/combatStats JSON',
    `enabled`     INT          NOT NULL DEFAULT 1 COMMENT '1启用 0停用',
    `sort_order`  INT          NOT NULL DEFAULT 0 COMMENT '列表排序',
    `remark`      VARCHAR(255) NULL COMMENT '备注',
    `create_time` DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_xy_enhance_enabled_sort` (`enabled`, `sort_order`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='装备强化战斗属性规则';

CREATE TABLE IF NOT EXISTS `xy_carry_item_stat`
(
    `id`               BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `item_id`          INT          NOT NULL COMMENT '物品ID',
    `item_name`        VARCHAR(128) NULL COMMENT '展示名（可空，Web 可填）',
    `stats_json`       LONGTEXT     NULL COMMENT 'combatStats JSON',
    `require_equipped` INT          NOT NULL DEFAULT 0 COMMENT '1必须穿戴 0背包即生效',
    `enabled`          INT          NOT NULL DEFAULT 1 COMMENT '1启用 0停用',
    `remark`           VARCHAR(255) NULL COMMENT '备注',
    `create_time`      DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      DATETIME     DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_xy_carry_item_id` (`item_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='携带物战斗属性（背包或指定栏生效）';

INSERT INTO `game_config`(`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`,
                          `config_desc`, `update_time`)
SELECT 'server', 'Game Mechanics', 'java.lang.Boolean', 'use_server_damage_calc', 'false',
       '是否用服务端完整防御减免重算伤害（客户端上报伤害已含v83防御，默认关闭以免双重减伤）', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `game_config` WHERE `config_code` = 'use_server_damage_calc');

INSERT INTO `game_config`(`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`,
                          `config_desc`, `update_time`)
SELECT 'server', 'Game Mechanics', 'java.lang.Integer', 'combat_ignore_pdr_cap', '100',
       '无视物理防御率上限%', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `game_config` WHERE `config_code` = 'combat_ignore_pdr_cap');

INSERT INTO `game_config`(`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`,
                          `config_desc`, `update_time`)
SELECT 'server', 'Game Mechanics', 'java.lang.Integer', 'combat_ignore_mdr_cap', '100',
       '无视魔法防御率上限%', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `game_config` WHERE `config_code` = 'combat_ignore_mdr_cap');

INSERT INTO `game_config`(`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`,
                          `config_desc`, `update_time`)
SELECT 'server', 'Game Mechanics', 'java.lang.Integer', 'combat_crit_rate_cap', '100',
       '战斗属性暴击率上限%', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `game_config` WHERE `config_code` = 'combat_crit_rate_cap');

INSERT INTO `game_config`(`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`,
                          `config_desc`, `update_time`)
SELECT 'server', 'Game Mechanics', 'java.lang.Integer', 'combat_pdr_convert_k', '1000',
       '无PDRate时用PDDamage换算PDR的K：rate=pd/(pd+K)*100，0则用1000', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `game_config` WHERE `config_code` = 'combat_pdr_convert_k');

INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'zh-CN', 'game_config', 'use_server_damage_calc', '服务端完整防御重算（实验，默认关）', NULL
WHERE NOT EXISTS (SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'zh-CN' AND `lang_code` = 'use_server_damage_calc');

INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'en-US', 'game_config', 'use_server_damage_calc', 'Server-side full PDR recalc (experimental, default off)', NULL
WHERE NOT EXISTS (SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'en-US' AND `lang_code` = 'use_server_damage_calc');

INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'zh-CN', 'game_config', 'combat_ignore_pdr_cap', '无视物理防御率上限%', NULL
WHERE NOT EXISTS (SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'zh-CN' AND `lang_code` = 'combat_ignore_pdr_cap');

INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'en-US', 'game_config', 'combat_ignore_pdr_cap', 'Ignore PDR cap %', NULL
WHERE NOT EXISTS (SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'en-US' AND `lang_code` = 'combat_ignore_pdr_cap');

INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'zh-CN', 'game_config', 'combat_ignore_mdr_cap', '无视魔法防御率上限%', NULL
WHERE NOT EXISTS (SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'zh-CN' AND `lang_code` = 'combat_ignore_mdr_cap');

INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'en-US', 'game_config', 'combat_ignore_mdr_cap', 'Ignore MDR cap %', NULL
WHERE NOT EXISTS (SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'en-US' AND `lang_code` = 'combat_ignore_mdr_cap');

INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'zh-CN', 'game_config', 'combat_crit_rate_cap', '战斗属性暴击率上限%', NULL
WHERE NOT EXISTS (SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'zh-CN' AND `lang_code` = 'combat_crit_rate_cap');

INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'en-US', 'game_config', 'combat_crit_rate_cap', 'Combat crit rate cap %', NULL
WHERE NOT EXISTS (SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'en-US' AND `lang_code` = 'combat_crit_rate_cap');

INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'zh-CN', 'game_config', 'combat_pdr_convert_k', '无PDRate时PDDamage换算PDR的K值', NULL
WHERE NOT EXISTS (SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'zh-CN' AND `lang_code` = 'combat_pdr_convert_k');

INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'en-US', 'game_config', 'combat_pdr_convert_k', 'K for PDDamage→PDR when PDRate missing', NULL
WHERE NOT EXISTS (SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'en-US' AND `lang_code` = 'combat_pdr_convert_k');
