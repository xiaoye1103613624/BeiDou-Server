-- 宠物成长进阶：阶段配置 + 进度 + 开关

CREATE TABLE IF NOT EXISTS `xy_pet_growth_stage` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `chain_code` varchar(64) NOT NULL COMMENT '进阶链编码，同链多阶段共用',
  `stage` int NOT NULL DEFAULT '1' COMMENT '阶段序号，从1开始',
  `name` varchar(128) NOT NULL COMMENT '展示名',
  `pet_id` int NOT NULL COMMENT '当前形态宠物物品ID(5000xxx)',
  `next_pet_id` int DEFAULT NULL COMMENT '进阶目标宠物ID，空=终阶',
  `need_exp` int NOT NULL DEFAULT '100' COMMENT '本阶段进阶所需成长经验',
  `exp_per_feed` int NOT NULL DEFAULT '10' COMMENT '每次喂养增加成长经验',
  `feed_item_ids` varchar(255) DEFAULT NULL COMMENT '允许的喂养道具ID，逗号分隔；空=任意212宠物食品',
  `exp_rate` double NOT NULL DEFAULT '1' COMMENT '召唤时经验倍率（乘算）',
  `drop_rate` double NOT NULL DEFAULT '1' COMMENT '召唤时爆率倍率（乘算）',
  `meso_rate` double NOT NULL DEFAULT '1' COMMENT '召唤时金币倍率（乘算）',
  `sort_order` int NOT NULL DEFAULT '0',
  `enabled` tinyint NOT NULL DEFAULT '1' COMMENT '0禁用 1启用',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP,
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_pet_growth_pet_id` (`pet_id`),
  KEY `idx_pet_growth_chain` (`chain_code`,`stage`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物成长阶段配置';

CREATE TABLE IF NOT EXISTS `xy_pet_growth_progress` (
  `petid` bigint NOT NULL COMMENT 'pets.petid',
  `growth_exp` int NOT NULL DEFAULT '0',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`petid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物自定义成长经验进度';

INSERT INTO `game_config` (`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`)
SELECT 'server', 'Game Mechanics', 'java.lang.Boolean', 'use_pet_growth_system', 'true', '自定义宠物成长进阶：喂养攒经验进阶，召唤时叠加经验/掉落/金币倍率'
WHERE NOT EXISTS (SELECT 1 FROM `game_config` WHERE `config_code` = 'use_pet_growth_system');

INSERT INTO `game_config` (`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`)
SELECT 'server', 'Game Mechanics', 'java.lang.Integer', 'pet_growth_junior_essence_exp', '10', '初级宠物精华(4310337)每次喂养成长经验'
WHERE NOT EXISTS (SELECT 1 FROM `game_config` WHERE `config_code` = 'pet_growth_junior_essence_exp');

INSERT INTO `game_config` (`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`)
SELECT 'server', 'Game Mechanics', 'java.lang.Integer', 'pet_growth_senior_essence_exp', '50', '高级宠物精华(4310338)每次喂养成长经验'
WHERE NOT EXISTS (SELECT 1 FROM `game_config` WHERE `config_code` = 'pet_growth_senior_essence_exp');

