-- 三副职业共用一张品级表：xy_alchemy_tier 增加 type 列（1=炼金 2=炼药 3=锻造），
-- 唯一性改为 (type, sort_order)，为炼药/锻造补种默认品级（曲线与炼金一致，最高品级无上限）。
ALTER TABLE `xy_alchemy_tier`
    ADD COLUMN `type` TINYINT NOT NULL DEFAULT 1 COMMENT '副职业类型：1=炼金 2=炼药 3=锻造' AFTER `id`;

ALTER TABLE `xy_alchemy_tier` DROP INDEX `uk_sort_order`;
ALTER TABLE `xy_alchemy_tier`
    ADD UNIQUE KEY `uk_type_sort_order` (`type`, `sort_order`);

-- 品级下标约定（每种类型独立）：0=入门 1=普通 2=职业 3=大师 4=宗师（达 exp_start 即进入，最高品级无上限）
INSERT INTO `xy_alchemy_tier` (`type`, `name`, `exp_start`, `is_max`, `sort_order`, `enabled`, `create_time`, `update_time`) VALUES
(2, '入门', 0,     0, 0, 1, NOW(), NOW()),
(2, '普通', 16000, 0, 1, 1, NOW(), NOW()),
(2, '职业', 32000, 0, 2, 1, NOW(), NOW()),
(2, '大师', 64000, 0, 3, 1, NOW(), NOW()),
(2, '宗师', 128000, 1, 4, 1, NOW(), NOW()),
(3, '入门', 0,     0, 0, 1, NOW(), NOW()),
(3, '普通', 16000, 0, 1, 1, NOW(), NOW()),
(3, '职业', 32000, 0, 2, 1, NOW(), NOW()),
(3, '大师', 64000, 0, 3, 1, NOW(), NOW()),
(3, '宗师', 128000, 1, 4, 1, NOW(), NOW());

-- 炼药师配方配置表（与炼金配方结构一致，支持至多5种材料）
CREATE TABLE IF NOT EXISTS `xy_alchemist_recipe` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `tier_required` TINYINT NOT NULL DEFAULT 0 COMMENT '所需炼药师品级下标：0=入门 1=普通 2=职业 3=大师 4=宗师',
    `result_item_id` INT NOT NULL DEFAULT 0 COMMENT '炼制产出物品ID',
    `result_count` INT NOT NULL DEFAULT 1 COMMENT '炼制产出物品数量',
    `exp_gain` INT NOT NULL DEFAULT 0 COMMENT '炼制成功增加的炼药师经验',
    `stamina_cost` INT NOT NULL DEFAULT 0 COMMENT '炼制消耗体力（账号通用体力池）',
    `meso_cost` BIGINT NOT NULL DEFAULT 0 COMMENT '炼制消耗金币',
    `material1_item_id` INT DEFAULT NULL COMMENT '材料1物品ID',
    `material1_count` INT NOT NULL DEFAULT 0 COMMENT '材料1所需数量',
    `material2_item_id` INT DEFAULT NULL COMMENT '材料2物品ID',
    `material2_count` INT NOT NULL DEFAULT 0 COMMENT '材料2所需数量',
    `material3_item_id` INT DEFAULT NULL COMMENT '材料3物品ID',
    `material3_count` INT NOT NULL DEFAULT 0 COMMENT '材料3所需数量',
    `material4_item_id` INT DEFAULT NULL COMMENT '材料4物品ID',
    `material4_count` INT NOT NULL DEFAULT 0 COMMENT '材料4所需数量',
    `material5_item_id` INT DEFAULT NULL COMMENT '材料5物品ID',
    `material5_count` INT NOT NULL DEFAULT 0 COMMENT '材料5所需数量',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '同品级内显示排序',
    `enabled` TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用：0=禁用 1=启用',
    `create_time` DATETIME NOT NULL COMMENT '创建时间',
    `update_time` DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='炼药师配方配置表（炼制物品/材料/消耗默认启用）';

-- 炼药师默认配方数据（来自策划数据）
INSERT INTO `xy_alchemist_recipe`
(`tier_required`, `result_item_id`, `result_count`, `exp_gain`, `stamina_cost`, `meso_cost`,
 `material1_item_id`, `material1_count`, `material2_item_id`, `material2_count`, `material3_item_id`, `material3_count`,
 `material4_item_id`, `material4_count`, `material5_item_id`, `material5_count`,
 `sort_order`, `enabled`, `create_time`, `update_time`)
