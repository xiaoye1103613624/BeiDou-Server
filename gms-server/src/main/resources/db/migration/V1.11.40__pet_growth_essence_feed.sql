-- 宠物成长改为使用宠物精华喂养：4310337 初级 / 4310338 高级

UPDATE xy_pet_growth_stage
SET feed_item_ids = '4310337,4310338',
    update_time = NOW()
WHERE enabled = 1;

INSERT INTO `game_config`(`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`, `update_time`)
SELECT 'server', 'Game Mechanics', 'java.lang.Integer', 'pet_growth_junior_essence_exp', '10',
       '初级宠物精华(4310337)每次增加的成长经验', NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM `game_config` WHERE `config_code` = 'pet_growth_junior_essence_exp'
);

INSERT INTO `game_config`(`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`, `update_time`)
SELECT 'server', 'Game Mechanics', 'java.lang.Integer', 'pet_growth_senior_essence_exp', '50',
       '高级宠物精华(4310338)每次增加的成长经验', NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM `game_config` WHERE `config_code` = 'pet_growth_senior_essence_exp'
);

INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'zh-CN', 'game_config', 'pet_growth_junior_essence_exp', '初级宠物精华每次成长经验', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'zh-CN' AND `lang_code` = 'pet_growth_junior_essence_exp'
);

INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'zh-CN', 'game_config', 'pet_growth_senior_essence_exp', '高级宠物精华每次成长经验', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'zh-CN' AND `lang_code` = 'pet_growth_senior_essence_exp'
);

INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'en-US', 'game_config', 'pet_growth_junior_essence_exp', 'Junior pet essence growth EXP per use', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'en-US' AND `lang_code` = 'pet_growth_junior_essence_exp'
);

INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'en-US', 'game_config', 'pet_growth_senior_essence_exp', 'Senior pet essence growth EXP per use', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'en-US' AND `lang_code` = 'pet_growth_senior_essence_exp'
);
