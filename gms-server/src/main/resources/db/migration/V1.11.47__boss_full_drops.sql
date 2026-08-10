-- Boss完整掉落补全：装备散件+材料+宝石+卷轴 (2026-07-30)
-- chance基数1,000,000
-- 使用ON DUPLICATE KEY UPDATE保底不覆盖已有数据

-- ============================================================
-- 一、普通挑战副本Boss（补充匠人币+勇者石）
-- ============================================================
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(3501008, 4001126, 10, 30, 0, 150000),
(3501008, 4000313, 1, 3, 0, 120000),
(3502008, 4001126, 15, 40, 0, 150000),
(3502008, 4000313, 2, 5, 0, 120000),
(8240046, 4001126, 20, 50, 0, 150000),
(8240046, 4000313, 3, 6, 0, 130000),
(8240046, 4001136, 5, 15, 0, 180000),
(7220003, 4001126, 25, 60, 0, 150000),
(7220003, 4000313, 3, 8, 0, 140000),
(7220003, 4000314, 1, 2, 0, 60000),
(9600086, 4001126, 30, 70, 0, 150000),
(9600086, 4000313, 3, 8, 0, 140000),
(9600086, 4000314, 1, 2, 0, 70000),
(8240098, 4001126, 30, 80, 0, 150000),
(8240098, 4000313, 5, 10, 0, 140000),
(8240098, 4000314, 2, 3, 0, 80000),
(8240105, 4001126, 30, 80, 0, 150000),
(8240105, 4000313, 5, 10, 0, 140000),
(8240105, 4000314, 2, 3, 0, 80000)
ON DUPLICATE KEY UPDATE chance = chance;

-- ============================================================
-- 二、团队挑战副本Boss（装备散件+宝石+材料）
-- ============================================================

-- 希拉(8870000)：暴君碎片+女皇装+S宝石
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(8870000, 4001126, 50, 100, 0, 200000),
(8870000, 4000314, 2, 5, 0, 150000),
(8870000, 4021017, 1, 1, 0, 30000),
(8870000, 4443300, 1, 1, 0, 50000),
(8870000, 4441300, 1, 2, 0, 120000),
(8870000, 2049300, 1, 3, 0, 100000),
(8870000, 2049402, 1, 1, 0, 60000)
ON DUPLICATE KEY UPDATE chance = chance;

-- 麦格纳斯(8880000)：暴君披风/鞋+S宝石
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(8880000, 4001126, 60, 120, 0, 200000),
(8880000, 4000314, 3, 6, 0, 160000),
(8880000, 4021017, 1, 2, 0, 40000),
(8880000, 4443300, 1, 1, 0, 60000),
(8880000, 4441300, 2, 3, 0, 150000),
(8880000, 2049302, 1, 2, 0, 80000),
(8880000, 2460002, 1, 1, 0, 50000)
ON DUPLICATE KEY UPDATE chance = chance;

-- 暴君/困难麦格纳斯(8880002)：暴君套装散件+SS宝石
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(8880002, 4001126, 80, 150, 0, 200000),
(8880002, 4000314, 5, 10, 0, 180000),
(8880002, 4021017, 2, 3, 0, 60000),
(8880002, 4443300, 1, 2, 0, 100000),
(8880002, 4444300, 1, 1, 0, 30000),
(8880002, 2049305, 1, 2, 0, 60000),
(8880002, 5062000, 1, 1, 0, 30000)
ON DUPLICATE KEY UPDATE chance = chance;

-- 四凶·穷奇(8880830)
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(8880830, 4001126, 100, 200, 0, 200000),
(8880830, 4000314, 8, 15, 0, 180000),
(8880830, 4444300, 1, 1, 0, 50000),
(8880830, 2049305, 1, 3, 0, 80000),
(8880830, 5062001, 1, 1, 0, 25000)
ON DUPLICATE KEY UPDATE chance = chance;

