-- =====================================================
-- 细化装备进阶路线：按具体职业拆分，不再使用粗粒度职业群
-- 旧路线（warrior/archer/mage/thief/pirate）替换为具体职业ID路线
-- e.g. warrior → warrior_hero(112)/warrior_paladin(122)/warrior_darkknight(132)
-- =====================================================

-- 清除旧粗粒度种子数据
DELETE FROM `xy_equip_advance_cost` WHERE stage_id IN (
    SELECT s.id FROM `xy_equip_advance_stage` s
    JOIN `xy_equip_advance_route` r ON s.route_id = r.id
    WHERE r.job_group IN ('warrior', 'archer', 'mage', 'thief', 'pirate')
);
DELETE FROM `xy_equip_advance_stage` WHERE route_id IN (
    SELECT id FROM `xy_equip_advance_route` WHERE job_group IN ('warrior', 'archer', 'mage', 'thief', 'pirate')
);
DELETE FROM `xy_equip_advance_route` WHERE job_group IN ('warrior', 'archer', 'mage', 'thief', 'pirate');

-- =====================================================
-- 细化种子数据（按具体职业ID，每个4转职业一条武器进阶路线）
-- 阶段0=初始武器，阶段1=一阶，阶段2=二阶
-- =====================================================

-- === 战士系 ===

-- 英雄(112) — 单手剑路线
INSERT IGNORE INTO `xy_equip_advance_route` (`job_group`, `route_name`, `enabled`) VALUES ('112', '英雄武器进阶(剑)', 1);
INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `str_add`, `dex_add`, `hp_add`, `watk_add`, `wdef_add`, `acc_add`)
SELECT r.id, 0, 1302000, '木剑', 0, 0, 0, 0, 0, 0, 0 FROM `xy_equip_advance_route` r WHERE r.job_group = '112';
INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `str_add`, `dex_add`, `hp_add`, `watk_add`, `wdef_add`, `acc_add`)
SELECT r.id, 1, 1302008, '长剑', 100000, 5, 2, 50, 8, 10, 5 FROM `xy_equip_advance_route` r WHERE r.job_group = '112';
INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `str_add`, `dex_add`, `hp_add`, `watk_add`, `wdef_add`, `acc_add`, `avoid_add`)
SELECT r.id, 2, 1302017, '宽剑', 500000, 10, 5, 100, 15, 20, 10, 5 FROM `xy_equip_advance_route` r WHERE r.job_group = '112';

-- 圣骑士(122) — 单手钝器路线
INSERT IGNORE INTO `xy_equip_advance_route` (`job_group`, `route_name`, `enabled`) VALUES ('122', '圣骑士武器进阶(钝器)', 1);
INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `str_add`, `dex_add`, `hp_add`, `watk_add`, `wdef_add`, `acc_add`)
SELECT r.id, 0, 1322000, '木制短锤', 0, 0, 0, 0, 0, 0, 0 FROM `xy_equip_advance_route` r WHERE r.job_group = '122';
INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `str_add`, `dex_add`, `hp_add`, `watk_add`, `wdef_add`, `acc_add`)
SELECT r.id, 1, 1322005, '短锤', 100000, 5, 2, 50, 8, 10, 5 FROM `xy_equip_advance_route` r WHERE r.job_group = '122';
INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `str_add`, `dex_add`, `hp_add`, `watk_add`, `wdef_add`, `acc_add`, `avoid_add`)
SELECT r.id, 2, 1322011, '战斗锤', 500000, 10, 5, 100, 15, 20, 10, 5 FROM `xy_equip_advance_route` r WHERE r.job_group = '122';

-- 龙骑士(132) — 枪/矛路线
INSERT IGNORE INTO `xy_equip_advance_route` (`job_group`, `route_name`, `enabled`) VALUES ('132', '龙骑士武器进阶(枪)', 1);
INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `str_add`, `dex_add`, `hp_add`, `watk_add`, `wdef_add`, `acc_add`)
SELECT r.id, 0, 1432000, '木制长枪', 0, 0, 0, 0, 0, 0, 0 FROM `xy_equip_advance_route` r WHERE r.job_group = '132';
INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `str_add`, `dex_add`, `hp_add`, `watk_add`, `wdef_add`, `acc_add`)
SELECT r.id, 1, 1432005, '长枪', 100000, 5, 2, 50, 8, 10, 5 FROM `xy_equip_advance_route` r WHERE r.job_group = '132';
INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `str_add`, `dex_add`, `hp_add`, `watk_add`, `wdef_add`, `acc_add`, `avoid_add`)
SELECT r.id, 2, 1432010, '钢铁长枪', 500000, 10, 5, 100, 15, 20, 10, 5 FROM `xy_equip_advance_route` r WHERE r.job_group = '132';

-- === 弓箭手系 ===

