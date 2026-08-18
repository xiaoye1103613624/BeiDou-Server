-- 远征死亡次数（共享计数，0=关闭）
INSERT INTO `game_config`(`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`, `update_time`)
SELECT 'server', 'Game Mechanics', 'java.lang.Integer', 'zakum_expedition_death_count', '10', '扎昆远征死亡次数(0=关闭)', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `game_config` WHERE `config_code` = 'zakum_expedition_death_count');

INSERT INTO `game_config`(`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`, `update_time`)
SELECT 'server', 'Game Mechanics', 'java.lang.Integer', 'chaos_zakum_expedition_death_count', '5', '进阶扎昆远征死亡次数(0=关闭)', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `game_config` WHERE `config_code` = 'chaos_zakum_expedition_death_count');

INSERT INTO `game_config`(`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`, `update_time`)
SELECT 'server', 'Game Mechanics', 'java.lang.Integer', 'horntail_expedition_death_count', '0', '暗黑龙王远征死亡次数(0=关闭)', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `game_config` WHERE `config_code` = 'horntail_expedition_death_count');

INSERT INTO `game_config`(`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`, `update_time`)
SELECT 'server', 'Game Mechanics', 'java.lang.Integer', 'chaos_horntail_expedition_death_count', '0', '进阶暗黑龙王远征死亡次数(0=关闭)', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `game_config` WHERE `config_code` = 'chaos_horntail_expedition_death_count');

INSERT INTO `game_config`(`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`, `update_time`)
SELECT 'server', 'Game Mechanics', 'java.lang.Integer', 'pinkbean_expedition_death_count', '0', '品克缤远征死亡次数(0=关闭)', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `game_config` WHERE `config_code` = 'pinkbean_expedition_death_count');

INSERT INTO `game_config`(`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`, `update_time`)
SELECT 'server', 'Game Mechanics', 'java.lang.Integer', 'balrog_easy_expedition_death_count', '0', '简单巴洛古远征死亡次数(0=关闭)', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `game_config` WHERE `config_code` = 'balrog_easy_expedition_death_count');

INSERT INTO `game_config`(`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`, `update_time`)
SELECT 'server', 'Game Mechanics', 'java.lang.Integer', 'balrog_normal_expedition_death_count', '0', '普通巴洛古远征死亡次数(0=关闭)', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `game_config` WHERE `config_code` = 'balrog_normal_expedition_death_count');

INSERT INTO `game_config`(`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`, `update_time`)
SELECT 'server', 'Game Mechanics', 'java.lang.Integer', 'scarga_expedition_death_count', '0', '斯乌/血腥女王远征死亡次数(0=关闭)', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `game_config` WHERE `config_code` = 'scarga_expedition_death_count');

INSERT INTO `game_config`(`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`, `update_time`)
SELECT 'server', 'Game Mechanics', 'java.lang.Integer', 'showa_expedition_death_count', '0', '昭和城远征死亡次数(0=关闭)', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `game_config` WHERE `config_code` = 'showa_expedition_death_count');

INSERT INTO `game_config`(`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`, `update_time`)
SELECT 'server', 'Game Mechanics', 'java.lang.Integer', 'cwkpq_expedition_death_count', '0', 'CWKPQ远征死亡次数(0=关闭)', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `game_config` WHERE `config_code` = 'cwkpq_expedition_death_count');

INSERT INTO command_info (syntax, level, enabled, clazz, default_level)
SELECT 'deathcount', 3, 1, 'DeathCountCommand', 3
WHERE NOT EXISTS (SELECT 1 FROM command_info WHERE clazz = 'DeathCountCommand' AND syntax = 'deathcount');
