-- Boss装备散件掉落 (2026-07-30)
-- 按Boss等级投放对应阶段的套装散件
-- 使用ON DUPLICATE KEY UPDATE chance = chance（追加模式，不覆盖已有掉落）

-- ============================================================
-- 一、普通挑战Boss → 枫叶/铂金系列装备
-- ============================================================

-- 地鼠王(3501008)：枫叶武器+套服
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(3501008, 1052165, 1, 1, 0, 15000),
(3501008, 1052166, 1, 1, 0, 10000),
(3501008, 1302030, 1, 1, 0, 15000)
ON DUPLICATE KEY UPDATE chance = chance;

-- 盖奥勒克(3502008)：青涩枫叶+宝石武器
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(3502008, 1052358, 1, 1, 0, 15000),
(3502008, 1302064, 1, 1, 0, 12000),
(3502008, 1312032, 1, 1, 0, 12000),
(3502008, 1322054, 1, 1, 0, 12000),
(3502008, 1112600, 1, 1, 0, 30000)
ON DUPLICATE KEY UPDATE chance = chance;

-- 飞船(8240046)：铂金武器+套服
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(8240046, 1052457, 1, 1, 0, 12000),
(8240046, 1112602, 1, 1, 0, 25000),
(8240046, 1032030, 1, 1, 0, 20000)
ON DUPLICATE KEY UPDATE chance = chance;

-- 贝尔加莫特(7220003)：紫金枫叶+传说套服
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(7220003, 1052405, 1, 1, 0, 12000),
(7220003, 1052461, 1, 1, 0, 10000),
(7220003, 1112603, 1, 1, 0, 25000),
(7220003, 1102046, 1, 1, 0, 20000)
ON DUPLICATE KEY UPDATE chance = chance;

-- 钻机(9600086)：革命武器系列
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(9600086, 1082472, 1, 1, 0, 15000),
(9600086, 1082473, 1, 1, 0, 15000),
(9600086, 1082474, 1, 1, 0, 15000),
(9600086, 1052612, 1, 1, 0, 10000),
(9600086, 1112605, 1, 1, 0, 20000)
ON DUPLICATE KEY UPDATE chance = chance;

-- 斯乌(8240098/8240105)：寻宝武器+套服+11周年
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(8240098, 1052758, 1, 1, 0, 10000),
(8240098, 1052467, 1, 1, 0, 8000),
(8240098, 1112607, 1, 1, 0, 15000),
(8240098, 1072485, 1, 1, 0, 15000),
(8240105, 1052758, 1, 1, 0, 12000),
(8240105, 1052569, 1, 1, 0, 8000),
(8240105, 1112608, 1, 1, 0, 15000),
(8240105, 1072486, 1, 1, 0, 15000)
ON DUPLICATE KEY UPDATE chance = chance;

-- ============================================================
-- 二、团队挑战Boss → 雷昂/血色/天照/埃苏碎片
-- ============================================================

-- 希拉(8870000)：皇家班雷昂套装+老兵戒指
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(8870000, 1052804, 1, 1, 0, 8000),
(8870000, 1052805, 1, 1, 0, 8000),
(8870000, 1052806, 1, 1, 0, 8000),
(8870000, 1052807, 1, 1, 0, 8000),
(8870000, 1052808, 1, 1, 0, 8000),
(8870000, 1112606, 1, 1, 0, 20000),
(8870000, 1082475, 1, 1, 0, 15000)
ON DUPLICATE KEY UPDATE chance = chance;

-- 麦格纳斯(8880000)：芬撒里尔+血色套装+暴君手套
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(8880000, 1052799, 1, 1, 0, 8000),
(8880000, 1052800, 1, 1, 0, 8000),
(8880000, 1052801, 1, 1, 0, 8000),
(8880000, 1052802, 1, 1, 0, 8000),
(8880000, 1052803, 1, 1, 0, 8000),
(8880000, 1052319, 1, 1, 0, 6000),
(8880000, 1052320, 1, 1, 0, 6000),
(8880000, 1052321, 1, 1, 0, 6000),
(8880000, 1052322, 1, 1, 0, 6000),
(8880000, 1052323, 1, 1, 0, 6000)
ON DUPLICATE KEY UPDATE chance = chance;

-- 暴君(8880002)：天照+暴君系列+埃苏碎片
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(8880002, 1052509, 1, 1, 0, 5000),
(8880002, 1052510, 1, 1, 0, 5000),
(8880002, 1052511, 1, 1, 0, 5000),
(8880002, 1052512, 1, 1, 0, 5000),
(8880002, 1052513, 1, 1, 0, 5000),
(8880002, 1112609, 1, 1, 0, 10000)
ON DUPLICATE KEY UPDATE chance = chance;

-- ============================================================
-- 三、四凶 → 埃苏莱布斯套装+英雄戒指
-- ============================================================

INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance)
SELECT mob.dropperid, d.itemid, d.minimum_quantity, d.maximum_quantity, d.questid, d.chance
FROM (
    SELECT 8880830 AS dropperid UNION ALL SELECT 8880831 UNION ALL SELECT 8880832
) mob CROSS JOIN (
    SELECT 1052882 AS itemid, 1 AS minimum_quantity, 1 AS maximum_quantity, 0 AS questid, 3000 AS chance UNION ALL
    SELECT 1052887, 1, 1, 0, 3000 UNION ALL
    SELECT 1052888, 1, 1, 0, 3000 UNION ALL
    SELECT 1052889, 1, 1, 0, 3000 UNION ALL
    SELECT 1052890, 1, 1, 0, 3000 UNION ALL
    SELECT 1112610, 1, 1, 0, 8000 UNION ALL
    SELECT 1112611, 1, 1, 0, 6000
) AS d WHERE 1=1
ON DUPLICATE KEY UPDATE chance = chance;

-- ============================================================
-- 四、进阶挑战Boss → 埃苏+神秘之影
-- ============================================================

-- 希纳斯(8850011)：埃苏+凯梅尔兹+勇士戒指
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(8850011, 1052673, 1, 1, 0, 5000),
(8850011, 1112612, 1, 1, 0, 8000),
(8850011, 1032070, 1, 1, 0, 10000)
ON DUPLICATE KEY UPDATE chance = chance;

-- 进阶扎昆(8800102)：扎昆进阶饰品+英雄戒指
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(8800102, 1112609, 1, 1, 0, 10000),
(8800102, 1032070, 1, 1, 0, 12000)
ON DUPLICATE KEY UPDATE chance = chance;

-- 鲁塔比斯四皇混沌版(8910000/8900000/8920000/8930000)：神秘之影套装+降魔戒指
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance)
SELECT mob.dropperid, d.itemid, d.minimum_quantity, d.maximum_quantity, d.questid, d.chance
FROM (
    SELECT 8910000 AS dropperid UNION ALL SELECT 8900000 UNION ALL SELECT 8920000 UNION ALL SELECT 8930000
) mob CROSS JOIN (
    SELECT 1053063 AS itemid, 1 AS minimum_quantity, 1 AS maximum_quantity, 0 AS questid, 1500 AS chance UNION ALL
    SELECT 1053064, 1, 1, 0, 1500 UNION ALL
    SELECT 1053065, 1, 1, 0, 1500 UNION ALL
    SELECT 1053066, 1, 1, 0, 1500 UNION ALL
    SELECT 1053067, 1, 1, 0, 1500 UNION ALL
    SELECT 1112613, 1, 1, 0, 5000
) AS d WHERE 1=1
ON DUPLICATE KEY UPDATE chance = chance;

-- ============================================================
-- 五、高级Boss → 各阶段对应装备
-- ============================================================

-- T5(8900000/8900002/8900101 皮埃尔三色)：Legend/专属紫金
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance)
SELECT mob.dropperid, d.itemid, d.minimum_quantity, d.maximum_quantity, d.questid, d.chance
FROM (
    SELECT 8900000 AS dropperid UNION ALL SELECT 8900002 UNION ALL SELECT 8900101
) mob CROSS JOIN (
    SELECT 1052461 AS itemid, 1 AS minimum_quantity, 1 AS maximum_quantity, 0 AS questid, 10000 AS chance UNION ALL
    SELECT 1053193, 1, 1, 0, 8000 UNION ALL
    SELECT 1112603, 1, 1, 0, 20000
) AS d WHERE 1=1
ON DUPLICATE KEY UPDATE chance = chance;

-- T4(9700043/8220109/9601505 火狐/恶狼/剑圣)：11周年+风暴
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance)
SELECT mob.dropperid, d.itemid, d.minimum_quantity, d.maximum_quantity, d.questid, d.chance
FROM (
    SELECT 9700043 AS dropperid UNION ALL SELECT 8220109 UNION ALL SELECT 9601505
) mob CROSS JOIN (
    SELECT 1052758 AS itemid, 1 AS minimum_quantity, 1 AS maximum_quantity, 0 AS questid, 10000 AS chance UNION ALL
    SELECT 1052467, 1, 1, 0, 8000 UNION ALL
    SELECT 1112604, 1, 1, 0, 20000
) AS d WHERE 1=1
ON DUPLICATE KEY UPDATE chance = chance;

-- T3(9601013/9601014/9601015 黄龙/赤虎/蛇)：寻宝+终极
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance)
SELECT mob.dropperid, d.itemid, d.minimum_quantity, d.maximum_quantity, d.questid, d.chance
FROM (
    SELECT 9601013 AS dropperid UNION ALL SELECT 9601014 UNION ALL SELECT 9601015
) mob CROSS JOIN (
    SELECT 1052929 AS itemid, 1 AS minimum_quantity, 1 AS maximum_quantity, 0 AS questid, 8000 AS chance UNION ALL
    SELECT 1052569, 1, 1, 0, 6000 UNION ALL
    SELECT 1112605, 1, 1, 0, 15000
) AS d WHERE 1=1
ON DUPLICATE KEY UPDATE chance = chance;

