-- 锻造师系统：角色级锻造师等级/经验表 + 打造配方配置表（配方/产出物品数据待定，本迁移只建结构）
-- 品级曲线与炼药师/炼金师一致(入门/普通/职业/大师/宗师)，经验池完全独立，详见 org.gms.service.ForgeService.TIERS
CREATE TABLE IF NOT EXISTS `xy_character_forge` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `character_id` INT NOT NULL COMMENT '角色ID（锻造师等级/经验按角色隔离，独立于炼药师/炼金师经验池）',
    `exp` BIGINT NOT NULL DEFAULT 0 COMMENT '锻造师累计经验值，只增不减，升级不重置',
    `create_time` DATETIME NOT NULL COMMENT '创建时间',
    `update_time` DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_character_id` (`character_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色级锻造师等级经验表';

CREATE TABLE IF NOT EXISTS `xy_forge_recipe` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '配方名称(展示用，如：智慧戒指Ⅶ)',
    `tier_required` TINYINT NOT NULL DEFAULT 0 COMMENT '所需锻造师品级下标：0=入门 1=普通 2=职业 3=大师 4=宗师',
    `result_item_id` INT NOT NULL DEFAULT 0 COMMENT '打造产出装备ID（待定，占位）',
    `exp_gain` INT NOT NULL DEFAULT 0 COMMENT '打造成功增加的锻造师经验',
    `meso_cost` BIGINT NOT NULL DEFAULT 0 COMMENT '打造消耗金币',
    `material1_item_id` INT DEFAULT NULL COMMENT '材料1物品ID（待定，占位，NULL表示不需要）',
    `material1_count` INT NOT NULL DEFAULT 0 COMMENT '材料1所需数量',
    `material2_item_id` INT DEFAULT NULL COMMENT '材料2物品ID（待定，占位，NULL表示不需要）',
    `material2_count` INT NOT NULL DEFAULT 0 COMMENT '材料2所需数量',
    `material3_item_id` INT DEFAULT NULL COMMENT '材料3物品ID（待定，占位，NULL表示不需要）',
    `material3_count` INT NOT NULL DEFAULT 0 COMMENT '材料3所需数量',
    `str_min` INT NOT NULL DEFAULT 0 COMMENT '力量默认随机区间下限(0表示该配方不涉及此属性)',
    `str_max` INT NOT NULL DEFAULT 0 COMMENT '力量默认随机区间上限',
    `dex_min` INT NOT NULL DEFAULT 0 COMMENT '敏捷默认随机区间下限(0表示该配方不涉及此属性)',
    `dex_max` INT NOT NULL DEFAULT 0 COMMENT '敏捷默认随机区间上限',
    `int_min` INT NOT NULL DEFAULT 0 COMMENT '智力默认随机区间下限(0表示该配方不涉及此属性)',
    `int_max` INT NOT NULL DEFAULT 0 COMMENT '智力默认随机区间上限',
    `luk_min` INT NOT NULL DEFAULT 0 COMMENT '运气默认随机区间下限(0表示该配方不涉及此属性)',
    `luk_max` INT NOT NULL DEFAULT 0 COMMENT '运气默认随机区间上限',
    `watk_min` INT NOT NULL DEFAULT 0 COMMENT '攻击力默认随机区间下限(0表示该配方不涉及此属性)',
    `watk_max` INT NOT NULL DEFAULT 0 COMMENT '攻击力默认随机区间上限',
    `matk_min` INT NOT NULL DEFAULT 0 COMMENT '魔攻默认随机区间下限(0表示该配方不涉及此属性)',
    `matk_max` INT NOT NULL DEFAULT 0 COMMENT '魔攻默认随机区间上限',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '同品级内显示排序，越小越靠前',
    `enabled` TINYINT NOT NULL DEFAULT 0 COMMENT '是否启用：0=禁用（待数据确定前默认禁用） 1=启用',
    `create_time` DATETIME NOT NULL COMMENT '创建时间',
    `update_time` DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='打造配方配置表（打造物品/材料数据待定，默认禁用，正式数据确定后启用）';

-- 占位示例配方：智慧戒指Ⅶ，区间数值取自截图确认(智力9~19、运气9~19、魔攻9~26)，
-- result_item_id/材料/金币消耗仍为TODO占位，默认禁用，确认数据后改为enabled=1
INSERT INTO `xy_forge_recipe`
(`name`, `tier_required`, `result_item_id`, `exp_gain`, `meso_cost`,
 `material1_item_id`, `material1_count`, `material2_item_id`, `material2_count`, `material3_item_id`, `material3_count`,
 `int_min`, `int_max`, `luk_min`, `luk_max`, `matk_min`, `matk_max`,
 `sort_order`, `enabled`, `create_time`, `update_time`)
VALUES
('智慧戒指Ⅶ', 1, 0, 640, 0, NULL, 0, NULL, 0, NULL, 0, 9, 19, 9, 19, 9, 26, 1, 0, NOW(), NOW());
