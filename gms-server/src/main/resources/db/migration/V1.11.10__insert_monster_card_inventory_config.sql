-- 怪物卡片拾取后存入背包（默认false=自动使用）。true=拾取后存入消耗栏，可手动双击使用
INSERT INTO `game_config`(`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`, `update_time`)
SELECT 'server', 'Game Mechanics', 'java.lang.Boolean', 'monster_card_to_inventory', 'false', '怪物卡片拾取后是否存入背包', NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM `game_config` WHERE `config_code` = 'monster_card_to_inventory'
);

-- 中文
INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'zh-CN', 'game_config', 'monster_card_to_inventory', '怪物卡片拾取后是否存入背包(true=存入背包 false=自动使用)', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'zh-CN' AND `lang_code` = 'monster_card_to_inventory'
);

-- 英文
INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'en-US', 'game_config', 'monster_card_to_inventory', 'Monster card goes to inventory instead of auto-use', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'en-US' AND `lang_code` = 'monster_card_to_inventory'
);