-- T2(8850013/8880000/8645009 森兰丸/麦格纳斯/敦凯尔)：雷昂+血色
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance)
SELECT mob.dropperid, d.itemid, d.minimum_quantity, d.maximum_quantity, d.questid, d.chance
FROM (
    SELECT 8850013 AS dropperid UNION ALL SELECT 8880000 UNION ALL SELECT 8645009
) mob CROSS JOIN (
    SELECT 1052804 AS itemid, 1 AS minimum_quantity, 1 AS maximum_quantity, 0 AS questid, 6000 AS chance UNION ALL
    SELECT 1052805, 1, 1, 0, 6000 UNION ALL
    SELECT 1052806, 1, 1, 0, 6000 UNION ALL
    SELECT 1052807, 1, 1, 0, 6000 UNION ALL
    SELECT 1052808, 1, 1, 0, 6000 UNION ALL
    SELECT 1052319, 1, 1, 0, 4000 UNION ALL
    SELECT 1052320, 1, 1, 0, 4000 UNION ALL
    SELECT 1112607, 1, 1, 0, 12000
) AS d WHERE 1=1
ON DUPLICATE KEY UPDATE chance = chance;

-- T1(8880500/8880501/8880502 光明/黑暗/黑魔法师)：天照+埃苏
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance)
SELECT mob.dropperid, d.itemid, d.minimum_quantity, d.maximum_quantity, d.questid, d.chance
FROM (
    SELECT 8880500 AS dropperid UNION ALL SELECT 8880501 UNION ALL SELECT 8880502
) mob CROSS JOIN (
    SELECT 1052509 AS itemid, 1 AS minimum_quantity, 1 AS maximum_quantity, 0 AS questid, 5000 AS chance UNION ALL
    SELECT 1052510, 1, 1, 0, 5000 UNION ALL
    SELECT 1052511, 1, 1, 0, 5000 UNION ALL
    SELECT 1052512, 1, 1, 0, 5000 UNION ALL
    SELECT 1052513, 1, 1, 0, 5000 UNION ALL
    SELECT 1052882, 1, 1, 0, 2000 UNION ALL
    SELECT 1112610, 1, 1, 0, 10000
) AS d WHERE 1=1
ON DUPLICATE KEY UPDATE chance = chance;

-- T0(8880612/8880603/8880609 塞伦三阶)：埃苏+凯梅尔兹+神秘碎片
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance)
SELECT mob.dropperid, d.itemid, d.minimum_quantity, d.maximum_quantity, d.questid, d.chance
FROM (
    SELECT 8880612 AS dropperid UNION ALL SELECT 8880603 UNION ALL SELECT 8880609
) mob CROSS JOIN (
    SELECT 1052882 AS itemid, 1 AS minimum_quantity, 1 AS maximum_quantity, 0 AS questid, 4000 AS chance UNION ALL
    SELECT 1052887, 1, 1, 0, 4000 UNION ALL
    SELECT 1052888, 1, 1, 0, 4000 UNION ALL
    SELECT 1052889, 1, 1, 0, 4000 UNION ALL
    SELECT 1052890, 1, 1, 0, 4000 UNION ALL
    SELECT 1052673, 1, 1, 0, 6000 UNION ALL
    SELECT 1112612, 1, 1, 0, 8000
) AS d WHERE 1=1
ON DUPLICATE KEY UPDATE chance = chance;

-- 神器(9410224/8880803 皇帝/貔貅)：神秘之影+降魔戒指+创世碎片
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance)
SELECT mob.dropperid, d.itemid, d.minimum_quantity, d.maximum_quantity, d.questid, d.chance
FROM (
    SELECT 9410224 AS dropperid UNION ALL SELECT 8880803
) mob CROSS JOIN (
    SELECT 1053063 AS itemid, 1 AS minimum_quantity, 1 AS maximum_quantity, 0 AS questid, 1500 AS chance UNION ALL
    SELECT 1053064, 1, 1, 0, 1500 UNION ALL
    SELECT 1053065, 1, 1, 0, 1500 UNION ALL
    SELECT 1053066, 1, 1, 0, 1500 UNION ALL
    SELECT 1053067, 1, 1, 0, 1500 UNION ALL
    SELECT 1112613, 1, 1, 0, 4000
) AS d WHERE 1=1
ON DUPLICATE KEY UPDATE chance = chance;

-- ============================================================
-- 六、083原生Boss补充装备掉落
-- ============================================================

-- 扎昆(8800002)：扎昆头盔为通关奖励(已配)，追加散件
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(8800002, 1002357, 1, 1, 0, 50000),
(8800002, 1112601, 1, 1, 0, 30000)
ON DUPLICATE KEY UPDATE chance = chance;

-- 黑龙(8810018)：黑龙项链/吊坠
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(8810018, 1122007, 1, 1, 0, 50000),
(8810018, 1112602, 1, 1, 0, 30000)
ON DUPLICATE KEY UPDATE chance = chance;

-- 品克缤(8820001)：品克缤腰带/吊坠
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(8820001, 1132004, 1, 1, 0, 50000),
(8820001, 1122000, 1, 1, 0, 50000),
(8820001, 1112603, 1, 1, 0, 30000)
ON DUPLICATE KEY UPDATE chance = chance;
