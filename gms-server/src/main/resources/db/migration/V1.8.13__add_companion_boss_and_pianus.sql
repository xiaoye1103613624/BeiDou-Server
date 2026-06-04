ALTER TABLE `xy_elite_boss_config`
    ADD COLUMN `companion_boss_id` INT DEFAULT NULL COMMENT '伴生BOSS怪物ID（同时召唤）' AFTER `boss_name`;

-- 鱼王(右) 8510000 和 鱼王(左) 8520000，互相设为伴生
INSERT INTO `xy_elite_boss_config` (`map_id`, `boss_id`, `boss_name`, `companion_boss_id`, `boss_time`, `script_name`, `enabled`)
VALUES (230040420, 8510000, '鱼王(右)', 8520000, 180, '', 1),
       (230040420, 8520000, '鱼王(左)', 8510000, 180, '', 1);
