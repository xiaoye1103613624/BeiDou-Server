-- 阿尔泰皮肤 AvatarLook 全覆盖过滤开关（默认开启）
INSERT INTO `game_config`(`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`, `update_time`)
SELECT 'server', 'Game Mechanics', 'java.lang.Boolean', 'use_altair_skin_look_filter', 'true', 'use_altair_skin_look_filter', NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM `game_config` WHERE `config_code` = 'use_altair_skin_look_filter'
);

INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'zh-CN', 'game_config', 'use_altair_skin_look_filter', '穿戴阿尔泰皮肤时仅广播皮肤外观（隐藏其它装备/脸/发型）', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'zh-CN' AND `lang_code` = 'use_altair_skin_look_filter'
);

INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'en-US', 'game_config', 'use_altair_skin_look_filter', 'When wearing Altair skins, broadcast skin-only AvatarLook (hide other equips/face/hair)', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'en-US' AND `lang_code` = 'use_altair_skin_look_filter'
);