-- 四凶·梼杌(8880831)
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(8880831, 4001126, 100, 200, 0, 200000),
(8880831, 4000314, 8, 15, 0, 180000),
(8880831, 4444300, 1, 1, 0, 50000),
(8880831, 2049305, 1, 3, 0, 80000),
(8880831, 2049912, 1, 1, 0, 25000)
ON DUPLICATE KEY UPDATE chance = chance;

-- 四凶·混沌(8880832)
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(8880832, 4001126, 120, 250, 0, 200000),
(8880832, 4000314, 10, 20, 0, 180000),
(8880832, 4444300, 1, 2, 0, 60000),
(8880832, 2049305, 2, 5, 0, 100000),
(8880832, 5062002, 1, 1, 0, 20000)
ON DUPLICATE KEY UPDATE chance = chance;

-- ============================================================
-- 三、进阶挑战副本Boss（套装散件+高级材料）
-- ============================================================

-- 鲁塔比斯大厅四皇(普通：8910100/8900100/8920100/8930100)：补充更多掉落
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance)
SELECT mob.dropperid, d.itemid, d.minimum_quantity, d.maximum_quantity, d.questid, d.chance
FROM (
    SELECT 8910100 AS dropperid UNION ALL SELECT 8900100 UNION ALL SELECT 8920100 UNION ALL SELECT 8930100
) mob CROSS JOIN (
    SELECT 4001126 AS itemid, 50 AS minimum_quantity, 100 AS maximum_quantity, 0 AS questid, 200000 AS chance UNION ALL
    SELECT 4000314, 3, 6, 0, 170000 UNION ALL
    SELECT 4021017, 1, 1, 0, 40000 UNION ALL
    SELECT 4443300, 1, 1, 0, 70000 UNION ALL
    SELECT 2460003, 1, 1, 0, 40000
) d ON DUPLICATE KEY UPDATE chance = chance;

-- 半半(混沌 8910000)
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(8910000, 4001126, 80, 150, 0, 200000),
(8910000, 4000314, 5, 10, 0, 180000),
(8910000, 4021017, 2, 3, 0, 60000),
(8910000, 4443300, 1, 2, 0, 100000),
(8910000, 4444300, 1, 1, 0, 35000),
(8910000, 2049400, 1, 2, 0, 120000),
(8910000, 5062000, 1, 1, 0, 40000)
ON DUPLICATE KEY UPDATE chance = chance;

-- 皮埃尔(混沌 8900000)
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(8900000, 4001126, 80, 150, 0, 200000),
(8900000, 4000314, 5, 10, 0, 180000),
(8900000, 4021017, 2, 3, 0, 60000),
(8900000, 4443300, 1, 2, 0, 100000),
(8900000, 4444300, 1, 1, 0, 35000),
(8900000, 2049402, 1, 2, 0, 120000),
(8900000, 5062000, 1, 1, 0, 40000)
ON DUPLICATE KEY UPDATE chance = chance;

-- 血腥女王(混沌 8920000)
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(8920000, 4001126, 80, 150, 0, 200000),
(8920000, 4000314, 5, 10, 0, 180000),
(8920000, 4021017, 2, 3, 0, 60000),
(8920000, 4443300, 1, 2, 0, 100000),
(8920000, 4444300, 1, 1, 0, 35000),
(8920000, 2049402, 1, 2, 0, 120000),
(8920000, 5062001, 1, 1, 0, 30000)
ON DUPLICATE KEY UPDATE chance = chance;

-- 贝伦(混沌 8930000)
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(8930000, 4001126, 100, 200, 0, 200000),
(8930000, 4000314, 8, 15, 0, 180000),
(8930000, 4021017, 3, 5, 0, 70000),
(8930000, 4444300, 1, 2, 0, 50000),
(8930000, 2049400, 2, 3, 0, 150000),
(8930000, 2049402, 2, 3, 0, 150000),
(8930000, 5062002, 1, 1, 0, 30000)
ON DUPLICATE KEY UPDATE chance = chance;

