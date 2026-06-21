-- NPC对话冷却限制开关
-- 开启(true)时走对话冷却限制逻辑，关闭(false)时不限制NPC对话频率
INSERT INTO game_config (config_type, config_sub_type, config_clazz, config_code, config_value, config_desc, update_time)
VALUES ('server', 'Game Mechanics', 'java.lang.Boolean', 'use_npc_race_condition', 'true', 'NPC对话冷却限制开关(true=启用冷却限制 false=不限制对话频率)', NOW())
ON DUPLICATE KEY UPDATE config_desc = VALUES(config_desc);
