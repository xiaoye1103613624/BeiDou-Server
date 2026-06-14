-- ================================================================
-- 从xy_daily_boss_config表移除扫荡相关列
-- 原因：每日Boss系统采用跑环制（每环随机Boss+击杀数），不支持扫荡功能
--       扫荡仅在每日副本(daily_dungeon)系统中使用
-- ================================================================
ALTER TABLE `xy_daily_boss_config` DROP COLUMN `sweep_item_id`;
ALTER TABLE `xy_daily_boss_config` DROP COLUMN `sweep_item_cost`;
ALTER TABLE `xy_daily_boss_config` DROP COLUMN `max_sweep`;