VALUES
-- ========== 入门级炼药 ==========
(0, 2003012, 100, 10, 30, 100000, 4000379, 100, 4000380, 100, 4000381, 100, NULL, 0, NULL, 0, 0, 1, NOW(), NOW()), -- 体力恢复药丸2000
(0, 2003032, 100, 10, 30, 100000, 4000382, 100, 4000383, 100, 4000384, 100, NULL, 0, NULL, 0, 1, 1, NOW(), NOW()), -- 魔力恢复药丸2000
(0, 2003007, 100, 10, 30, 300000, 4000379, 200, 4000380, 200, 4000381, 200, NULL, 0, NULL, 0, 2, 1, NOW(), NOW()), -- 体力恢复药水5000
(0, 2003027, 100, 10, 30, 300000, 4000379, 200, 4000380, 200, 4000381, 200, NULL, 0, NULL, 0, 3, 1, NOW(), NOW()), -- 魔力恢复药水5000
-- ========== 普通级炼药 ==========
(1, 4025001, 1, 30, 50, 500000, 4025004, 1, 4000040, 2, 4000178, 200, NULL, 0, NULL, 0, 0, 1, NOW(), NOW()), -- 低级研磨剂
(1, 2050004, 20, 20, 50, 100000, 4000010, 20, 4000037, 100, 4000004, 100, NULL, 0, NULL, 0, 1, 1, NOW(), NOW()), -- 万能疗伤药
(1, 2450000, 1, 30, 50, 500000, 4000028, 20, 4000046, 20, 4000027, 50, NULL, 0, NULL, 0, 2, 1, NOW(), NOW()), -- 幸运的狩猎
-- ========== 职业级炼药 ==========
(2, 4025001, 1, 50, 70, 700000, 4025005, 1, 4000176, 2, 4000008, 200, NULL, 0, NULL, 0, 0, 1, NOW(), NOW()), -- 中级研磨剂
(2, 2000004, 100, 40, 70, 700000, 4000292, 100, 4000293, 100, 4000294, 100, NULL, 0, NULL, 0, 1, 1, NOW(), NOW()), -- 特殊药水
(2, 2022345, 1, 50, 70, 700000, 4000288, 100, 4000292, 100, 4000294, 100, NULL, 0, NULL, 0, 2, 1, NOW(), NOW()), -- 大力药水
(2, 2022459, 2, 50, 70, 1000000, 2022459, 2, 4000053, 20, 4000054, 20, NULL, 0, NULL, 0, 3, 1, NOW(), NOW()), -- 星缘的奖励1
-- ========== 大师级炼药 ==========
(3, 4025002, 1, 70, 90, 900000, 4025006, 1, 4000195, 2, 4000215, 200, NULL, 0, NULL, 0, 0, 1, NOW(), NOW()), -- 高级研磨剂
(3, 2002023, 100, 70, 90, 900000, 4000179, 100, 4000181, 100, 4000182, 100, NULL, 0, NULL, 0, 1, 1, NOW(), NOW()), -- 姜汁
(3, 2004099, 5, 70, 90, 900000, 4000262, 100, 4000263, 100, 4000272, 100, NULL, 0, NULL, 0, 2, 1, NOW(), NOW()), -- 强化10级防御药水
(3, 2022918, 1, 70, 90, 5000000, 4021009, 10, 4011007, 10, 4001241, 2, 4001242, 2, NULL, 0, 3, 1, NOW(), NOW()), -- 掉落率1.5倍券
-- ========== 宗师级炼药 ==========
(4, 4025003, 1, 120, 150, 1500000, 4025007, 1, 4031991, 2, 4000114, 200, NULL, 0, NULL, 0, 0, 1, NOW(), NOW()), -- 最高级研磨剂
(4, 2000005, 100, 70, 90, 1500000, 4000268, 100, 4000269, 100, 4000270, 100, NULL, 0, NULL, 0, 1, 1, NOW(), NOW()), -- 超级药水
(4, 2022070, 1, 120, 150, 2000000, 4021009, 20, 4011007, 20, 2022345, 1, NULL, 0, NULL, 0, 2, 1, NOW(), NOW()), -- 管理者的祝福
(4, 2023132, 1, 120, 150, 5000000, 4021009, 30, 4011007, 30, 4000461, 2, 4000462, 2, 4000460, 2, 3, 1, NOW(), NOW()); -- 掉落率2倍券