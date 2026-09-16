-- 玩法闭环：世界Boss伤害统计 / 挑战drop / 跳跳排期 / 签到奖励 / 装备强化

-- 1) 开启伤害排名（世界Boss / 远征等）
UPDATE `game_config`
SET `config_value` = 'true', `update_time` = NOW()
WHERE `config_code` = 'damage_ranking';

-- 2) 挑战副本 Boss 掉落（套服材料 + 匠人街常用材料）
INSERT INTO `drop_data` (`dropperid`, `itemid`, `minimum_quantity`, `maximum_quantity`, `questid`, `chance`)
VALUES
    (3501008, 4002000, 1, 2, 0, 500000),
    (3501008, 4002001, 1, 2, 0, 500000),
    (3502008, 4002002, 1, 2, 0, 500000),
    (3502008, 4002003, 1, 2, 0, 500000),
    (8240046, 4032171, 1, 1, 0, 300000),
    (8240046, 4021009, 1, 1, 0, 200000),
    (7220003, 4002000, 2, 3, 0, 400000),
    (7220003, 4002001, 2, 3, 0, 400000),
    (9600086, 4021017, 1, 1, 0, 250000),
    (9600086, 4032171, 1, 2, 0, 350000),
    (8240098, 4002002, 2, 4, 0, 450000),
    (8240098, 4002003, 2, 4, 0, 450000),
    (8240105, 4021017, 1, 2, 0, 300000),
    (8240105, 4000313, 1, 3, 0, 200000),
    (8870000, 4021017, 1, 2, 0, 350000),
    (8870000, 4032171, 2, 3, 0, 400000),
    (8880000, 4002000, 3, 5, 0, 500000),
    (8880000, 4002001, 3, 5, 0, 500000),
    (8880002, 4021017, 2, 3, 0, 450000),
    (8880830, 4002002, 2, 4, 0, 450000),
    (8880831, 4002003, 2, 4, 0, 450000),
    (8880832, 4021017, 2, 3, 0, 500000),
    (8910000, 4002000, 2, 3, 0, 400000),
    (8900000, 4002001, 2, 3, 0, 400000),
    (8920000, 4002002, 2, 3, 0, 400000),
    (8930000, 4002003, 2, 3, 0, 400000),
    (8850011, 4021017, 2, 4, 0, 500000),
    (8800102, 4000313, 3, 6, 0, 600000),
    (8800102, 4021017, 1, 3, 0, 500000)
ON DUPLICATE KEY UPDATE
    `minimum_quantity` = VALUES(`minimum_quantity`),
    `maximum_quantity` = VALUES(`maximum_quantity`),
    `questid` = VALUES(`questid`),
    `chance` = VALUES(`chance`);

-- 3) 忍苦跳跳每日排期（频道1，20:00，提前15分钟公告，5分钟预传送）
INSERT INTO `activity_schedule`
(`activity_code`, `world_id`, `channel_id`, `schedule_type`, `start_at`, `cron_time`, `days_of_week`,
 `max_players`, `notify_minutes`, `notify_interval_sec`, `prewarp_minutes`, `enabled`, `next_run_at`)
SELECT 'jump_patience', 0, 1, 'DAILY', NULL, '20:00:00', NULL,
       50, 15, 60, 5, 1,
       TIMESTAMP(DATE_ADD(CURDATE(), INTERVAL IF(TIME(NOW()) >= '20:00:00', 1, 0) DAY), '20:00:00')
WHERE NOT EXISTS (
    SELECT 1 FROM `activity_schedule` WHERE `activity_code` = 'jump_patience' AND `channel_id` = 1
);

-- 4) 每日签到奖励（真实道具，Web 可再热重载覆盖）
INSERT INTO `daily_checkin_reward`
(`day`, `icon_item_id`, `mesos`, `item_id`, `item_qty`, `expire_days`, `item2_id`, `item2_qty`, `item2_expire`, `slot_type`, `slot_count`, `remark`)
VALUES
    (1, 2000000, 50000, 4002000, 5, 0, 0, 0, 0, 0, 0, '套服材料'),
    (2, 2000000, 80000, 4002001, 5, 0, 0, 0, 0, 0, 0, '套服材料'),
    (3, 2000000, 100000, 4032171, 3, 0, 0, 0, 0, 0, 0, '洗炼石'),
    (4, 2000000, 120000, 4021009, 2, 0, 0, 0, 0, 0, 0, '星石'),
    (5, 2000000, 150000, 4002002, 5, 0, 0, 0, 0, 0, 0, '套服材料'),
    (6, 2000000, 180000, 2049300, 1, 0, 0, 0, 0, 0, 0, 'T5装备强化卷'),
    (7, 2000000, 200000, 4021017, 1, 0, 0, 0, 0, 0, 0, '灵韵结晶')