-- 希纳斯(8850011)
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(8850011, 4001126, 100, 200, 0, 200000),
(8850011, 4000314, 8, 12, 0, 180000),
(8850011, 4021017, 2, 4, 0, 60000),
(8850011, 4443300, 2, 3, 0, 120000),
(8850011, 4444300, 1, 1, 0, 40000),
(8850011, 2049400, 1, 2, 0, 100000),
(8850011, 5062001, 1, 1, 0, 35000)
ON DUPLICATE KEY UPDATE chance = chance;

-- 进阶扎昆(8800102)
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(8800102, 4001126, 80, 150, 0, 200000),
(8800102, 4000314, 5, 10, 0, 180000),
(8800102, 4021017, 1, 3, 0, 50000),
(8800102, 4443300, 1, 2, 0, 100000),
(8800102, 4444300, 1, 1, 0, 30000),
(8800102, 2049303, 2, 4, 0, 120000),
(8800102, 5062000, 1, 1, 0, 40000)
ON DUPLICATE KEY UPDATE chance = chance;

-- ============================================================
-- 四、高级BOSS系统 (T5→T0 分档掉落)
-- ============================================================

-- T5：皮埃尔 紫/蓝/红 (8900000/8900002/8900101)
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance)
SELECT mob.dropperid, d.itemid, d.minimum_quantity, d.maximum_quantity, d.questid, d.chance
FROM (
    SELECT 8900000 AS dropperid UNION ALL SELECT 8900002 UNION ALL SELECT 8900101
) mob CROSS JOIN (
    SELECT 4000313 AS itemid, 3 AS minimum_quantity, 8 AS maximum_quantity, 0 AS questid, 150000 AS chance UNION ALL
    SELECT 4001126, 30, 60, 0, 200000 UNION ALL
    SELECT 4032171, 1, 3, 0, 120000 UNION ALL
    SELECT 4441300, 1, 1, 0, 80000 UNION ALL
    SELECT 2049300, 1, 2, 0, 100000 UNION ALL
    SELECT 2049402, 1, 1, 0, 60000
) d ON DUPLICATE KEY UPDATE chance = chance;

-- T4：火狐/黑暗恶狼/黑暗剑圣 (9700043/8220109/9601505)
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance)
SELECT mob.dropperid, d.itemid, d.minimum_quantity, d.maximum_quantity, d.questid, d.chance
FROM (
    SELECT 9700043 AS dropperid UNION ALL SELECT 8220109 UNION ALL SELECT 9601505
) mob CROSS JOIN (
    SELECT 4000313 AS itemid, 5 AS minimum_quantity, 12 AS maximum_quantity, 0 AS questid, 160000 AS chance UNION ALL
    SELECT 4001126, 40, 80, 0, 200000 UNION ALL
    SELECT 4032171, 2, 5, 0, 130000 UNION ALL
    SELECT 4000314, 1, 2, 0, 60000 UNION ALL
    SELECT 4441300, 1, 2, 0, 100000 UNION ALL
    SELECT 2049300, 2, 3, 0, 120000 UNION ALL
    SELECT 2460001, 1, 1, 0, 50000
) d ON DUPLICATE KEY UPDATE chance = chance;

-- T3：黄龙/赤虎/蛇兄弟 (9601013/9601014/9601015)
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance)
SELECT mob.dropperid, d.itemid, d.minimum_quantity, d.maximum_quantity, d.questid, d.chance
FROM (
    SELECT 9601013 AS dropperid UNION ALL SELECT 9601014 UNION ALL SELECT 9601015
) mob CROSS JOIN (
    SELECT 4000314 AS itemid, 2 AS minimum_quantity, 4 AS maximum_quantity, 0 AS questid, 160000 AS chance UNION ALL
    SELECT 4001126, 50, 100, 0, 200000 UNION ALL
    SELECT 4032171, 3, 6, 0, 140000 UNION ALL
    SELECT 4021017, 1, 1, 0, 30000 UNION ALL
    SELECT 4443300, 1, 1, 0, 60000 UNION ALL
    SELECT 2049302, 1, 2, 0, 100000 UNION ALL
    SELECT 2460002, 1, 1, 0, 50000
) d ON DUPLICATE KEY UPDATE chance = chance;

