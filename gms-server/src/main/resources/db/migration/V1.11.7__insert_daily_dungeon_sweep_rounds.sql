-- ================================================================
-- 每日副本VIP扫荡配置
-- 用途：VIP玩家可一键快速完成每日副本，跳过手动打副本流程
-- 参数：daily_dungeon_sweep_rounds = 每日可扫荡轮数
--       0 = 禁用扫荡功能（所有玩家需手动打副本）
--       N = VIP玩家每日可扫荡 N 轮（跳过副本战斗直接领奖）
-- 关联表：xy_daily_dungeon_config（每日副本配置表）
-- ================================================================
INSERT INTO `game_config`(`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`, `update_time`)
SELECT 'server', 'Game Mechanics', 'java.lang.Integer', 'daily_dungeon_sweep_rounds', '0', '每日副本VIP扫荡轮数（0=禁用）', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `game_config` WHERE `config_code` = 'daily_dungeon_sweep_rounds');

-- 中文
INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'zh-CN', 'game_config', 'daily_dungeon_sweep_rounds', '每日副本VIP扫荡轮数（0=禁用）', NULL
WHERE NOT EXISTS (SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'zh-CN' AND `lang_code` = 'daily_dungeon_sweep_rounds');

-- 英文
INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'en-US', 'game_config', 'daily_dungeon_sweep_rounds', 'Daily dungeon VIP sweep rounds (0=disabled)', NULL
WHERE NOT EXISTS (SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'en-US' AND `lang_code` = 'daily_dungeon_sweep_rounds');
