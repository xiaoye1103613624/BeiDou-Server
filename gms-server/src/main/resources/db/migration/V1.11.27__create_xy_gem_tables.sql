-- 宝石合成 + 宝石镶嵌 配置表
-- 宝石合成：2个N级宝石合成1个N+1级宝石，1~16级，物品ID/合成金币消耗均待定（占位，enabled=0待数据确认后启用）
-- 宝石镶嵌：消耗对应等级宝石 + 属性水晶 + 金币，为已装备的装备永久叠加属性，叠加值通过 safeAddStat 钳制防溢出

CREATE TABLE `xy_gem_level` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `gem_level` INT NOT NULL COMMENT '宝石等级，1~16级',
    `item_id` INT NOT NULL COMMENT '该等级宝石的物品ID（占位，待正式数据确认后替换）',
    `item_name` VARCHAR(64) DEFAULT NULL COMMENT '该等级宝石名称，仅用于后台展示',
    `synthesis_meso_cost` BIGINT NOT NULL DEFAULT 0 COMMENT '由该等级合成下一等级所需金币（最高级无需填写）',
    `enabled` TINYINT NOT NULL DEFAULT 0 COMMENT '是否启用：0=禁用 1=启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_gem_level` (`gem_level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宝石等级配置表（1~16级，每级2个低级宝石合成1个高级宝石）';

CREATE TABLE `xy_gem_socket_level` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `gem_level` INT NOT NULL COMMENT '对应宝石等级，关联 xy_gem_level.gem_level',
    `crystal_item_id` INT NOT NULL COMMENT '镶嵌所需属性水晶物品ID（如力量水晶/智慧水晶等）',
    `crystal_count` INT NOT NULL DEFAULT 1 COMMENT '镶嵌所需属性水晶数量',
    `meso_cost` BIGINT NOT NULL DEFAULT 0 COMMENT '镶嵌所需金币',
    `str_add` SMALLINT NOT NULL DEFAULT 0 COMMENT '本次镶嵌叠加力量',
    `dex_add` SMALLINT NOT NULL DEFAULT 0 COMMENT '本次镶嵌叠加敏捷',
    `int_add` SMALLINT NOT NULL DEFAULT 0 COMMENT '本次镶嵌叠加智力',
    `luk_add` SMALLINT NOT NULL DEFAULT 0 COMMENT '本次镶嵌叠加运气',
    `watk_add` SMALLINT NOT NULL DEFAULT 0 COMMENT '本次镶嵌叠加物攻',
    `matk_add` SMALLINT NOT NULL DEFAULT 0 COMMENT '本次镶嵌叠加魔攻',
    `enabled` TINYINT NOT NULL DEFAULT 0 COMMENT '是否启用：0=禁用 1=启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_socket_gem_level` (`gem_level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宝石镶嵌配置表（每等级宝石对应一组镶嵌消耗与属性加成，永久写入装备属性）';

-- 占位数据：1~16级宝石，物品ID/合成金币均为占位（TODO：正式数据确认后替换 item_id，并将 enabled 改为1）
INSERT INTO `xy_gem_level` (`gem_level`, `item_id`, `item_name`, `synthesis_meso_cost`, `enabled`) VALUES
(1, 9031940, '1级宝石', 0, 0),
(2, 9031941, '2级宝石', 0, 0),
(3, 9031942, '3级宝石', 0, 0),
(4, 9031943, '4级宝石', 0, 0),
(5, 9031944, '5级宝石', 0, 0),
(6, 9031945, '6级宝石', 0, 0),
(7, 9031946, '7级宝石', 0, 0),
(8, 9031947, '8级宝石', 0, 0),
(9, 9031948, '9级宝石', 0, 0),
(10, 9031949, '10级宝石', 0, 0),
(11, 9031950, '11级宝石', 0, 0),
(12, 9031951, '12级宝石', 0, 0),
(13, 9031952, '13级宝石', 0, 0),
(14, 9031953, '14级宝石', 0, 0),
(15, 9031954, '15级宝石', 0, 0),
(16, 9031955, '16级宝石', 0, 0);

-- 占位数据：1级宝石镶嵌示例，参照截图"力量+2,物攻+1"，属性水晶暂用已核实存在的力量水晶(4004000)占位
INSERT INTO `xy_gem_socket_level` (`gem_level`, `crystal_item_id`, `crystal_count`, `meso_cost`, `str_add`, `watk_add`, `enabled`) VALUES
(1, 4004000, 5, 10000, 2, 1, 0);
