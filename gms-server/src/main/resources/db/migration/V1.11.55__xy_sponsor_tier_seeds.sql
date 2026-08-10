-- 赞助档位骨架（截图累充路线）。仅插入缺失 amount，不覆盖已有配置；奖励物品请在 Web 后台「赞助档位」中配置。
-- 截图档位金额：188/588/888/1888/2888/3888/4888/5888/6888/7888/8888/9888/12888/15888/18888
-- 装备名线索（itemId 待后台核对）：龙王的项链、经典的冒险帽子、卓越戒指、白狼图腾、圣魂腰带、万众瞩目戒指、真理勋章等

INSERT INTO `xy_sponsor_config` (`name`, `amount`, `enabled`, `sort_order`, `create_time`, `update_time`)
SELECT '赞助满188', 188, 1, 188, NOW(), NOW() FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `xy_sponsor_config` WHERE `amount` = 188);

INSERT INTO `xy_sponsor_config` (`name`, `amount`, `enabled`, `sort_order`, `create_time`, `update_time`)
SELECT '赞助满588', 588, 1, 588, NOW(), NOW() FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `xy_sponsor_config` WHERE `amount` = 588);

INSERT INTO `xy_sponsor_config` (`name`, `amount`, `enabled`, `sort_order`, `create_time`, `update_time`)
SELECT '赞助满888', 888, 1, 888, NOW(), NOW() FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `xy_sponsor_config` WHERE `amount` = 888);

INSERT INTO `xy_sponsor_config` (`name`, `amount`, `enabled`, `sort_order`, `create_time`, `update_time`)
SELECT '赞助满1888', 1888, 1, 1888, NOW(), NOW() FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `xy_sponsor_config` WHERE `amount` = 1888);

INSERT INTO `xy_sponsor_config` (`name`, `amount`, `enabled`, `sort_order`, `create_time`, `update_time`)
SELECT '赞助满2888', 2888, 1, 2888, NOW(), NOW() FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `xy_sponsor_config` WHERE `amount` = 2888);

INSERT INTO `xy_sponsor_config` (`name`, `amount`, `enabled`, `sort_order`, `create_time`, `update_time`)
SELECT '赞助满3888', 3888, 1, 3888, NOW(), NOW() FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `xy_sponsor_config` WHERE `amount` = 3888);

INSERT INTO `xy_sponsor_config` (`name`, `amount`, `enabled`, `sort_order`, `create_time`, `update_time`)
SELECT '赞助满4888', 4888, 1, 4888, NOW(), NOW() FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `xy_sponsor_config` WHERE `amount` = 4888);

INSERT INTO `xy_sponsor_config` (`name`, `amount`, `enabled`, `sort_order`, `create_time`, `update_time`)
SELECT '赞助满5888', 5888, 1, 5888, NOW(), NOW() FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `xy_sponsor_config` WHERE `amount` = 5888);

INSERT INTO `xy_sponsor_config` (`name`, `amount`, `enabled`, `sort_order`, `create_time`, `update_time`)
SELECT '赞助满6888', 6888, 1, 6888, NOW(), NOW() FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `xy_sponsor_config` WHERE `amount` = 6888);

INSERT INTO `xy_sponsor_config` (`name`, `amount`, `enabled`, `sort_order`, `create_time`, `update_time`)
SELECT '赞助满7888', 7888, 1, 7888, NOW(), NOW() FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `xy_sponsor_config` WHERE `amount` = 7888);

INSERT INTO `xy_sponsor_config` (`name`, `amount`, `enabled`, `sort_order`, `create_time`, `update_time`)
SELECT '赞助满8888', 8888, 1, 8888, NOW(), NOW() FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `xy_sponsor_config` WHERE `amount` = 8888);

INSERT INTO `xy_sponsor_config` (`name`, `amount`, `enabled`, `sort_order`, `create_time`, `update_time`)
SELECT '赞助满9888', 9888, 1, 9888, NOW(), NOW() FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `xy_sponsor_config` WHERE `amount` = 9888);

INSERT INTO `xy_sponsor_config` (`name`, `amount`, `enabled`, `sort_order`, `create_time`, `update_time`)
SELECT '赞助满12888', 12888, 1, 12888, NOW(), NOW() FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `xy_sponsor_config` WHERE `amount` = 12888);

INSERT INTO `xy_sponsor_config` (`name`, `amount`, `enabled`, `sort_order`, `create_time`, `update_time`)
SELECT '赞助满15888', 15888, 1, 15888, NOW(), NOW() FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `xy_sponsor_config` WHERE `amount` = 15888);

INSERT INTO `xy_sponsor_config` (`name`, `amount`, `enabled`, `sort_order`, `create_time`, `update_time`)
SELECT '赞助满18888', 18888, 1, 18888, NOW(), NOW() FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM `xy_sponsor_config` WHERE `amount` = 18888);
