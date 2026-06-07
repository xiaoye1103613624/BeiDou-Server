-- =====================================================
-- 装备进阶系统配置表
-- 支持：按职业群划分进阶路线，多阶段进化装备
--   每阶属性与之前阶段叠加
--   剩余强化次数保留
-- =====================================================

-- 装备进阶路线配置主表（每个职业群一条路线）
CREATE TABLE IF NOT EXISTS `xy_equip_advance_route`
(
    `id`          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
    `job_group`   VARCHAR(32)  NOT NULL COMMENT '职业群（warrior/archer/mage/thief/pirate）',
    `route_name`  VARCHAR(128) NOT NULL COMMENT '路线名称',
    `enabled`     TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用（0=禁用 1=启用）',
    `create_time` TIMESTAMP             DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` TIMESTAMP             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    UNIQUE KEY `uk_job_group` (`job_group`)
) COMMENT '装备进阶路线配置表' ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 装备进阶阶段配置（每个路线包含多个阶段，0=初始装备，1+=进阶阶段）
CREATE TABLE IF NOT EXISTS `xy_equip_advance_stage`
(
    `id`               BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
    `route_id`         BIGINT       NOT NULL COMMENT '关联 xy_equip_advance_route.id',
    `stage_order`      INT          NOT NULL COMMENT '阶段顺序（0=初始装备，1=一阶，2=二阶...）',
    `target_item_id`   INT          NOT NULL COMMENT '该阶段目标装备ID',
    `target_item_name` VARCHAR(128) NOT NULL COMMENT '目标装备名称',
    `meso_cost`        INT          NOT NULL DEFAULT 0 COMMENT '金币消耗',
    `cash_cost`        INT          NOT NULL DEFAULT 0 COMMENT '点卷消耗',
    `credit_cost`      INT          NOT NULL DEFAULT 0 COMMENT '抵用券消耗',
    `str_add`          INT          NOT NULL DEFAULT 0 COMMENT '力量加成',
    `dex_add`          INT          NOT NULL DEFAULT 0 COMMENT '敏捷加成',
    `int_add`          INT          NOT NULL DEFAULT 0 COMMENT '智力加成',
    `luk_add`          INT          NOT NULL DEFAULT 0 COMMENT '运气加成',
    `hp_add`           INT          NOT NULL DEFAULT 0 COMMENT 'HP加成',
    `mp_add`           INT          NOT NULL DEFAULT 0 COMMENT 'MP加成',
    `watk_add`         INT          NOT NULL DEFAULT 0 COMMENT '物理攻击加成',
    `matk_add`         INT          NOT NULL DEFAULT 0 COMMENT '魔法攻击加成',
    `wdef_add`         INT          NOT NULL DEFAULT 0 COMMENT '物理防御加成',
    `mdef_add`         INT          NOT NULL DEFAULT 0 COMMENT '魔法防御加成',
    `acc_add`          INT          NOT NULL DEFAULT 0 COMMENT '命中加成',
    `avoid_add`        INT          NOT NULL DEFAULT 0 COMMENT '回避加成',
    `speed_add`        INT          NOT NULL DEFAULT 0 COMMENT '速度加成',
    `jump_add`         INT          NOT NULL DEFAULT 0 COMMENT '跳跃加成',
    `create_time`      TIMESTAMP             DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      TIMESTAMP             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    UNIQUE KEY `uk_route_stage` (`route_id`, `stage_order`),
    FOREIGN KEY (`route_id`) REFERENCES `xy_equip_advance_route` (`id`) ON DELETE CASCADE
) COMMENT '装备进阶阶段配置表' ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 装备进阶材料消耗（每个阶段可配置多种材料）
CREATE TABLE IF NOT EXISTS `xy_equip_advance_cost`
(
    `id`          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
    `stage_id`    BIGINT NOT NULL COMMENT '关联 xy_equip_advance_stage.id',
    `item_id`     INT    NOT NULL COMMENT '消耗道具ID',
    `count`       INT    NOT NULL DEFAULT 1 COMMENT '消耗数量',
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    FOREIGN KEY (`stage_id`) REFERENCES `xy_equip_advance_stage` (`id`) ON DELETE CASCADE
) COMMENT '装备进阶材料消耗表' ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- =====================================================
-- 种子数据：每个职业群一条示例进阶路线（3阶段）
-- 阶段0=初始装备，阶段1=一阶，阶段2=二阶
-- =====================================================

-- 战士路线（木剑 → 长剑 → 宽剑）
INSERT IGNORE INTO `xy_equip_advance_route` (`job_group`, `route_name`, `enabled`) VALUES ('warrior', '战士武器进阶', 1);

INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `str_add`, `dex_add`, `hp_add`, `watk_add`, `wdef_add`, `acc_add`, `avoid_add`)
SELECT r.id, 0, 1302000, '木剑', 0, 0, 0, 0, 0, 0, 0, 0 FROM `xy_equip_advance_route` r WHERE r.job_group = 'warrior';

INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `str_add`, `dex_add`, `hp_add`, `watk_add`, `wdef_add`, `acc_add`)
SELECT r.id, 1, 1302008, '长剑', 100000, 5, 2, 50, 8, 10, 5 FROM `xy_equip_advance_route` r WHERE r.job_group = 'warrior';

INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `str_add`, `dex_add`, `hp_add`, `watk_add`, `wdef_add`, `acc_add`, `avoid_add`)
SELECT r.id, 2, 1302017, '宽剑', 500000, 10, 5, 100, 15, 20, 10, 5 FROM `xy_equip_advance_route` r WHERE r.job_group = 'warrior';

INSERT INTO `xy_equip_advance_cost` (`stage_id`, `item_id`, `count`)
SELECT s.id, 4030000, 10 FROM `xy_equip_advance_stage` s JOIN `xy_equip_advance_route` r ON s.route_id = r.id WHERE r.job_group = 'warrior' AND s.stage_order = 1 AND NOT EXISTS (SELECT 1 FROM `xy_equip_advance_cost` c WHERE c.stage_id = s.id AND c.item_id = 4030000);

INSERT INTO `xy_equip_advance_cost` (`stage_id`, `item_id`, `count`)
SELECT s.id, 4030000, 30 FROM `xy_equip_advance_stage` s JOIN `xy_equip_advance_route` r ON s.route_id = r.id WHERE r.job_group = 'warrior' AND s.stage_order = 2 AND NOT EXISTS (SELECT 1 FROM `xy_equip_advance_cost` c WHERE c.stage_id = s.id AND c.item_id = 4030000);

-- 弓箭手路线（木弓 → 战弓 → 复合弓）
INSERT IGNORE INTO `xy_equip_advance_route` (`job_group`, `route_name`, `enabled`) VALUES ('archer', '弓箭手武器进阶', 1);

INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `dex_add`, `luk_add`, `hp_add`, `watk_add`, `acc_add`)
SELECT r.id, 0, 1452000, '木弓', 0, 0, 0, 0, 0, 0 FROM `xy_equip_advance_route` r WHERE r.job_group = 'archer';

INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `dex_add`, `luk_add`, `hp_add`, `watk_add`, `acc_add`)
SELECT r.id, 1, 1452005, '战弓', 100000, 5, 2, 30, 8, 8 FROM `xy_equip_advance_route` r WHERE r.job_group = 'archer';

INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `dex_add`, `luk_add`, `hp_add`, `watk_add`, `acc_add`, `avoid_add`)
SELECT r.id, 2, 1452016, '复合弓', 500000, 10, 5, 60, 15, 15, 5 FROM `xy_equip_advance_route` r WHERE r.job_group = 'archer';

INSERT INTO `xy_equip_advance_cost` (`stage_id`, `item_id`, `count`)
SELECT s.id, 4030000, 10 FROM `xy_equip_advance_stage` s JOIN `xy_equip_advance_route` r ON s.route_id = r.id WHERE r.job_group = 'archer' AND s.stage_order = 1 AND NOT EXISTS (SELECT 1 FROM `xy_equip_advance_cost` c WHERE c.stage_id = s.id AND c.item_id = 4030000);

INSERT INTO `xy_equip_advance_cost` (`stage_id`, `item_id`, `count`)
SELECT s.id, 4030000, 30 FROM `xy_equip_advance_stage` s JOIN `xy_equip_advance_route` r ON s.route_id = r.id WHERE r.job_group = 'archer' AND s.stage_order = 2 AND NOT EXISTS (SELECT 1 FROM `xy_equip_advance_cost` c WHERE c.stage_id = s.id AND c.item_id = 4030000);

-- 法师路线（木制法杖 → 法杖 → 硬木法杖）
INSERT IGNORE INTO `xy_equip_advance_route` (`job_group`, `route_name`, `enabled`) VALUES ('mage', '法师武器进阶', 1);

INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `int_add`, `luk_add`, `mp_add`, `matk_add`, `mdef_add`)
SELECT r.id, 0, 1372000, '木制法杖', 0, 0, 0, 0, 0, 0 FROM `xy_equip_advance_route` r WHERE r.job_group = 'mage';

INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `int_add`, `luk_add`, `mp_add`, `matk_add`, `mdef_add`)
SELECT r.id, 1, 1372005, '法杖', 100000, 5, 2, 50, 8, 10 FROM `xy_equip_advance_route` r WHERE r.job_group = 'mage';

INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `int_add`, `luk_add`, `mp_add`, `matk_add`, `mdef_add`)
SELECT r.id, 2, 1372010, '硬木法杖', 500000, 10, 5, 100, 15, 20 FROM `xy_equip_advance_route` r WHERE r.job_group = 'mage';

INSERT INTO `xy_equip_advance_cost` (`stage_id`, `item_id`, `count`)
SELECT s.id, 4030000, 10 FROM `xy_equip_advance_stage` s JOIN `xy_equip_advance_route` r ON s.route_id = r.id WHERE r.job_group = 'mage' AND s.stage_order = 1 AND NOT EXISTS (SELECT 1 FROM `xy_equip_advance_cost` c WHERE c.stage_id = s.id AND c.item_id = 4030000);

INSERT INTO `xy_equip_advance_cost` (`stage_id`, `item_id`, `count`)
SELECT s.id, 4030000, 30 FROM `xy_equip_advance_stage` s JOIN `xy_equip_advance_route` r ON s.route_id = r.id WHERE r.job_group = 'mage' AND s.stage_order = 2 AND NOT EXISTS (SELECT 1 FROM `xy_equip_advance_cost` c WHERE c.stage_id = s.id AND c.item_id = 4030000);

-- 飞侠路线（木制短刀 → 短刀 → 三角刃）
INSERT IGNORE INTO `xy_equip_advance_route` (`job_group`, `route_name`, `enabled`) VALUES ('thief', '飞侠武器进阶', 1);

INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `dex_add`, `luk_add`, `hp_add`, `watk_add`, `avoid_add`)
SELECT r.id, 0, 1332000, '木制短刀', 0, 0, 0, 0, 0, 0 FROM `xy_equip_advance_route` r WHERE r.job_group = 'thief';

INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `dex_add`, `luk_add`, `hp_add`, `watk_add`, `avoid_add`)
SELECT r.id, 1, 1332005, '短刀', 100000, 2, 5, 30, 8, 5 FROM `xy_equip_advance_route` r WHERE r.job_group = 'thief';

INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `dex_add`, `luk_add`, `hp_add`, `watk_add`, `avoid_add`)
SELECT r.id, 2, 1332010, '三角刃', 500000, 5, 10, 60, 15, 10 FROM `xy_equip_advance_route` r WHERE r.job_group = 'thief';

INSERT INTO `xy_equip_advance_cost` (`stage_id`, `item_id`, `count`)
SELECT s.id, 4030000, 10 FROM `xy_equip_advance_stage` s JOIN `xy_equip_advance_route` r ON s.route_id = r.id WHERE r.job_group = 'thief' AND s.stage_order = 1 AND NOT EXISTS (SELECT 1 FROM `xy_equip_advance_cost` c WHERE c.stage_id = s.id AND c.item_id = 4030000);

INSERT INTO `xy_equip_advance_cost` (`stage_id`, `item_id`, `count`)
SELECT s.id, 4030000, 30 FROM `xy_equip_advance_stage` s JOIN `xy_equip_advance_route` r ON s.route_id = r.id WHERE r.job_group = 'thief' AND s.stage_order = 2 AND NOT EXISTS (SELECT 1 FROM `xy_equip_advance_cost` c WHERE c.stage_id = s.id AND c.item_id = 4030000);

-- 海盗路线（木制拳套 → 拳套 → 钢铁拳套）
INSERT IGNORE INTO `xy_equip_advance_route` (`job_group`, `route_name`, `enabled`) VALUES ('pirate', '海盗武器进阶', 1);

INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `str_add`, `dex_add`, `hp_add`, `watk_add`, `wdef_add`, `acc_add`)
SELECT r.id, 0, 1482000, '木制拳套', 0, 0, 0, 0, 0, 0, 0 FROM `xy_equip_advance_route` r WHERE r.job_group = 'pirate';

INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `str_add`, `dex_add`, `hp_add`, `watk_add`, `wdef_add`, `acc_add`)
SELECT r.id, 1, 1482005, '拳套', 100000, 3, 3, 40, 8, 5, 5 FROM `xy_equip_advance_route` r WHERE r.job_group = 'pirate';

INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `str_add`, `dex_add`, `hp_add`, `watk_add`, `wdef_add`, `acc_add`, `avoid_add`)
SELECT r.id, 2, 1482010, '钢铁拳套', 500000, 7, 7, 80, 15, 10, 10, 5 FROM `xy_equip_advance_route` r WHERE r.job_group = 'pirate';

INSERT INTO `xy_equip_advance_cost` (`stage_id`, `item_id`, `count`)
SELECT s.id, 4030000, 10 FROM `xy_equip_advance_stage` s JOIN `xy_equip_advance_route` r ON s.route_id = r.id WHERE r.job_group = 'pirate' AND s.stage_order = 1 AND NOT EXISTS (SELECT 1 FROM `xy_equip_advance_cost` c WHERE c.stage_id = s.id AND c.item_id = 4030000);

INSERT INTO `xy_equip_advance_cost` (`stage_id`, `item_id`, `count`)
SELECT s.id, 4030000, 30 FROM `xy_equip_advance_stage` s JOIN `xy_equip_advance_route` r ON s.route_id = r.id WHERE r.job_group = 'pirate' AND s.stage_order = 2 AND NOT EXISTS (SELECT 1 FROM `xy_equip_advance_cost` c WHERE c.stage_id = s.id AND c.item_id = 4030000);
