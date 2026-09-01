-- 匠人街：炼金/炼药/锻造/洗炼/挑战疲劳（结构与种子从 beidou_s8 同步）

-- 洗炼字段（S9 无 socket3，挂在 tintfxbright 后）
SET @db := DATABASE();
SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='inventoryequipment' AND COLUMN_NAME='reforge1');
SET @sql := IF(@exists=0, 'ALTER TABLE inventoryequipment ADD COLUMN reforge1 INT NOT NULL DEFAULT 0 COMMENT ''洗炼词条1'' AFTER tintfxbright', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='inventoryequipment' AND COLUMN_NAME='reforge2');
SET @sql := IF(@exists=0, 'ALTER TABLE inventoryequipment ADD COLUMN reforge2 INT NOT NULL DEFAULT 0 COMMENT ''洗炼词条2'' AFTER reforge1', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='inventoryequipment' AND COLUMN_NAME='reforge3');
SET @sql := IF(@exists=0, 'ALTER TABLE inventoryequipment ADD COLUMN reforge3 INT NOT NULL DEFAULT 0 COMMENT ''洗炼词条3'' AFTER reforge2', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='inventoryequipment' AND COLUMN_NAME='reforgeLock');
SET @sql := IF(@exists=0, 'ALTER TABLE inventoryequipment ADD COLUMN reforgeLock TINYINT NOT NULL DEFAULT 0 COMMENT ''洗炼锁定位掩码'' AFTER reforge3', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;


-- ===== xy_character_alchemy =====
CREATE TABLE IF NOT EXISTS `xy_character_alchemy` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `character_id` int NOT NULL COMMENT '角色ID（炼金师等级/经验按角色隔离，独立于炼药师经验池）',
  `exp` bigint NOT NULL DEFAULT '0' COMMENT '炼金师累计经验值，只增不减，升级不重置',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_character_id` (`character_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色级炼金师等级经验表';

-- ===== xy_character_alchemist =====
CREATE TABLE IF NOT EXISTS `xy_character_alchemist` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `character_id` int NOT NULL COMMENT '角色ID（炼药师等级/经验按角色隔离）',
  `exp` bigint NOT NULL DEFAULT '0' COMMENT '炼药师累计经验值，只增不减，升级不重置',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_character_id` (`character_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色级炼药师（药剂师副职业）等级经验表';

-- ===== xy_character_forge =====
CREATE TABLE IF NOT EXISTS `xy_character_forge` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `character_id` int NOT NULL COMMENT '角色ID（锻造师等级/经验按角色隔离，独立于炼药师/炼金师经验池）',
  `exp` bigint NOT NULL DEFAULT '0' COMMENT '锻造师累计经验值，只增不减，升级不重置',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_character_id` (`character_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色级锻造师等级经验表';

-- ===== xy_alchemy_tier =====
CREATE TABLE IF NOT EXISTS `xy_alchemy_tier` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `type` tinyint NOT NULL DEFAULT '1' COMMENT '副职业类型：1=炼金 2=炼药 3=锻造',
  `name` varchar(32) NOT NULL COMMENT '品级名称（如：入门、普通、职业、大师、宗师）',
  `exp_start` bigint NOT NULL DEFAULT '0' COMMENT '达到该品级所需的最低累计经验（经验阈值）',
  `is_max` tinyint NOT NULL DEFAULT '0' COMMENT '是否为最高品级：0=否 1=是（最高品级无上限）',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '品级显示顺序，越小品级越低',
  `enabled` tinyint NOT NULL DEFAULT '1' COMMENT '是否启用：0=禁用 1=启用',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_type_sort_order` (`type`,`sort_order`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='炼金师品级配置表（品级名称/经验阈值后台可配）';

-- ===== xy_alchemy_recipe =====
CREATE TABLE IF NOT EXISTS `xy_alchemy_recipe` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tier_required` tinyint NOT NULL DEFAULT '0' COMMENT '所需炼金师品级下标：0=入门 1=普通 2=职业 3=大师 4=宗师',
  `result_item_id` int NOT NULL DEFAULT '0' COMMENT '炼制产出物品ID（待定，占位）',
  `result_count` int NOT NULL DEFAULT '1' COMMENT '炼制产出物品数量',
  `exp_gain` int NOT NULL DEFAULT '0' COMMENT '炼制成功增加的炼金师经验',
  `stamina_cost` int NOT NULL DEFAULT '0' COMMENT '炼制消耗体力（账号通用体力池）',
  `meso_cost` bigint NOT NULL DEFAULT '0' COMMENT '炼制消耗金币',
  `cash_cost` int NOT NULL DEFAULT '0' COMMENT '炼制消耗点券(NX_CREDIT)',
  `material1_item_id` int DEFAULT NULL COMMENT '材料1物品ID（待定，占位，NULL表示不需要）',
  `material1_count` int NOT NULL DEFAULT '0' COMMENT '材料1所需数量',
  `material2_item_id` int DEFAULT NULL COMMENT '材料2物品ID（待定，占位，NULL表示不需要）',
  `material2_count` int NOT NULL DEFAULT '0' COMMENT '材料2所需数量',
  `material3_item_id` int DEFAULT NULL COMMENT '材料3物品ID（待定，占位，NULL表示不需要）',
  `material3_count` int NOT NULL DEFAULT '0' COMMENT '材料3所需数量',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '同品级内显示排序，越小越靠前',
  `enabled` tinyint NOT NULL DEFAULT '0' COMMENT '是否启用：0=禁用（待数据确定前默认禁用） 1=启用',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='炼金配方配置表（炼制物品/材料数据待定，默认禁用，正式数据确定后启用）';

-- ===== xy_alchemist_recipe =====
CREATE TABLE IF NOT EXISTS `xy_alchemist_recipe` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tier_required` tinyint NOT NULL DEFAULT '0' COMMENT '所需炼药师品级下标：0=入门 1=普通 2=职业 3=大师 4=宗师',
  `result_item_id` int NOT NULL DEFAULT '0' COMMENT '炼制产出物品ID',
  `result_count` int NOT NULL DEFAULT '1' COMMENT '炼制产出物品数量',
  `exp_gain` int NOT NULL DEFAULT '0' COMMENT '炼制成功增加的炼药师经验',
  `stamina_cost` int NOT NULL DEFAULT '0' COMMENT '炼制消耗体力（账号通用体力池）',
  `meso_cost` bigint NOT NULL DEFAULT '0' COMMENT '炼制消耗金币',
  `material1_item_id` int DEFAULT NULL COMMENT '材料1物品ID',
  `material1_count` int NOT NULL DEFAULT '0' COMMENT '材料1所需数量',
  `material2_item_id` int DEFAULT NULL COMMENT '材料2物品ID',
  `material2_count` int NOT NULL DEFAULT '0' COMMENT '材料2所需数量',
  `material3_item_id` int DEFAULT NULL COMMENT '材料3物品ID',
  `material3_count` int NOT NULL DEFAULT '0' COMMENT '材料3所需数量',
  `material4_item_id` int DEFAULT NULL COMMENT '材料4物品ID',
  `material4_count` int NOT NULL DEFAULT '0' COMMENT '材料4所需数量',
  `material5_item_id` int DEFAULT NULL COMMENT '材料5物品ID',
  `material5_count` int NOT NULL DEFAULT '0' COMMENT '材料5所需数量',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '同品级内显示排序',
  `enabled` tinyint NOT NULL DEFAULT '1' COMMENT '是否启用：0=禁用 1=启用',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=20 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='炼药师配方配置表（炼制物品/材料/消耗默认启用）';

-- ===== xy_forge_recipe =====
CREATE TABLE IF NOT EXISTS `xy_forge_recipe` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `name` varchar(64) NOT NULL DEFAULT '' COMMENT '配方名称(展示用，如：智慧戒指Ⅶ)',
  `tier_required` tinyint NOT NULL DEFAULT '0' COMMENT '所需锻造师品级下标：0=入门 1=普通 2=职业 3=大师 4=宗师',
  `result_item_id` int NOT NULL DEFAULT '0' COMMENT '打造产出装备ID（待定，占位）',
  `exp_gain` int NOT NULL DEFAULT '0' COMMENT '打造成功增加的锻造师经验',
  `meso_cost` bigint NOT NULL DEFAULT '0' COMMENT '打造消耗金币',
  `stamina_cost` int NOT NULL DEFAULT '0' COMMENT '打造消耗体力',
  `material1_item_id` int DEFAULT NULL COMMENT '材料1物品ID（待定，占位，NULL表示不需要）',
  `material1_count` int NOT NULL DEFAULT '0' COMMENT '材料1所需数量',
  `material2_item_id` int DEFAULT NULL COMMENT '材料2物品ID（待定，占位，NULL表示不需要）',
  `material2_count` int NOT NULL DEFAULT '0' COMMENT '材料2所需数量',
  `material3_item_id` int DEFAULT NULL COMMENT '材料3物品ID（待定，占位，NULL表示不需要）',
  `material3_count` int NOT NULL DEFAULT '0' COMMENT '材料3所需数量',
  `str_min` int NOT NULL DEFAULT '0' COMMENT '力量默认随机区间下限(0表示该配方不涉及此属性)',
  `str_max` int NOT NULL DEFAULT '0' COMMENT '力量默认随机区间上限',
  `dex_min` int NOT NULL DEFAULT '0' COMMENT '敏捷默认随机区间下限(0表示该配方不涉及此属性)',
  `dex_max` int NOT NULL DEFAULT '0' COMMENT '敏捷默认随机区间上限',
  `int_min` int NOT NULL DEFAULT '0' COMMENT '智力默认随机区间下限(0表示该配方不涉及此属性)',
  `int_max` int NOT NULL DEFAULT '0' COMMENT '智力默认随机区间上限',
  `luk_min` int NOT NULL DEFAULT '0' COMMENT '运气默认随机区间下限(0表示该配方不涉及此属性)',
  `luk_max` int NOT NULL DEFAULT '0' COMMENT '运气默认随机区间上限',
  `watk_min` int NOT NULL DEFAULT '0' COMMENT '攻击力默认随机区间下限(0表示该配方不涉及此属性)',
  `watk_max` int NOT NULL DEFAULT '0' COMMENT '攻击力默认随机区间上限',
  `matk_min` int NOT NULL DEFAULT '0' COMMENT '魔攻默认随机区间下限(0表示该配方不涉及此属性)',
  `matk_max` int NOT NULL DEFAULT '0' COMMENT '魔攻默认随机区间上限',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '同品级内显示排序，越小越靠前',
  `enabled` tinyint NOT NULL DEFAULT '0' COMMENT '是否启用：0=禁用（待数据确定前默认禁用） 1=启用',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  `material4_item_id` int DEFAULT NULL COMMENT '材料4物品ID(NULL表示不需要)',
  `material4_count` int NOT NULL DEFAULT '0' COMMENT '材料4所需数量',
  `material5_item_id` int DEFAULT NULL COMMENT '材料5物品ID(NULL表示不需要)',
  `material5_count` int NOT NULL DEFAULT '0' COMMENT '材料5所需数量',
  `material6_item_id` int DEFAULT NULL COMMENT '材料6物品ID(NULL表示不需要)',
  `material6_count` int NOT NULL DEFAULT '0' COMMENT '材料6所需数量',
  `material7_item_id` int DEFAULT NULL COMMENT '材料7物品ID(NULL表示不需要)',
  `material7_count` int NOT NULL DEFAULT '0' COMMENT '材料7所需数量',
  `material8_item_id` int DEFAULT NULL COMMENT '材料8物品ID(NULL表示不需要)',
  `material8_count` int NOT NULL DEFAULT '0' COMMENT '材料8所需数量',
  `pdd_min` int NOT NULL DEFAULT '0' COMMENT '物理防御力默认随机区间下限(0表示不涉及)',
  `pdd_max` int NOT NULL DEFAULT '0' COMMENT '物理防御力默认随机区间上限',
  `mdd_min` int NOT NULL DEFAULT '0' COMMENT '魔法防御力默认随机区间下限(0表示不涉及)',
  `mdd_max` int NOT NULL DEFAULT '0' COMMENT '魔法防御力默认随机区间上限',
  `hp_min` int NOT NULL DEFAULT '0' COMMENT 'MaxHP默认随机区间下限(0表示不涉及)',
  `hp_max` int NOT NULL DEFAULT '0' COMMENT 'MaxHP默认随机区间上限',
  `mp_min` int NOT NULL DEFAULT '0' COMMENT 'MaxMP默认随机区间下限(0表示不涉及)',
  `mp_max` int NOT NULL DEFAULT '0' COMMENT 'MaxMP默认随机区间上限',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='打造配方配置表（打造物品/材料数据待定，默认禁用，正式数据确定后启用）';

-- ===== xy_reforge_affix =====
CREATE TABLE IF NOT EXISTS `xy_reforge_affix` (
  `code` varchar(16) NOT NULL COMMENT '词条代码：HP/DEF/WAR/MAG/BOW/THF/ALL/PAD/MAD/BRAVE/WISE/SWIFT/THORN/HOLY/XIAN/SHEN',
  `name_zh` varchar(8) NOT NULL COMMENT '中文名：血/防/战/法/弓/侠/全/攻/魔/勇/慧/迅/刺/圣/仙/神',
  `max_prefix` tinyint NOT NULL DEFAULT '5' COMMENT '最高前缀等级：血/防固定1，其余5',
  `base_json` json NOT NULL COMMENT '①级基值JSON：{"HP":100,"MP":100} 或 {"STR":2,"DEX":1} 等',
  `weight` int NOT NULL DEFAULT '10' COMMENT '抽取权重(万分比相对权重)，血/防低权重降低常驻概率',
  `enabled` tinyint NOT NULL DEFAULT '1' COMMENT '是否启用：0=禁用 1=启用',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='洗炼词条配置表';

-- ===== xy_character_challenge_fatigue =====
CREATE TABLE IF NOT EXISTS `xy_character_challenge_fatigue` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `character_id` int NOT NULL COMMENT '角色ID',
  `challenge_type` tinyint NOT NULL COMMENT '1=普通 2=进阶 3=团队',
  `remaining` int NOT NULL DEFAULT '3' COMMENT '剩余次数，当日可用恢复剂叠加',
  `last_reset_date` date NOT NULL COMMENT '上次跨天重置日期，跨天时重置为3',
  `create_time` datetime NOT NULL COMMENT '创建时间',
  `update_time` datetime NOT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_char_type` (`character_id`,`challenge_type`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色挑战副本次数（按种类独立，跨天重置为3）';

-- ===== xy_character_challenge_log =====
CREATE TABLE IF NOT EXISTS `xy_character_challenge_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `character_id` int NOT NULL COMMENT '角色ID',
  `account_id` int NOT NULL COMMENT '账号ID',
  `challenge_type` tinyint NOT NULL COMMENT '1=普通 2=进阶 3=团队',
  `action_type` varchar(16) NOT NULL COMMENT 'ENTER=进入挑战 RESTORE=使用恢复剂',
  `boss_name` varchar(64) DEFAULT NULL COMMENT 'Boss名称（进入时）',
  `map_id` int DEFAULT NULL COMMENT '地图ID',
  `mob_ids` varchar(128) DEFAULT NULL COMMENT '怪物ID列表',
  `item_id` int DEFAULT NULL COMMENT '恢复剂物品ID',
  `remaining_after` int NOT NULL COMMENT '操作后剩余次数',
  `create_time` datetime NOT NULL COMMENT '记录时间',
  PRIMARY KEY (`id`),
  KEY `idx_char_time` (`character_id`,`create_time`),
  KEY `idx_type_time` (`challenge_type`,`create_time`),
  KEY `idx_account_time` (`account_id`,`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='角色挑战副本操作日志';

-- seed xy_alchemy_tier (15 rows)
DELETE FROM `xy_alchemy_tier`;
INSERT INTO `xy_alchemy_tier` (`id`,`type`,`name`,`exp_start`,`is_max`,`sort_order`,`enabled`,`create_time`,`update_time`) VALUES
(1,1,'入门',0,0,0,1,'2026-08-07 13:31:58','2026-08-07 13:31:58'),
(2,1,'普通',16000,0,1,1,'2026-08-07 13:31:58','2026-08-07 13:31:58'),
(3,1,'职业',32000,0,2,1,'2026-08-07 13:31:58','2026-08-07 13:31:58'),
(4,1,'大师',64000,0,3,1,'2026-08-07 13:31:58','2026-08-07 13:31:58'),
(5,1,'宗师',128000,1,4,1,'2026-08-07 13:31:58','2026-08-07 13:31:58'),
(6,2,'入门',0,0,0,1,'2026-08-07 17:09:59','2026-08-07 17:09:59'),
(7,2,'普通',16000,0,1,1,'2026-08-07 17:09:59','2026-08-07 17:09:59'),
(8,2,'职业',32000,0,2,1,'2026-08-07 17:09:59','2026-08-07 17:09:59'),
(9,2,'大师',64000,0,3,1,'2026-08-07 17:09:59','2026-08-07 17:09:59'),
(10,2,'宗师',128000,1,4,1,'2026-08-07 17:09:59','2026-08-07 17:09:59'),
(11,3,'入门',0,0,0,1,'2026-08-07 17:09:59','2026-08-07 17:09:59'),
(12,3,'普通',16000,0,1,1,'2026-08-07 17:09:59','2026-08-07 17:09:59'),
(13,3,'职业',32000,0,2,1,'2026-08-07 17:09:59','2026-08-07 17:09:59'),
(14,3,'大师',64000,0,3,1,'2026-08-07 17:09:59','2026-08-07 17:09:59'),
(15,3,'宗师',128000,1,4,1,'2026-08-07 17:09:59','2026-08-07 17:09:59');

-- seed xy_alchemy_recipe (18 rows)
DELETE FROM `xy_alchemy_recipe`;
INSERT INTO `xy_alchemy_recipe` (`id`,`tier_required`,`result_item_id`,`result_count`,`exp_gain`,`stamina_cost`,`meso_cost`,`cash_cost`,`material1_item_id`,`material1_count`,`material2_item_id`,`material2_count`,`material3_item_id`,`material3_count`,`sort_order`,`enabled`,`create_time`,`update_time`) VALUES
(1,1,0,1,40,50,100000,0,0,10,0,5,0,5,1,0,'2026-07-25 01:04:35','2026-07-25 01:04:35'),
(2,0,4032171,1,30,50,500000,0,4021009,2,NULL,0,NULL,0,0,1,'2026-08-07 14:10:40','2026-08-07 14:10:40'),
(3,0,4032169,1,30,50,500000,0,4011007,2,NULL,0,NULL,0,1,1,'2026-08-07 14:10:40','2026-08-07 14:10:40'),
(4,1,4025004,1,20,30,300000,0,4021009,3,4011007,3,NULL,0,0,1,'2026-08-07 14:10:40','2026-08-07 14:10:40'),
(5,1,4032170,1,70,100,1000000,0,4021009,3,4011007,3,4005004,6,1,1,'2026-08-07 14:10:40','2026-08-07 14:10:40'),
(6,1,4032862,1,70,100,2000000,0,4021009,5,4011007,5,4005004,10,2,1,'2026-08-07 14:10:40','2026-08-07 14:10:40'),
(7,1,2432443,1,70,100,2000000,0,4021009,5,4011007,5,4005004,10,3,1,'2026-08-07 14:10:40','2026-08-07 14:10:40'),
(8,2,4025005,1,80,100,300000,0,4021009,1,4011007,1,NULL,0,0,1,'2026-08-07 14:10:40','2026-08-09 15:19:50'),
(9,2,4032873,1,130,150,3000000,0,4021009,10,4011007,10,4005004,20,1,1,'2026-08-07 14:10:40','2026-08-07 14:10:40'),
(10,2,2432444,1,130,150,3000000,0,4021009,10,4011007,10,4005004,20,2,1,'2026-08-07 14:10:40','2026-08-07 14:10:40'),
(11,2,2614000,1,170,200,4000000,1000,4021009,15,4011007,15,4005004,30,3,1,'2026-08-07 14:10:40','2026-08-07 14:10:40'),
(12,3,4025006,1,90,100,300000,0,4021009,1,4011007,1,NULL,0,0,1,'2026-08-07 14:10:40','2026-08-07 14:10:40'),
(13,3,4032863,1,190,200,4000000,0,4021009,20,4011007,20,4005004,40,1,1,'2026-08-07 14:10:40','2026-08-07 14:10:40'),
(14,3,2432445,1,190,200,5000000,0,4021009,20,4011007,20,4005004,40,2,1,'2026-08-07 14:10:40','2026-08-07 14:10:40'),
(15,3,4021017,1,190,200,5000000,1000,4021009,20,4011007,20,4005004,40,3,1,'2026-08-07 14:10:40','2026-08-07 14:10:40'),
(16,4,4025007,1,100,100,300000,0,4021009,1,4011007,1,NULL,0,0,1,'2026-08-07 14:10:40','2026-08-07 14:10:40'),
(17,4,2432446,1,300,300,8000000,0,4021009,30,4011007,30,4005004,60,1,1,'2026-08-07 14:10:40','2026-08-07 14:10:40'),
(18,4,4032868,1,300,300,8000000,0,4021009,30,4011007,30,4005004,60,2,1,'2026-08-07 14:10:40','2026-08-07 14:10:40');

-- seed xy_alchemist_recipe (19 rows)
DELETE FROM `xy_alchemist_recipe`;
INSERT INTO `xy_alchemist_recipe` (`id`,`tier_required`,`result_item_id`,`result_count`,`exp_gain`,`stamina_cost`,`meso_cost`,`material1_item_id`,`material1_count`,`material2_item_id`,`material2_count`,`material3_item_id`,`material3_count`,`material4_item_id`,`material4_count`,`material5_item_id`,`material5_count`,`sort_order`,`enabled`,`create_time`,`update_time`) VALUES
(1,0,2003012,100,10,30,100000,4000379,100,4000380,100,4000381,100,NULL,0,NULL,0,0,1,'2026-08-07 17:10:00','2026-08-07 17:10:00'),
(2,0,2003032,100,10,30,100000,4000382,100,4000383,100,4000384,100,NULL,0,NULL,0,1,1,'2026-08-07 17:10:00','2026-08-07 17:10:00'),
(3,0,2003007,100,10,30,300000,4000379,200,4000380,200,4000381,200,NULL,0,NULL,0,2,1,'2026-08-07 17:10:00','2026-08-07 17:10:00'),
(4,0,2003027,100,10,30,300000,4000379,200,4000380,200,4000381,200,NULL,0,NULL,0,3,1,'2026-08-07 17:10:00','2026-08-07 17:10:00'),
(5,1,4025001,1,30,50,500000,4025004,1,4000040,2,4000178,200,NULL,0,NULL,0,0,1,'2026-08-07 17:10:00','2026-08-07 17:10:00'),
(6,1,2050004,20,20,50,100000,4000010,20,4000037,100,4000004,100,NULL,0,NULL,0,1,1,'2026-08-07 17:10:00','2026-08-07 17:10:00'),
(7,1,2450000,1,30,50,500000,4000028,20,4000046,20,4000027,50,NULL,0,NULL,0,2,1,'2026-08-07 17:10:00','2026-08-07 17:10:00'),
(8,2,4025001,1,50,70,700000,4025005,1,4000176,2,4000008,200,NULL,0,NULL,0,0,1,'2026-08-07 17:10:00','2026-08-07 17:10:00'),
(9,2,2000004,100,40,70,700000,4000292,100,4000293,100,4000294,100,NULL,0,NULL,0,1,1,'2026-08-07 17:10:00','2026-08-07 17:10:00'),
(10,2,2022345,1,50,70,700000,4000288,100,4000292,100,4000294,100,NULL,0,NULL,0,2,1,'2026-08-07 17:10:00','2026-08-07 17:10:00'),
(11,2,2022459,2,50,70,1000000,2022459,2,4000053,20,4000054,20,NULL,0,NULL,0,3,1,'2026-08-07 17:10:00','2026-08-07 17:10:00'),
(12,3,4025002,1,70,90,900000,4025006,1,4000195,2,4000215,200,NULL,0,NULL,0,0,1,'2026-08-07 17:10:00','2026-08-07 17:10:00'),
(13,3,2002023,100,70,90,900000,4000179,100,4000181,100,4000182,100,NULL,0,NULL,0,1,1,'2026-08-07 17:10:00','2026-08-07 17:10:00'),
(14,3,2004099,5,70,90,900000,4000262,100,4000263,100,4000272,100,NULL,0,NULL,0,2,1,'2026-08-07 17:10:00','2026-08-07 17:10:00'),
(15,3,2022918,1,70,90,5000000,4021009,10,4011007,10,4001241,2,4001242,2,NULL,0,3,1,'2026-08-07 17:10:00','2026-08-07 17:10:00'),
(16,4,4025003,1,120,150,1500000,4025007,1,4031991,2,4000114,200,NULL,0,NULL,0,0,1,'2026-08-07 17:10:00','2026-08-07 17:10:00'),
(17,4,2000005,100,70,90,1500000,4000268,100,4000269,100,4000270,100,NULL,0,NULL,0,1,1,'2026-08-07 17:10:00','2026-08-07 17:10:00'),
(18,4,2022070,1,120,150,2000000,4021009,20,4011007,20,2022345,1,NULL,0,NULL,0,2,1,'2026-08-07 17:10:00','2026-08-07 17:10:00'),
(19,4,2023132,1,120,150,5000000,4021009,30,4011007,30,4000461,2,4000462,2,4000460,2,3,1,'2026-08-07 17:10:00','2026-08-07 17:10:00');

-- seed xy_forge_recipe (27 rows)
DELETE FROM `xy_forge_recipe`;
INSERT INTO `xy_forge_recipe` (`id`,`name`,`tier_required`,`result_item_id`,`exp_gain`,`meso_cost`,`stamina_cost`,`material1_item_id`,`material1_count`,`material2_item_id`,`material2_count`,`material3_item_id`,`material3_count`,`str_min`,`str_max`,`dex_min`,`dex_max`,`int_min`,`int_max`,`luk_min`,`luk_max`,`watk_min`,`watk_max`,`matk_min`,`matk_max`,`sort_order`,`enabled`,`create_time`,`update_time`,`material4_item_id`,`material4_count`,`material5_item_id`,`material5_count`,`material6_item_id`,`material6_count`,`material7_item_id`,`material7_count`,`material8_item_id`,`material8_count`,`pdd_min`,`pdd_max`,`mdd_min`,`mdd_max`,`hp_min`,`hp_max`,`mp_min`,`mp_max`) VALUES
(3,'环游世界吊坠',0,1122300,300,1000000,300,4032171,10,4000998,1,4000019,200,1,5,1,5,1,5,1,5,1,2,1,2,1,1,'2026-09-01 16:33:20','2026-09-01 16:33:20',4000000,200,4000016,200,NULL,0,NULL,0,NULL,0,20,40,20,40,0,0,0,0),
(4,'环游世界腰带',0,1132278,300,1000000,300,4032171,10,4000998,1,4000037,200,1,5,1,5,1,5,1,5,1,2,1,2,2,1,'2026-09-01 16:33:20','2026-09-01 16:33:20',4000059,200,4000060,200,4000061,200,NULL,0,NULL,0,20,40,20,40,0,0,0,0),
(5,'环游世界耳饰',0,1032248,300,1000000,300,4032171,10,4000998,1,4000001,200,1,5,1,5,1,5,1,5,1,2,1,2,3,1,'2026-09-01 16:33:20','2026-09-01 16:33:20',4000002,200,4000003,200,4000004,200,NULL,0,NULL,0,20,40,20,40,0,0,0,0),
(6,'环游世界戒指',0,1113162,300,1000000,300,4032171,10,4000998,1,4000005,200,1,5,1,5,1,5,1,5,1,2,2,4,4,1,'2026-09-01 16:33:20','2026-09-01 16:33:20',4000006,200,4000007,200,4000008,200,NULL,0,NULL,0,20,40,20,40,0,0,0,0),
(7,'巨匠之戒',1,1113055,400,2000000,400,4032169,30,2432443,5,4025001,5,5,10,5,10,5,10,5,10,1,6,2,12,5,1,'2026-09-01 16:33:20','2026-09-01 16:33:20',4033098,1,4000009,200,4000010,200,4000011,200,4000012,200,40,80,40,80,0,0,0,0),
(8,'环游世界肩饰',1,1152173,400,2000000,400,4032169,30,2432443,3,4025001,3,3,7,3,7,3,7,3,7,2,4,4,8,6,1,'2026-09-01 16:33:20','2026-09-01 16:33:20',4033098,1,4000013,200,4000014,200,4000015,200,4000017,200,40,80,40,80,0,0,0,0),
(9,'环游世界脸饰',1,1012504,400,2000000,400,4032169,30,2432443,3,4025001,3,3,7,3,7,3,7,3,7,2,4,4,8,7,1,'2026-09-01 16:33:20','2026-09-01 16:33:20',4033098,1,4000018,200,4000020,200,4000021,200,4000022,200,40,80,40,80,0,0,0,0),
(10,'环游世界眼饰',1,1022238,400,2000000,400,4032169,30,2432443,3,4025001,3,3,7,3,7,3,7,3,7,2,4,4,8,8,1,'2026-09-01 16:33:20','2026-09-01 16:33:20',4033098,1,4000023,200,4000024,200,4000025,200,4000026,200,40,80,40,80,0,0,0,0),
(11,'金色珍珠眼影',2,1022255,500,3000000,500,4032170,50,2432444,3,4025001,3,3,8,3,8,3,8,3,8,1,6,2,12,9,1,'2026-09-01 16:33:20','2026-09-01 16:33:20',4033312,1,4000027,200,4000029,200,4000030,200,4000031,200,50,80,50,80,0,0,0,0),
(12,'灿烂金光图案',2,1012545,500,3000000,500,4032170,50,2432444,5,4025001,5,3,8,3,8,3,8,3,8,4,9,8,18,10,1,'2026-09-01 16:33:20','2026-09-01 16:33:20',4033312,1,4000032,200,4000033,200,4000034,200,4000035,200,50,80,50,80,0,0,0,0),
(13,'金花耳环',2,1032258,500,3000000,500,4032170,50,2432444,3,4025001,3,4,9,4,9,4,9,4,9,2,7,4,14,11,1,'2026-09-01 16:33:20','2026-09-01 16:33:20',4033312,1,4000036,200,4000039,200,4000041,200,4000042,200,50,80,50,80,0,0,0,0),
(14,'金花',2,1162037,500,3000000,500,4032170,50,2432444,5,4025001,5,3,8,3,8,3,8,3,8,4,9,8,18,12,1,'2026-09-01 16:33:20','2026-09-01 16:33:20',4033312,1,4000043,200,4000044,200,4000045,200,4000048,200,50,80,50,80,0,0,0,0),
(15,'金花徽章',2,1182175,500,3000000,500,4032170,50,2432444,5,4025001,5,5,10,5,10,5,10,5,10,5,10,10,20,13,1,'2026-09-01 16:33:20','2026-09-01 16:33:20',4033312,1,4000049,200,4000050,200,4000051,200,4000052,200,50,80,50,80,0,0,0,0),
(16,'金花护肩',2,1152192,500,3000000,500,4032170,50,2432444,5,4025001,5,8,13,8,13,8,13,8,13,4,9,8,18,14,1,'2026-09-01 16:33:20','2026-09-01 16:33:20',4033312,1,4000055,200,4000056,200,4000057,200,4000063,200,50,80,50,80,0,0,0,0),
(17,'天堂气息',3,1113211,700,5000000,700,4032171,70,2432445,7,4025002,7,20,30,20,30,20,30,20,30,10,30,20,60,15,1,'2026-09-01 16:33:20','2026-09-01 16:33:20',4000659,3,4000058,200,4000062,200,4000064,200,4000065,200,0,0,0,0,100,200,100,200),
(18,'金花耳环',3,1032258,700,5000000,700,4032171,70,2432445,5,4025002,5,4,9,4,9,4,9,4,9,2,7,4,14,16,1,'2026-09-01 16:33:20','2026-09-01 16:33:20',4000659,1,4000066,200,4000069,200,4000075,200,4000077,200,50,80,50,80,0,0,0,0),
(19,'金花腰带',3,1132294,700,5000000,700,4032171,70,2432445,5,4025002,5,5,15,5,15,5,15,5,15,3,8,6,16,17,1,'2026-09-01 16:33:20','2026-09-01 16:33:20',4000659,2,4000070,200,4000071,200,4000072,200,4000074,200,50,80,50,80,100,200,100,200),
(20,'金花吊坠',3,1122322,700,5000000,700,4032171,70,2432445,5,4025002,5,3,13,3,13,3,13,3,13,2,7,4,14,18,1,'2026-09-01 16:33:20','2026-09-01 16:33:20',4000659,2,4000078,200,4000079,200,4000080,200,4000081,200,80,100,80,100,0,0,0,0),
(21,'巨匠护肩',3,1152154,700,5000000,700,4032171,90,2432445,9,4025002,9,6,18,6,18,6,18,6,18,5,14,5,14,19,1,'2026-09-01 16:33:20','2026-09-01 16:33:20',4000659,2,4000082,200,4000083,200,4000084,200,4000085,200,80,150,80,150,0,0,0,0),
(22,'完全掌控',4,1672095,900,7000000,900,4032169,90,2432446,9,4025003,9,25,30,25,30,25,30,25,30,10,20,20,30,20,1,'2026-09-01 16:33:20','2026-09-01 16:33:20',4033255,2,4000073,200,4000086,200,4000087,200,4000088,200,0,0,0,0,0,0,0,0),
(23,'创世徽章',4,1182285,900,7000000,900,4032169,90,2432446,9,4025003,9,15,20,15,20,15,20,15,20,10,15,20,30,21,1,'2026-09-01 16:33:20','2026-09-01 16:33:20',4033255,2,4000089,200,4000090,200,4000091,200,4000092,200,0,0,0,0,0,0,0,0),
(24,'梦幻腰带',4,1132308,900,7000000,900,4032169,90,2432446,9,4025003,9,45,55,45,55,45,55,45,55,6,11,12,22,22,1,'2026-09-01 16:33:20','2026-09-01 16:33:20',4033255,2,4000103,200,4000104,200,4000105,200,4000099,200,100,150,100,150,150,200,150,200),
(25,'超越证明眼饰',4,1022195,900,7000000,900,4032170,90,2432446,9,4025003,9,15,30,15,30,15,30,15,30,10,20,20,40,23,1,'2026-09-01 16:33:20','2026-09-01 16:33:20',4033255,2,4000095,200,4000096,200,4000097,200,4000100,200,200,300,200,300,0,0,0,0),
(26,'超越证明脸饰',4,1012414,900,7000000,900,4032170,90,2432446,9,4025003,9,15,30,15,30,15,30,15,30,10,20,20,40,24,1,'2026-09-01 16:33:20','2026-09-01 16:33:20',4033255,2,4000101,200,4000102,200,4000106,200,4000107,200,200,300,200,300,0,0,0,0),
(27,'超越证明耳环',4,1032201,900,7000000,900,4032170,90,2432446,9,4025003,9,15,30,15,30,15,30,15,30,10,20,20,40,25,1,'2026-09-01 16:33:20','2026-09-01 16:33:20',4033255,2,4000108,200,4000109,200,4000110,200,4000111,200,200,300,200,300,0,0,0,0),
(28,'超越证明吊坠',4,1122259,900,7000000,900,4032170,90,2432446,9,4025003,9,15,30,15,30,15,30,15,30,10,20,20,40,26,1,'2026-09-01 16:33:20','2026-09-01 16:33:20',4033255,2,4000112,200,4000113,200,4000114,200,4000115,200,200,300,200,300,0,0,0,0),
(29,'超越证明戒指',4,1113056,900,7000000,900,4032170,90,2432446,9,4025003,9,15,30,15,30,15,30,15,30,10,20,20,40,27,1,'2026-09-01 16:33:20','2026-09-01 16:33:20',4033255,2,4000116,200,4000117,200,4000118,200,4000119,200,200,300,200,300,0,0,0,0);


-- seed xy_reforge_affix (16 rows)
DELETE FROM `xy_reforge_affix`;
INSERT INTO `xy_reforge_affix` (`code`,`name_zh`,`max_prefix`,`base_json`,`weight`,`enabled`,`create_time`,`update_time`) VALUES
('ALL','全',5,'{"ALLSTAT": 1}',8,1,'2026-07-30 21:32:59','2026-07-30 21:32:59'),
('BOW','弓',5,'{"DEX": 2, "STR": 1}',10,1,'2026-07-30 21:32:59','2026-07-30 21:32:59'),
('BRAVE','勇',5,'{"DEX": 1, "PAD": 1, "STR": 2}',6,1,'2026-07-30 21:32:59','2026-07-30 21:32:59'),
('DEF','防',1,'{"MDD": 50, "PDD": 50}',8,1,'2026-07-30 21:32:59','2026-07-30 21:32:59'),
('HOLY','圣',5,'{"HP": 20, "MP": 20, "PAD": 1, "ALLSTAT": 2}',4,1,'2026-07-30 21:32:59','2026-07-30 21:32:59'),
('HP','血',1,'{"HP": 100, "MP": 100}',8,1,'2026-07-30 21:32:59','2026-07-30 21:32:59'),
('MAD','魔',5,'{"MAD": 2}',10,1,'2026-07-30 21:32:59','2026-07-30 21:32:59'),
('MAG','法',5,'{"INT": 2, "LUK": 1}',10,1,'2026-07-30 21:32:59','2026-07-30 21:32:59'),
('PAD','攻',5,'{"PAD": 1}',10,1,'2026-07-30 21:32:59','2026-07-30 21:32:59'),
('SHEN','神',5,'{"HP": 30, "MP": 30, "MAD": 2, "PAD": 1, "ALLSTAT": 3}',3,1,'2026-07-30 21:32:59','2026-07-30 21:32:59'),
('SWIFT','迅',5,'{"DEX": 2, "PAD": 1, "STR": 1}',6,1,'2026-07-30 21:32:59','2026-07-30 21:32:59'),
('THF','侠',5,'{"DEX": 1, "LUK": 2}',10,1,'2026-07-30 21:32:59','2026-07-30 21:32:59'),
('THORN','刺',5,'{"DEX": 1, "LUK": 2, "PAD": 1}',6,1,'2026-07-30 21:32:59','2026-07-30 21:32:59'),
('WAR','战',5,'{"DEX": 1, "STR": 2}',10,1,'2026-07-30 21:32:59','2026-07-30 21:32:59'),
('WISE','慧',5,'{"INT": 2, "LUK": 1, "MAD": 2}',6,1,'2026-07-30 21:32:59','2026-07-30 21:32:59'),
('XIAN','仙',5,'{"HP": 20, "MP": 20, "MAD": 2, "ALLSTAT": 2}',4,1,'2026-07-30 21:32:59','2026-07-30 21:32:59');
