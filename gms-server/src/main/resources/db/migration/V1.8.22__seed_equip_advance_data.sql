-- 装备进阶路线种子数据（每个职业群一条示例路线，均为3阶段）
-- 阶段0=初始装备，阶段1=一阶，阶段2=二阶
-- 注意：此迁移会先删除V1.8.22可能残留的数据

-- ===== 战士路线 =====
INSERT INTO `xy_equip_advance_route` (`job_group`, `route_name`, `enabled`) VALUES ('warrior', '战士武器进阶', 1);

INSERT INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `str_add`, `dex_add`, `hp_add`, `watk_add`, `wdef_add`, `acc_add`, `avoid_add`)
SELECT r.id, 0, 1302000, '木剑', 0, 0, 0, 0, 0, 0, 0, 0 FROM `xy_equip_advance_route` r WHERE r.job_group = 'warrior';

INSERT INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `str_add`, `dex_add`, `hp_add`, `watk_add`, `wdef_add`, `acc_add`)
SELECT r.id, 1, 1302008, '长剑', 100000, 5, 2, 50, 8, 10, 5 FROM `xy_equip_advance_route` r WHERE r.job_group = 'warrior';

INSERT INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `str_add`, `dex_add`, `hp_add`, `watk_add`, `wdef_add`, `acc_add`, `avoid_add`)
SELECT r.id, 2, 1302017, '宽剑', 500000, 10, 5, 100, 15, 20, 10, 5 FROM `xy_equip_advance_route` r WHERE r.job_group = 'warrior';

INSERT INTO `xy_equip_advance_cost` (`stage_id`, `item_id`, `count`)
SELECT s.id, 4030000, 10 FROM `xy_equip_advance_stage` s JOIN `xy_equip_advance_route` r ON s.route_id = r.id WHERE r.job_group = 'warrior' AND s.stage_order = 1;

INSERT INTO `xy_equip_advance_cost` (`stage_id`, `item_id`, `count`)
SELECT s.id, 4030000, 30 FROM `xy_equip_advance_stage` s JOIN `xy_equip_advance_route` r ON s.route_id = r.id WHERE r.job_group = 'warrior' AND s.stage_order = 2;

-- ===== 弓箭手路线 =====
INSERT INTO `xy_equip_advance_route` (`job_group`, `route_name`, `enabled`) VALUES ('archer', '弓箭手武器进阶', 1);

INSERT INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `dex_add`, `luk_add`, `hp_add`, `watk_add`, `acc_add`)
SELECT r.id, 0, 1452000, '木弓', 0, 0, 0, 0, 0, 0 FROM `xy_equip_advance_route` r WHERE r.job_group = 'archer';

INSERT INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `dex_add`, `luk_add`, `hp_add`, `watk_add`, `acc_add`)
SELECT r.id, 1, 1452005, '战弓', 100000, 5, 2, 30, 8, 8 FROM `xy_equip_advance_route` r WHERE r.job_group = 'archer';

INSERT INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `dex_add`, `luk_add`, `hp_add`, `watk_add`, `acc_add`, `avoid_add`)
SELECT r.id, 2, 1452016, '复合弓', 500000, 10, 5, 60, 15, 15, 5 FROM `xy_equip_advance_route` r WHERE r.job_group = 'archer';

INSERT INTO `xy_equip_advance_cost` (`stage_id`, `item_id`, `count`)
SELECT s.id, 4030000, 10 FROM `xy_equip_advance_stage` s JOIN `xy_equip_advance_route` r ON s.route_id = r.id WHERE r.job_group = 'archer' AND s.stage_order = 1;

INSERT INTO `xy_equip_advance_cost` (`stage_id`, `item_id`, `count`)
SELECT s.id, 4030000, 30 FROM `xy_equip_advance_stage` s JOIN `xy_equip_advance_route` r ON s.route_id = r.id WHERE r.job_group = 'archer' AND s.stage_order = 2;

-- ===== 法师路线 =====
INSERT INTO `xy_equip_advance_route` (`job_group`, `route_name`, `enabled`) VALUES ('mage', '法师武器进阶', 1);

INSERT INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `int_add`, `luk_add`, `mp_add`, `matk_add`, `mdef_add`)
SELECT r.id, 0, 1372000, '木制法杖', 0, 0, 0, 0, 0, 0 FROM `xy_equip_advance_route` r WHERE r.job_group = 'mage';

INSERT INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `int_add`, `luk_add`, `mp_add`, `matk_add`, `mdef_add`)
SELECT r.id, 1, 1372005, '法杖', 100000, 5, 2, 50, 8, 10 FROM `xy_equip_advance_route` r WHERE r.job_group = 'mage';

