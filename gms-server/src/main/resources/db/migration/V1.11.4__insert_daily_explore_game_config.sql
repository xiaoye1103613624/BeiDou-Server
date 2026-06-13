-- ================================================================
-- 每日探索系统配置
-- 用途：玩家每日可进行探索任务，随机获得奖励物品或金币
-- 参数：daily_explore_limit = 每日探索上限次数
--       默认10次，修改此值可调整每日可探索的最大次数
-- 关联表：xy_daily_explore_config（探索奖励配置表）
-- 参考模式：V1.8.36__insert_paohuan_game_config.sql（跑环配置模式）
-- ================================================================
INSERT INTO `game_config`(`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`, `update_time`)
SELECT 'server', 'Game Mechanics', 'java.lang.Integer', 'daily_explore_limit', '10', '每日探索每日上限次数', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `game_config` WHERE `config_code` = 'daily_explore_limit');

-- 中文语言资源
INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'zh-CN', 'game_config', 'daily_explore_limit', '每日探索上限次数', NULL
WHERE NOT EXISTS (SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'zh-CN' AND `lang_code` = 'daily_explore_limit');

-- 英文语言资源
INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'en-US', 'game_config', 'daily_explore_limit', 'Daily exploration limit', NULL
WHERE NOT EXISTS (SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'en-US' AND `lang_code` = 'daily_explore_limit');
