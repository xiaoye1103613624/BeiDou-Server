-- 10级前自由加点：关闭新手自动分配 AP
UPDATE `game_config`
SET `config_value` = 'false',
    `update_time` = NOW()
WHERE `config_code` = 'use_auto_assign_starters_ap';

-- 冒险家创建角色原生投骰子
INSERT INTO `game_config`(`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`, `update_time`)
SELECT 'server', 'Game Mechanics', 'java.lang.Boolean', 'enable_native_adventurer_dice', 'true', 'enable_native_adventurer_dice', NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM `game_config` WHERE `config_code` = 'enable_native_adventurer_dice'
);

-- 中文描述
INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'zh-CN', 'game_config', 'enable_native_adventurer_dice', '启用冒险家创建角色原生投骰子（末尾追加 STR/DEX/INT/LUK）', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'zh-CN' AND `lang_code` = 'enable_native_adventurer_dice'
);

INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'zh-CN', 'game_config', 'use_auto_assign_starters_ap', '10级以下新手是否自动分配属性点（关闭后可手动加点）', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'zh-CN' AND `lang_code` = 'use_auto_assign_starters_ap'
);

-- 英文描述
INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'en-US', 'game_config', 'enable_native_adventurer_dice', 'Enable native adventurer dice stats on create-char (append STR/DEX/INT/LUK)', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'en-US' AND `lang_code` = 'enable_native_adventurer_dice'
);