-- T2：森兰丸/麦格纳斯/敦凯尔 (8850013/8880000/8645009)
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance)
SELECT mob.dropperid, d.itemid, d.minimum_quantity, d.maximum_quantity, d.questid, d.chance
FROM (
    SELECT 8850013 AS dropperid UNION ALL SELECT 8880000 UNION ALL SELECT 8645009
) mob CROSS JOIN (
    SELECT 4000314 AS itemid, 3 AS minimum_quantity, 6 AS maximum_quantity, 0 AS questid, 170000 AS chance UNION ALL
    SELECT 4001126, 60, 120, 0, 200000 UNION ALL
    SELECT 4032171, 5, 10, 0, 150000 UNION ALL
    SELECT 4021017, 1, 2, 0, 50000 UNION ALL
    SELECT 4443300, 1, 2, 0, 80000 UNION ALL
    SELECT 4444300, 1, 1, 0, 20000 UNION ALL
    SELECT 2049303, 1, 2, 0, 100000
) d ON DUPLICATE KEY UPDATE chance = chance;

-- T1：光明剑灵/黑暗剑灵/黑魔法师 (8880500/8880501/8880502)
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance)
SELECT mob.dropperid, d.itemid, d.minimum_quantity, d.maximum_quantity, d.questid, d.chance
FROM (
    SELECT 8880500 AS dropperid UNION ALL SELECT 8880501 UNION ALL SELECT 8880502
) mob CROSS JOIN (
    SELECT 4000314 AS itemid, 5 AS minimum_quantity, 10 AS maximum_quantity, 0 AS questid, 180000 AS chance UNION ALL
    SELECT 4001126, 80, 150, 0, 200000 UNION ALL
    SELECT 4032171, 8, 15, 0, 150000 UNION ALL
    SELECT 4021017, 2, 3, 0, 60000 UNION ALL
    SELECT 4444300, 1, 1, 0, 40000 UNION ALL
    SELECT 2049305, 1, 2, 0, 80000 UNION ALL
    SELECT 5062000, 1, 1, 0, 40000)
) d ON DUPLICATE KEY UPDATE chance = chance;

-- T0：塞伦一阶/二阶/三阶 (8880612/8880603/8880609)
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance)
SELECT mob.dropperid, d.itemid, d.minimum_quantity, d.maximum_quantity, d.questid, d.chance
FROM (
    SELECT 8880612 AS dropperid UNION ALL SELECT 8880603 UNION ALL SELECT 8880609
) mob CROSS JOIN (
    SELECT 4000314 AS itemid, 8 AS minimum_quantity, 15 AS maximum_quantity, 0 AS questid, 190000 AS chance UNION ALL
    SELECT 4001126, 100, 200, 0, 200000 UNION ALL
    SELECT 4032171, 10, 20, 0, 150000 UNION ALL
    SELECT 4021017, 3, 5, 0, 80000 UNION ALL
    SELECT 4444300, 1, 2, 0, 60000 UNION ALL
    SELECT 2049305, 2, 4, 0, 100000 UNION ALL
    SELECT 5062001, 1, 1, 0, 50000
) d ON DUPLICATE KEY UPDATE chance = chance;

