-- 挑战副本 Boss 官方掉落补全
-- 说明：
-- 1) 与 V1.11.11 定制掉落并存；已存在的 (dropperid,itemid) 不会被覆盖
-- 2) chance 基数 1,000,000（50000=5%）
-- 3) 部分 Boss 从同系列已有官方掉落继承（如 7220002 → 7220003，8800002 → 8800102）

-- ========== 贝尔加莫特：继承龙神副本同系列 7220002 官方池 ==========
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance)
SELECT 7220003, itemid, minimum_quantity, maximum_quantity, questid, chance
FROM drop_data
WHERE dropperid = 7220002
ON DUPLICATE KEY UPDATE chance = chance;

INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(7220003, 4032515, 1, 1, 0, 80000),
(7220003, 4032157, 1, 1, 0, 50000),
(7220003, 2388048, 1, 1, 0, 25000),
(7220003, 2000005, 1, 5, 0, 300000)
ON DUPLICATE KEY UPDATE chance = chance;

-- ========== 进阶扎昆：继承 083 扎昆本体 8800002 官方池 ==========
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance)
SELECT 8800102, itemid, minimum_quantity, maximum_quantity, questid, chance
FROM drop_data
WHERE dropperid = 8800002
ON DUPLICATE KEY UPDATE chance = chance;

-- ========== 地鼠王 / 盖奥勒克 ==========
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(3501008, 4000998, 1, 3, 0, 150000),
(3501008, 2000005, 1, 3, 0, 250000),
(3501008, 0, 3000, 8000, 0, 400000),
(3502008, 4034293, 1, 1, 0, 120000),
(3502008, 2000005, 1, 3, 0, 250000),
(3502008, 0, 5000, 12000, 0, 400000)
ON DUPLICATE KEY UPDATE chance = chance;

-- ========== 黑色之翼飞船 ==========
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(8240046, 4032745, 1, 1, 0, 80000),
(8240046, 4033098, 1, 1, 0, 100000),
(8240046, 4009324, 1, 1, 0, 50000),
(8240046, 4034231, 1, 1, 0, 100000),
(8240046, 4032773, 1, 1, 0, 80000),
(8240046, 4000617, 1, 2, 0, 120000),
(8240046, 2000005, 1, 5, 0, 300000),
(8240046, 0, 8000, 20000, 0, 500000)
ON DUPLICATE KEY UPDATE chance = chance;

-- ========== 钻机 ==========
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(9600086, 4001842, 1, 2, 0, 150000),
(9600086, 4032773, 1, 1, 0, 100000),
(9600086, 4034231, 1, 1, 0, 100000),
(9600086, 4000617, 1, 3, 0, 120000),
(9600086, 2000005, 1, 5, 0, 300000),
(9600086, 0, 10000, 30000, 0, 500000)
ON DUPLICATE KEY UPDATE chance = chance;

-- ========== 斯乌全系（黑色天堂能量核心） ==========
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance)
SELECT mob.dropperid, d.itemid, d.minimum_quantity, d.maximum_quantity, d.questid, d.chance
FROM (
    SELECT 8240097 AS dropperid UNION ALL SELECT 8240098 UNION ALL SELECT 8240099 UNION ALL
    SELECT 8240104 UNION ALL SELECT 8240105 UNION ALL SELECT 8240107 UNION ALL
    SELECT 8240108 UNION ALL SELECT 8240109
) mob
CROSS JOIN (
    SELECT 4001842 AS itemid, 1 AS minimum_quantity, 2 AS maximum_quantity, 0 AS questid, 150000 AS chance UNION ALL
    SELECT 4001843, 1, 1, 0, 80000 UNION ALL
    SELECT 4032773, 1, 1, 0, 100000 UNION ALL
    SELECT 4034231, 1, 2, 0, 120000 UNION ALL
    SELECT 4000617, 1, 3, 0, 120000 UNION ALL
    SELECT 2000005, 1, 5, 0, 300000 UNION ALL
    SELECT 0, 15000, 40000, 0, 500000
) d
ON DUPLICATE KEY UPDATE chance = chance;

-- ========== 鲁塔比斯四皇（普通）共享官方池 ==========
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance)
SELECT mob.dropperid, d.itemid, d.minimum_quantity, d.maximum_quantity, d.questid, d.chance
FROM (
    SELECT 8910100 AS dropperid UNION ALL SELECT 8900100 UNION ALL SELECT 8920100 UNION ALL SELECT 8930100
) mob
CROSS JOIN (
    SELECT 2028154 AS itemid, 1 AS minimum_quantity, 1 AS maximum_quantity, 0 AS questid, 10000 AS chance UNION ALL
    SELECT 2028155, 1, 1, 0, 10000 UNION ALL
    SELECT 2028156, 1, 1, 0, 10000 UNION ALL
    SELECT 2028161, 1, 1, 0, 10000 UNION ALL
    SELECT 2028158, 1, 1, 0, 15000 UNION ALL
    SELECT 2028159, 1, 1, 0, 15000 UNION ALL
    SELECT 2028160, 1, 1, 0, 15000 UNION ALL
    SELECT 2023136, 1, 1, 0, 5000 UNION ALL
    SELECT 2003577, 1, 1, 0, 30000 UNION ALL
    SELECT 2003578, 1, 1, 0, 30000 UNION ALL
    SELECT 2000005, 1, 10, 0, 350000 UNION ALL
    SELECT 2049400, 1, 1, 0, 50000 UNION ALL
    SELECT 0, 20000, 50000, 0, 500000
) d
ON DUPLICATE KEY UPDATE chance = chance;