-- 神射手(312) — 弓路线
INSERT IGNORE INTO `xy_equip_advance_route` (`job_group`, `route_name`, `enabled`) VALUES ('312', '神射手武器进阶(弓)', 1);
INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `dex_add`, `luk_add`, `hp_add`, `watk_add`, `acc_add`)
SELECT r.id, 0, 1452000, '木弓', 0, 0, 0, 0, 0, 0 FROM `xy_equip_advance_route` r WHERE r.job_group = '312';
INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `dex_add`, `luk_add`, `hp_add`, `watk_add`, `acc_add`)
SELECT r.id, 1, 1452005, '战弓', 100000, 5, 2, 30, 8, 8 FROM `xy_equip_advance_route` r WHERE r.job_group = '312';
INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `dex_add`, `luk_add`, `hp_add`, `watk_add`, `acc_add`, `avoid_add`)
SELECT r.id, 2, 1452016, '复合弓', 500000, 10, 5, 60, 15, 15, 5 FROM `xy_equip_advance_route` r WHERE r.job_group = '312';

-- 箭神(322) — 弩路线
INSERT IGNORE INTO `xy_equip_advance_route` (`job_group`, `route_name`, `enabled`) VALUES ('322', '箭神武器进阶(弩)', 1);
INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `dex_add`, `luk_add`, `hp_add`, `watk_add`, `acc_add`)
SELECT r.id, 0, 1462000, '木制弩', 0, 0, 0, 0, 0, 0 FROM `xy_equip_advance_route` r WHERE r.job_group = '322';
INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `dex_add`, `luk_add`, `hp_add`, `watk_add`, `acc_add`)
SELECT r.id, 1, 1462004, '战弩', 100000, 5, 2, 30, 8, 8 FROM `xy_equip_advance_route` r WHERE r.job_group = '322';
INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `dex_add`, `luk_add`, `hp_add`, `watk_add`, `acc_add`, `avoid_add`)
SELECT r.id, 2, 1462008, '复合弩', 500000, 10, 5, 60, 15, 15, 5 FROM `xy_equip_advance_route` r WHERE r.job_group = '322';

-- === 法师系 ===

-- 火毒(212) — 长杖路线
INSERT IGNORE INTO `xy_equip_advance_route` (`job_group`, `route_name`, `enabled`) VALUES ('212', '火毒法师武器进阶(长杖)', 1);
INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `int_add`, `luk_add`, `mp_add`, `matk_add`, `mdef_add`)
SELECT r.id, 0, 1382000, '木制长杖', 0, 0, 0, 0, 0, 0 FROM `xy_equip_advance_route` r WHERE r.job_group = '212';
INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `int_add`, `luk_add`, `mp_add`, `matk_add`, `mdef_add`)
SELECT r.id, 1, 1382006, '长杖', 100000, 5, 2, 50, 8, 10 FROM `xy_equip_advance_route` r WHERE r.job_group = '212';
INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `int_add`, `luk_add`, `mp_add`, `matk_add`, `mdef_add`)
SELECT r.id, 2, 1382012, '硬木长杖', 500000, 10, 5, 100, 15, 20 FROM `xy_equip_advance_route` r WHERE r.job_group = '212';

-- 冰雷(222) — 短杖路线
INSERT IGNORE INTO `xy_equip_advance_route` (`job_group`, `route_name`, `enabled`) VALUES ('222', '冰雷法师武器进阶(短杖)', 1);
INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `int_add`, `luk_add`, `mp_add`, `matk_add`, `mdef_add`)
SELECT r.id, 0, 1372000, '木制法杖', 0, 0, 0, 0, 0, 0 FROM `xy_equip_advance_route` r WHERE r.job_group = '222';
INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `int_add`, `luk_add`, `mp_add`, `matk_add`, `mdef_add`)
SELECT r.id, 1, 1372005, '法杖', 100000, 5, 2, 50, 8, 10 FROM `xy_equip_advance_route` r WHERE r.job_group = '222';
INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `int_add`, `luk_add`, `mp_add`, `matk_add`, `mdef_add`)
SELECT r.id, 2, 1372010, '硬木法杖', 500000, 10, 5, 100, 15, 20 FROM `xy_equip_advance_route` r WHERE r.job_group = '222';

-- 主教(232) — 长杖路线（与火毒相同武器类型）
INSERT IGNORE INTO `xy_equip_advance_route` (`job_group`, `route_name`, `enabled`) VALUES ('232', '主教武器进阶(长杖)', 1);
INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `int_add`, `luk_add`, `mp_add`, `matk_add`, `mdef_add`)
SELECT r.id, 0, 1382000, '木制长杖', 0, 0, 0, 0, 0, 0 FROM `xy_equip_advance_route` r WHERE r.job_group = '232';
INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `int_add`, `luk_add`, `mp_add`, `matk_add`, `mdef_add`)
SELECT r.id, 1, 1382006, '长杖', 100000, 5, 2, 50, 8, 10 FROM `xy_equip_advance_route` r WHERE r.job_group = '232';
INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `int_add`, `luk_add`, `mp_add`, `matk_add`, `mdef_add`)
SELECT r.id, 2, 1382012, '硬木长杖', 500000, 10, 5, 100, 15, 20 FROM `xy_equip_advance_route` r WHERE r.job_group = '232';

