-- 修正BOSS配置名称：8500001 = 闹钟(帕普拉图斯)，不是蝙蝠王
UPDATE `xy_boss_config` SET `boss_name` = '闹钟' WHERE `mob_id` = 8500001;