ON DUPLICATE KEY UPDATE
    `icon_item_id` = VALUES(`icon_item_id`),
    `mesos` = VALUES(`mesos`),
    `item_id` = VALUES(`item_id`),
    `item_qty` = VALUES(`item_qty`),
    `remark` = VALUES(`remark`);

-- 5) 装备强化（DB 驱动，供 EquipEnhanceManager 读取）
CREATE TABLE IF NOT EXISTS `xy_equip_enhance_config` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `item_id` int NOT NULL COMMENT '可强化装备模板ID',
    `item_name` varchar(64) NOT NULL,
    `max_enhance` int NOT NULL DEFAULT 5,
    `unique_per_char` tinyint NOT NULL DEFAULT 0,
    `enabled` tinyint NOT NULL DEFAULT 1,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_item_id` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='装备强化配置';

CREATE TABLE IF NOT EXISTS `xy_equip_enhance_level` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `config_id` bigint NOT NULL,
    `star_level` int NOT NULL COMMENT '目标星级 1..N',
    `success_rate` int NOT NULL DEFAULT 50 COMMENT '成功率%',
    `destroy_on_fail` tinyint NOT NULL DEFAULT 0,
    `meso_cost` bigint NOT NULL DEFAULT 0,
    `str_add` int NOT NULL DEFAULT 0,
    `dex_add` int NOT NULL DEFAULT 0,
    `int_add` int NOT NULL DEFAULT 0,
    `luk_add` int NOT NULL DEFAULT 0,
    `hp_add` int NOT NULL DEFAULT 0,
    `mp_add` int NOT NULL DEFAULT 0,
    `watk_add` int NOT NULL DEFAULT 0,
    `matk_add` int NOT NULL DEFAULT 0,
    `wdef_add` int NOT NULL DEFAULT 0,
    `mdef_add` int NOT NULL DEFAULT 0,
    `acc_add` int NOT NULL DEFAULT 0,
    `avoid_add` int NOT NULL DEFAULT 0,
    `speed_add` int NOT NULL DEFAULT 0,
    `jump_add` int NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_cfg_star` (`config_id`, `star_level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='装备强化星级';

CREATE TABLE IF NOT EXISTS `xy_equip_enhance_cost` (
    `id` bigint NOT NULL AUTO_INCREMENT,
    `level_id` bigint NOT NULL,
    `item_id` int NOT NULL,
    `count` int NOT NULL DEFAULT 1,
    PRIMARY KEY (`id`),
    KEY `idx_level_id` (`level_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='装备强化消耗';

INSERT IGNORE INTO `xy_equip_enhance_config` (`id`, `item_id`, `item_name`, `max_enhance`, `unique_per_char`, `enabled`)
VALUES (1, 1302000, '竹杖', 5, 0, 1);

INSERT IGNORE INTO `xy_equip_enhance_level`
(`config_id`, `star_level`, `success_rate`, `destroy_on_fail`, `meso_cost`,
 `str_add`, `dex_add`, `int_add`, `luk_add`, `hp_add`, `watk_add`, `matk_add`)
VALUES
    (1, 1, 90, 0, 100000, 2, 2, 2, 2, 50, 3, 3),
    (1, 2, 80, 0, 200000, 3, 3, 3, 3, 80, 5, 5),
    (1, 3, 70, 0, 400000, 4, 4, 4, 4, 100, 7, 7),
    (1, 4, 60, 0, 800000, 5, 5, 5, 5, 150, 10, 10),
    (1, 5, 50, 1, 1500000, 8, 8, 8, 8, 200, 15, 15);

INSERT IGNORE INTO `xy_equip_enhance_cost` (`level_id`, `item_id`, `count`)
SELECT l.id, 2049300, 1 FROM `xy_equip_enhance_level` l WHERE l.config_id = 1 AND l.star_level = 1;
INSERT IGNORE INTO `xy_equip_enhance_cost` (`level_id`, `item_id`, `count`)
SELECT l.id, 2049300, 1 FROM `xy_equip_enhance_level` l WHERE l.config_id = 1 AND l.star_level = 2;
INSERT IGNORE INTO `xy_equip_enhance_cost` (`level_id`, `item_id`, `count`)
SELECT l.id, 2049300, 2 FROM `xy_equip_enhance_level` l WHERE l.config_id = 1 AND l.star_level = 3;
INSERT IGNORE INTO `xy_equip_enhance_cost` (`level_id`, `item_id`, `count`)
SELECT l.id, 2049300, 2 FROM `xy_equip_enhance_level` l WHERE l.config_id = 1 AND l.star_level = 4;
INSERT IGNORE INTO `xy_equip_enhance_cost` (`level_id`, `item_id`, `count`)
SELECT l.id, 2049300, 3 FROM `xy_equip_enhance_level` l WHERE l.config_id = 1 AND l.star_level = 5;