INSERT INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `int_add`, `luk_add`, `mp_add`, `matk_add`, `mdef_add`)
SELECT r.id, 2, 1372010, '硬木法杖', 500000, 10, 5, 100, 15, 20 FROM `xy_equip_advance_route` r WHERE r.job_group = 'mage';

INSERT INTO `xy_equip_advance_cost` (`stage_id`, `item_id`, `count`)
SELECT s.id, 4030000, 10 FROM `xy_equip_advance_stage` s JOIN `xy_equip_advance_route` r ON s.route_id = r.id WHERE r.job_group = 'mage' AND s.stage_order = 1;

INSERT INTO `xy_equip_advance_cost` (`stage_id`, `item_id`, `count`)
SELECT s.id, 4030000, 30 FROM `xy_equip_advance_stage` s JOIN `xy_equip_advance_route` r ON s.route_id = r.id WHERE r.job_group = 'mage' AND s.stage_order = 2;

-- ===== 飞侠路线 =====
INSERT INTO `xy_equip_advance_route` (`job_group`, `route_name`, `enabled`) VALUES ('thief', '飞侠武器进阶', 1);

INSERT INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `dex_add`, `luk_add`, `hp_add`, `watk_add`, `avoid_add`)
SELECT r.id, 0, 1332000, '木制短刀', 0, 0, 0, 0, 0, 0 FROM `xy_equip_advance_route` r WHERE r.job_group = 'thief';

INSERT INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `dex_add`, `luk_add`, `hp_add`, `watk_add`, `avoid_add`)
SELECT r.id, 1, 1332005, '短刀', 100000, 2, 5, 30, 8, 5 FROM `xy_equip_advance_route` r WHERE r.job_group = 'thief';

INSERT INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `dex_add`, `luk_add`, `hp_add`, `watk_add`, `avoid_add`)
SELECT r.id, 2, 1332010, '三角刃', 500000, 5, 10, 60, 15, 10 FROM `xy_equip_advance_route` r WHERE r.job_group = 'thief';

INSERT INTO `xy_equip_advance_cost` (`stage_id`, `item_id`, `count`)
SELECT s.id, 4030000, 10 FROM `xy_equip_advance_stage` s JOIN `xy_equip_advance_route` r ON s.route_id = r.id WHERE r.job_group = 'thief' AND s.stage_order = 1;

INSERT INTO `xy_equip_advance_cost` (`stage_id`, `item_id`, `count`)
SELECT s.id, 4030000, 30 FROM `xy_equip_advance_stage` s JOIN `xy_equip_advance_route` r ON s.route_id = r.id WHERE r.job_group = 'thief' AND s.stage_order = 2;

-- ===== 海盗路线 =====
INSERT INTO `xy_equip_advance_route` (`job_group`, `route_name`, `enabled`) VALUES ('pirate', '海盗武器进阶', 1);

INSERT INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `str_add`, `dex_add`, `hp_add`, `watk_add`, `wdef_add`, `acc_add`)
SELECT r.id, 0, 1482000, '木制拳套', 0, 0, 0, 0, 0, 0, 0 FROM `xy_equip_advance_route` r WHERE r.job_group = 'pirate';

INSERT INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `str_add`, `dex_add`, `hp_add`, `watk_add`, `wdef_add`, `acc_add`)
SELECT r.id, 1, 1482005, '拳套', 100000, 3, 3, 40, 8, 5, 5 FROM `xy_equip_advance_route` r WHERE r.job_group = 'pirate';

INSERT INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `str_add`, `dex_add`, `hp_add`, `watk_add`, `wdef_add`, `acc_add`, `avoid_add`)
SELECT r.id, 2, 1482010, '钢铁拳套', 500000, 7, 7, 80, 15, 10, 10, 5 FROM `xy_equip_advance_route` r WHERE r.job_group = 'pirate';

INSERT INTO `xy_equip_advance_cost` (`stage_id`, `item_id`, `count`)
SELECT s.id, 4030000, 10 FROM `xy_equip_advance_stage` s JOIN `xy_equip_advance_route` r ON s.route_id = r.id WHERE r.job_group = 'pirate' AND s.stage_order = 1;

INSERT INTO `xy_equip_advance_cost` (`stage_id`, `item_id`, `count`)
SELECT s.id, 4030000, 30 FROM `xy_equip_advance_stage` s JOIN `xy_equip_advance_route` r ON s.route_id = r.id WHERE r.job_group = 'pirate' AND s.stage_order = 2;
