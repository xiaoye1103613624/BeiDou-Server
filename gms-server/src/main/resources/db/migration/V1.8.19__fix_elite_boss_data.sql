-- 1. 修正boss_name：确保boss_id与名称一致
UPDATE `xy_elite_boss_config` SET `boss_name` = '巨型蝙蝠怪' WHERE `boss_id` = 8830000 AND `boss_name` != '巨型蝙蝠怪';
UPDATE `xy_elite_boss_config` SET `boss_name` = '暴力熊' WHERE `boss_id` = 9420549 AND `boss_name` != '暴力熊';

-- 2. 补充缺失的巨型蝙蝠怪(Balrog, mob=8830000, map=105100100)
INSERT IGNORE INTO `xy_elite_boss_config` (`map_id`, `boss_id`, `boss_name`, `boss_time`, `script_name`, `enabled`)
VALUES (105100100, 8830000, '巨型蝙蝠怪', 180, '', 1);

-- 3. 如果暴力熊也不存在则补充(Scarlion, mob=9420549, map=942054900)
INSERT IGNORE INTO `xy_elite_boss_config` (`map_id`, `boss_id`, `boss_name`, `boss_time`, `script_name`, `enabled`)
VALUES (942054900, 9420549, '暴力熊', 180, '', 1);
