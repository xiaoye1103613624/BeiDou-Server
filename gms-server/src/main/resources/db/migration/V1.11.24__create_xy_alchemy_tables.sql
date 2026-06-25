-- 炼金师系统：角色级炼金师等级/经验表 + 炼金配方配置表（配方/产出物品数据待定，本迁移只建结构）
CREATE TABLE IF NOT EXISTS `xy_character_alchemy` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `character_id` INT NOT NULL COMMENT '角色ID（炼金师等级/经验按角色隔离，独立于炼药师经验池）',
    `exp` BIGINT NOT NULL DEFAULT 0 COMMENT '炼金师累计经验值，只增不减，升级不重置',
    `create_time` DATETIME NOT NULL COMMENT '创建时间',
    `update_time` DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_character_id` (`character_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色级炼金师等级经验表';

CREATE TABLE IF NOT EXISTS `xy_alchemy_recipe` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `tier_required` TINYINT NOT NULL DEFAULT 0 COMMENT '所需炼金师品级下标：0=入门 1=普通 2=职业 3=大师 4=宗师',
    `result_item_id` INT NOT NULL DEFAULT 0 COMMENT '炼制产出物品ID（待定，占位）',
    `result_count` INT NOT NULL DEFAULT 1 COMMENT '炼制产出物品数量',
    `exp_gain` INT NOT NULL DEFAULT 0 COMMENT '炼制成功增加的炼金师经验',
    `stamina_cost` INT NOT NULL DEFAULT 0 COMMENT '炼制消耗体力（账号通用体力池）',
    `meso_cost` BIGINT NOT NULL DEFAULT 0 COMMENT '炼制消耗金币',
    `cash_cost` INT NOT NULL DEFAULT 0 COMMENT '炼制消耗点券(NX_CREDIT)',
    `material1_item_id` INT DEFAULT NULL COMMENT '材料1物品ID（待定，占位，NULL表示不需要）',
    `material1_count` INT NOT NULL DEFAULT 0 COMMENT '材料1所需数量',
    `material2_item_id` INT DEFAULT NULL COMMENT '材料2物品ID（待定，占位，NULL表示不需要）',
    `material2_count` INT NOT NULL DEFAULT 0 COMMENT '材料2所需数量',
    `material3_item_id` INT DEFAULT NULL COMMENT '材料3物品ID（待定，占位，NULL表示不需要）',
    `material3_count` INT NOT NULL DEFAULT 0 COMMENT '材料3所需数量',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '同品级内显示排序，越小越靠前',
    `enabled` TINYINT NOT NULL DEFAULT 0 COMMENT '是否启用：0=禁用（待数据确定前默认禁用） 1=启用',
    `create_time` DATETIME NOT NULL COMMENT '创建时间',
    `update_time` DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='炼金配方配置表（炼制物品/材料数据待定，默认禁用，正式数据确定后启用）';

-- 占位示例配方（默认禁用，仅作为数据结构参考，正式数据确定后修改并启用）
INSERT INTO `xy_alchemy_recipe`
(`tier_required`, `result_item_id`, `result_count`, `exp_gain`, `stamina_cost`, `meso_cost`, `cash_cost`,
 `material1_item_id`, `material1_count`, `material2_item_id`, `material2_count`, `material3_item_id`, `material3_count`,
 `sort_order`, `enabled`, `create_time`, `update_time`)
VALUES
(1, 0, 1, 40, 50, 100000, 0, 0, 10, 0, 5, 0, 5, 1, 0, NOW(), NOW());