INSERT INTO `xy_pet_growth_stage`
(`chain_code`, `stage`, `name`, `pet_id`, `next_pet_id`, `need_exp`, `exp_per_feed`, `feed_item_ids`, `exp_rate`, `drop_rate`, `meso_rate`, `sort_order`, `enabled`)
SELECT * FROM (
  SELECT 'hedgehog_pastel' AS chain_code, 1 AS stage, '粉嫩刺猬' AS name, 5000765 AS pet_id, 5000766 AS next_pet_id, 100 AS need_exp, 10 AS exp_per_feed, NULL AS feed_item_ids, 1.03 AS exp_rate, 1.02 AS drop_rate, 1.02 AS meso_rate, 10 AS sort_order, 1 AS enabled
  UNION ALL SELECT 'hedgehog_pastel', 2, '天蓝刺猬', 5000766, 5000767, 200, 10, NULL, 1.06, 1.04, 1.04, 11, 1
  UNION ALL SELECT 'hedgehog_pastel', 3, '蛋黄刺猬', 5000767, NULL, 0, 10, NULL, 1.1, 1.06, 1.06, 12, 1
  UNION ALL SELECT 'sisters_trio', 1, '珊珊', 5000772, 5000773, 100, 10, NULL, 1.03, 1.02, 1.02, 20, 1
  UNION ALL SELECT 'sisters_trio', 2, '纳利', 5000773, 5000774, 200, 10, NULL, 1.06, 1.04, 1.04, 21, 1
  UNION ALL SELECT 'sisters_trio', 3, '白露', 5000774, NULL, 0, 10, NULL, 1.1, 1.06, 1.06, 22, 1
  UNION ALL SELECT 'mystic_cloud', 1, '神秘', 5000918, 5000919, 120, 10, NULL, 1.04, 1.02, 1.02, 30, 1
  UNION ALL SELECT 'mystic_cloud', 2, '云云', 5000919, 5000920, 240, 10, NULL, 1.07, 1.04, 1.04, 31, 1
  UNION ALL SELECT 'mystic_cloud', 3, '闪闪', 5000920, NULL, 0, 10, NULL, 1.12, 1.07, 1.07, 32, 1
  UNION ALL SELECT 'peng_ping_feng', 1, '彭彭', 5000933, 5000934, 100, 10, NULL, 1.03, 1.02, 1.02, 40, 1
  UNION ALL SELECT 'peng_ping_feng', 2, '平平', 5000934, 5000935, 200, 10, NULL, 1.06, 1.04, 1.04, 41, 1
  UNION ALL SELECT 'peng_ping_feng', 3, '风风', 5000935, NULL, 0, 10, NULL, 1.1, 1.06, 1.06, 42, 1
  UNION ALL SELECT 'serena_cosmos', 1, '赛瑞纳', 5000963, 5000964, 150, 10, NULL, 1.04, 1.03, 1.02, 50, 1
  UNION ALL SELECT 'serena_cosmos', 2, '宇宙兔', 5000964, 5000965, 300, 10, NULL, 1.08, 1.05, 1.04, 51, 1
  UNION ALL SELECT 'serena_cosmos', 3, '紫晶', 5000965, NULL, 0, 10, NULL, 1.12, 1.08, 1.06, 52, 1
  UNION ALL SELECT 'bento_pets', 1, '玉子烧', 5002030, 5002031, 80, 10, NULL, 1.02, 1.02, 1.03, 60, 1
  UNION ALL SELECT 'bento_pets', 2, '章鱼香肠', 5002031, 5002032, 160, 10, NULL, 1.04, 1.04, 1.05, 61, 1
  UNION ALL SELECT 'bento_pets', 3, '饭团', 5002032, NULL, 0, 10, NULL, 1.06, 1.06, 1.08, 62, 1
  UNION ALL SELECT 'small_trio', 1, '小乌帕', 5002048, 5002049, 100, 10, NULL, 1.03, 1.02, 1.02, 70, 1
  UNION ALL SELECT 'small_trio', 2, '小镥', 5002049, 5002050, 200, 10, NULL, 1.06, 1.04, 1.04, 71, 1
  UNION ALL SELECT 'small_trio', 3, '小葱', 5002050, NULL, 0, 10, NULL, 1.1, 1.06, 1.06, 72, 1
  UNION ALL SELECT 'fruit_hedgehog', 1, '桃子刺猬', 5002057, 5002058, 100, 10, NULL, 1.03, 1.03, 1.02, 80, 1
  UNION ALL SELECT 'fruit_hedgehog', 2, '蓝莓刺猬', 5002058, 5002059, 200, 10, NULL, 1.06, 1.05, 1.04, 81, 1
  UNION ALL SELECT 'fruit_hedgehog', 3, '樱桃刺猬', 5002059, NULL, 0, 10, NULL, 1.1, 1.08, 1.06, 82, 1
  UNION ALL SELECT 'tiny_cats', 1, '娇小白猫', 5002110, 5002111, 120, 10, NULL, 1.03, 1.02, 1.02, 90, 1
  UNION ALL SELECT 'tiny_cats', 2, '娇小棕纹猫', 5002111, NULL, 0, 10, NULL, 1.08, 1.05, 1.05, 91, 1
) AS seed
WHERE NOT EXISTS (SELECT 1 FROM `xy_pet_growth_stage` LIMIT 1);