-- ========== 鲁塔比斯四皇（混沌）官方追加 ==========
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(8910000, 2028162, 1, 1, 0, 10000),
(8910000, 2028163, 1, 1, 0, 10000),
(8910000, 2028164, 1, 1, 0, 10000),
(8910000, 2028165, 1, 1, 0, 10000),
(8910000, 2000005, 1, 15, 0, 400000),
(8910000, 2049400, 1, 1, 0, 80000),
(8900000, 2028162, 1, 1, 0, 10000),
(8900000, 2028163, 1, 1, 0, 10000),
(8900000, 2028164, 1, 1, 0, 10000),
(8900000, 2028165, 1, 1, 0, 10000),
(8900000, 2000005, 1, 15, 0, 400000),
(8900000, 2049400, 1, 1, 0, 80000),
(8920000, 2028162, 1, 1, 0, 10000),
(8920000, 2028163, 1, 1, 0, 10000),
(8920000, 2028164, 1, 1, 0, 10000),
(8920000, 2028165, 1, 1, 0, 10000),
(8920000, 2000005, 1, 15, 0, 400000),
(8920000, 2049400, 1, 1, 0, 80000),
(8930000, 2028162, 1, 1, 0, 10000),
(8930000, 2028163, 1, 1, 0, 10000),
(8930000, 2028164, 1, 1, 0, 10000),
(8930000, 2028165, 1, 1, 0, 10000),
(8930000, 2000005, 1, 15, 0, 400000),
(8930000, 2049400, 1, 1, 0, 80000)
ON DUPLICATE KEY UPDATE chance = chance;

-- ========== 希纳斯 ==========
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(8850011, 1142906, 1, 1, 0, 30000),
(8850011, 2028155, 1, 1, 0, 50000),
(8850011, 2028156, 1, 1, 0, 50000),
(8850011, 2028161, 1, 1, 0, 50000),
(8850011, 2000005, 1, 10, 0, 350000),
(8850011, 2049400, 1, 1, 0, 80000),
(8850011, 0, 30000, 80000, 0, 600000)
ON DUPLICATE KEY UPDATE chance = chance;

-- ========== 希拉 ==========
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(8870000, 2000005, 1, 10, 0, 350000),
(8870000, 2049400, 1, 1, 0, 80000),
(8870000, 4021020, 1, 1, 0, 50000),
(8870000, 0, 25000, 60000, 0, 500000)
ON DUPLICATE KEY UPDATE chance = chance;

-- ========== 麦格纳斯 / 暴君 ==========
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(8880000, 4033767, 1, 2, 0, 120000),
(8880000, 4033347, 1, 1, 0, 80000),
(8880000, 2000005, 1, 10, 0, 350000),
(8880000, 2049400, 1, 1, 0, 80000),
(8880000, 0, 30000, 70000, 0, 500000),
(8880002, 4033767, 1, 3, 0, 150000),
(8880002, 4033347, 1, 1, 0, 100000),
(8880002, 2000005, 1, 15, 0, 400000),
(8880002, 2049400, 1, 1, 0, 100000),
(8880002, 4021041, 1, 1, 0, 50000),
(8880002, 0, 40000, 90000, 0, 600000)
ON DUPLICATE KEY UPDATE chance = chance;

-- ========== 四凶 ==========
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(8880830, 4021020, 1, 2, 0, 120000),
(8880830, 4021041, 1, 1, 0, 80000),
(8880830, 2000005, 1, 10, 0, 350000),
(8880830, 0, 35000, 80000, 0, 500000),
(8880831, 4021020, 1, 2, 0, 120000),
(8880831, 4021042, 1, 1, 0, 60000),
(8880831, 2000005, 1, 10, 0, 350000),
(8880831, 0, 35000, 80000, 0, 500000),
(8880832, 4021020, 1, 3, 0, 150000),
(8880832, 4021042, 1, 1, 0, 80000),
(8880832, 4021041, 1, 1, 0, 80000),
(8880832, 2000005, 1, 15, 0, 400000),
(8880832, 0, 50000, 120000, 0, 600000)
ON DUPLICATE KEY UPDATE chance = chance;