-- 神器：皇帝/貔貅 (9410224/8880803)
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance)
SELECT mob.dropperid, d.itemid, d.minimum_quantity, d.maximum_quantity, d.questid, d.chance
FROM (
    SELECT 9410224 AS dropperid UNION ALL SELECT 8880803
) mob CROSS JOIN (
    SELECT 4000314 AS itemid, 10 AS minimum_quantity, 20 AS maximum_quantity, 0 AS questid, 200000 AS chance UNION ALL
    SELECT 4001126, 150, 300, 0, 200000 UNION ALL
    SELECT 4032171, 15, 30, 0, 160000 UNION ALL
    SELECT 4021017, 5, 8, 0, 100000 UNION ALL
    SELECT 4444300, 2, 3, 0, 80000 UNION ALL
    SELECT 2049307, 2, 5, 0, 100000 UNION ALL
    SELECT 5062002, 1, 1, 0, 60000
) d ON DUPLICATE KEY UPDATE chance = chance;

-- ============================================================
-- 五、083原生Boss补充材料（扎昆/黑龙/品克缤）
-- ============================================================

-- 扎昆本体(8800002)：增加匠人币+通用材料
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(8800002, 4001126, 20, 50, 0, 200000),
(8800002, 4000313, 2, 5, 0, 150000),
(8800002, 4032171, 1, 3, 0, 100000),
(8800002, 2049300, 1, 2, 0, 100000)
ON DUPLICATE KEY UPDATE chance = chance;

-- 黑龙本体(8810018)
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(8810018, 4001126, 30, 60, 0, 200000),
(8810018, 4000314, 1, 3, 0, 150000),
(8810018, 4032171, 2, 5, 0, 120000),
(8810018, 4021017, 1, 1, 0, 30000),
(8810018, 2049302, 1, 2, 0, 80000)
ON DUPLICATE KEY UPDATE chance = chance;

-- 品克缤本体(8820001)
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(8820001, 4001126, 40, 80, 0, 200000),
(8820001, 4000314, 2, 4, 0, 160000),
(8820001, 4032171, 3, 6, 0, 130000),
(8820001, 4021017, 1, 2, 0, 50000),
(8820001, 4443300, 1, 1, 0, 60000),
(8820001, 2049303, 1, 2, 0, 80000)
ON DUPLICATE KEY UPDATE chance = chance;

-- ============================================================
-- 六、全域通用材料掉落补全（野外Boss+常规Boss）
-- ============================================================

-- 肯德熊(7220000)
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(7220000, 4001126, 5, 15, 0, 180000),
(7220000, 4000313, 1, 2, 0, 120000)
ON DUPLICATE KEY UPDATE chance = chance;

-- 九尾狐(7220001)
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(7220001, 4001126, 5, 15, 0, 180000),
(7220001, 4000313, 1, 2, 0, 120000)
ON DUPLICATE KEY UPDATE chance = chance;

-- 妖怪禅师(7220002)
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(7220002, 4001126, 8, 20, 0, 180000),
(7220002, 4000313, 1, 3, 0, 130000)
ON DUPLICATE KEY UPDATE chance = chance;

-- 天鹰(8180001)
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(8180001, 4001126, 15, 30, 0, 180000),
(8180001, 4000313, 2, 4, 0, 140000),
(8180001, 4000314, 1, 1, 0, 50000)
ON DUPLICATE KEY UPDATE chance = chance;

-- 火焰龙(8180000)
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(8180000, 4001126, 15, 30, 0, 180000),
(8180000, 4000313, 2, 4, 0, 140000),
(8180000, 4000314, 1, 1, 0, 50000)
ON DUPLICATE KEY UPDATE chance = chance;

-- 大海兽(8220003)
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(8220003, 4001126, 10, 25, 0, 180000),
(8220003, 4000313, 1, 3, 0, 130000)
ON DUPLICATE KEY UPDATE chance = chance;

-- 蝙蝠怪(8130100)
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(8130100, 4001126, 5, 10, 0, 150000),
(8130100, 4000313, 1, 2, 0, 100000)
ON DUPLICATE KEY UPDATE chance = chance;