-- === 飞侠系 ===

-- 隐士(412) — 拳套路线
INSERT IGNORE INTO `xy_equip_advance_route` (`job_group`, `route_name`, `enabled`) VALUES ('412', '隐士武器进阶(拳套)', 1);
INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `dex_add`, `luk_add`, `hp_add`, `watk_add`, `avoid_add`)
SELECT r.id, 0, 1472000, '木制拳套', 0, 0, 0, 0, 0, 0 FROM `xy_equip_advance_route` r WHERE r.job_group = '412';
INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `dex_add`, `luk_add`, `hp_add`, `watk_add`, `avoid_add`)
SELECT r.id, 1, 1472005, '拳套', 100000, 2, 5, 30, 8, 5 FROM `xy_equip_advance_route` r WHERE r.job_group = '412';
INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `dex_add`, `luk_add`, `hp_add`, `watk_add`, `avoid_add`)
SELECT r.id, 2, 1472012, '钢铁拳套', 500000, 5, 10, 60, 15, 10 FROM `xy_equip_advance_route` r WHERE r.job_group = '412';

-- 侠盗(422) — 短刀路线
INSERT IGNORE INTO `xy_equip_advance_route` (`job_group`, `route_name`, `enabled`) VALUES ('422', '侠盗武器进阶(短刀)', 1);
INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `dex_add`, `luk_add`, `hp_add`, `watk_add`, `avoid_add`)
SELECT r.id, 0, 1332000, '木制短刀', 0, 0, 0, 0, 0, 0 FROM `xy_equip_advance_route` r WHERE r.job_group = '422';
INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `dex_add`, `luk_add`, `hp_add`, `watk_add`, `avoid_add`)
SELECT r.id, 1, 1332005, '短刀', 100000, 2, 5, 30, 8, 5 FROM `xy_equip_advance_route` r WHERE r.job_group = '422';
INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `dex_add`, `luk_add`, `hp_add`, `watk_add`, `avoid_add`)
SELECT r.id, 2, 1332010, '三角刃', 500000, 5, 10, 60, 15, 10 FROM `xy_equip_advance_route` r WHERE r.job_group = '422';

-- === 海盗系 ===

-- 冲锋队长(512) — 拳套路线
INSERT IGNORE INTO `xy_equip_advance_route` (`job_group`, `route_name`, `enabled`) VALUES ('512', '冲锋队长武器进阶(指节)', 1);
INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `str_add`, `dex_add`, `hp_add`, `watk_add`, `wdef_add`, `acc_add`)
SELECT r.id, 0, 1482000, '木制指节', 0, 0, 0, 0, 0, 0, 0 FROM `xy_equip_advance_route` r WHERE r.job_group = '512';
INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `str_add`, `dex_add`, `hp_add`, `watk_add`, `wdef_add`, `acc_add`)
SELECT r.id, 1, 1482005, '指节', 100000, 3, 3, 40, 8, 5, 5 FROM `xy_equip_advance_route` r WHERE r.job_group = '512';
INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `str_add`, `dex_add`, `hp_add`, `watk_add`, `wdef_add`, `acc_add`, `avoid_add`)
SELECT r.id, 2, 1482010, '钢铁指节', 500000, 7, 7, 80, 15, 10, 10, 5 FROM `xy_equip_advance_route` r WHERE r.job_group = '512';

-- 船长(522) — 手枪路线
INSERT IGNORE INTO `xy_equip_advance_route` (`job_group`, `route_name`, `enabled`) VALUES ('522', '船长武器进阶(手枪)', 1);
INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `str_add`, `dex_add`, `hp_add`, `watk_add`, `wdef_add`, `acc_add`)
SELECT r.id, 0, 1492000, '木制手枪', 0, 0, 0, 0, 0, 0, 0 FROM `xy_equip_advance_route` r WHERE r.job_group = '522';
INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `str_add`, `dex_add`, `hp_add`, `watk_add`, `wdef_add`, `acc_add`)
SELECT r.id, 1, 1492005, '手枪', 100000, 3, 3, 40, 8, 5, 5 FROM `xy_equip_advance_route` r WHERE r.job_group = '522';
INSERT IGNORE INTO `xy_equip_advance_stage` (`route_id`, `stage_order`, `target_item_id`, `target_item_name`, `meso_cost`, `str_add`, `dex_add`, `hp_add`, `watk_add`, `wdef_add`, `acc_add`, `avoid_add`)
SELECT r.id, 2, 1492010, '钢铁手枪', 500000, 7, 7, 80, 15, 10, 10, 5 FROM `xy_equip_advance_route` r WHERE r.job_group = '522';
