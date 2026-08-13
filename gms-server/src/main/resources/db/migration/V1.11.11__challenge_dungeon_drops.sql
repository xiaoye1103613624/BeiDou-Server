-- 挑战副本分层掉落
-- chance 基数 1,000,000（50000=5%, 100000=10%, 150000=15%, 200000=20%）
-- 通用石 4032169 / 4032171 单次最多 4 个

-- ========== 普通挑战副本 ==========
-- 档位1 地鼠王
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(3501008, 4032169, 1, 4, 0, 50000),
(3501008, 4032171, 1, 4, 0, 50000),
(3501008, 4441300, 1, 1, 0, 50000),
(3501008, 4443300, 1, 1, 0, 50000)
ON DUPLICATE KEY UPDATE minimum_quantity=VALUES(minimum_quantity), maximum_quantity=VALUES(maximum_quantity), chance=VALUES(chance);

-- 档位2 盖奥勒克
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(3502008, 4032169, 1, 4, 0, 50000),
(3502008, 4032171, 1, 4, 0, 50000),
(3502008, 4441300, 1, 1, 0, 50000),
(3502008, 4443300, 1, 1, 0, 50000)
ON DUPLICATE KEY UPDATE minimum_quantity=VALUES(minimum_quantity), maximum_quantity=VALUES(maximum_quantity), chance=VALUES(chance);

-- 档位3 黑色之翼飞船
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(8240046, 4032169, 1, 4, 0, 100000),
(8240046, 4032171, 1, 4, 0, 100000),
(8240046, 4441300, 1, 1, 0, 100000),
(8240046, 4443300, 1, 1, 0, 100000),
(8240046, 1082472, 1, 1, 0, 50000),
(8240046, 1082473, 1, 1, 0, 50000),
(8240046, 1082474, 1, 1, 0, 50000),
(8240046, 1082475, 1, 1, 0, 50000)
ON DUPLICATE KEY UPDATE minimum_quantity=VALUES(minimum_quantity), maximum_quantity=VALUES(maximum_quantity), chance=VALUES(chance);

-- 档位4 贝尔加莫特
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(7220003, 4032169, 1, 4, 0, 100000),
(7220003, 4032171, 1, 4, 0, 100000),
(7220003, 4441300, 1, 1, 0, 100000),
(7220003, 4443300, 1, 1, 0, 150000),
(7220003, 2330007, 1, 1, 0, 50000)
ON DUPLICATE KEY UPDATE minimum_quantity=VALUES(minimum_quantity), maximum_quantity=VALUES(maximum_quantity), chance=VALUES(chance);

-- 档位5 钻机
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(9600086, 4032169, 1, 4, 0, 200000),
(9600086, 4032171, 1, 4, 0, 200000),
(9600086, 4441300, 1, 1, 0, 150000),
(9600086, 4443300, 1, 1, 0, 200000),
(9600086, 4033255, 1, 1, 0, 200000)
ON DUPLICATE KEY UPDATE minimum_quantity=VALUES(minimum_quantity), maximum_quantity=VALUES(maximum_quantity), chance=VALUES(chance);

-- 档位6 斯乌全系
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(8240097, 4032169, 1, 4, 0, 200000),
(8240097, 4032171, 1, 4, 0, 200000),
(8240097, 4441300, 1, 1, 0, 200000),
(8240097, 4443300, 1, 1, 0, 200000),
(8240098, 4032169, 1, 4, 0, 200000),
(8240098, 4032171, 1, 4, 0, 200000),
(8240098, 4441300, 1, 1, 0, 200000),
(8240098, 4443300, 1, 1, 0, 200000),
(8240099, 4032169, 1, 4, 0, 200000),
(8240099, 4032171, 1, 4, 0, 200000),
(8240099, 4441300, 1, 1, 0, 200000),
(8240099, 4443300, 1, 1, 0, 200000),
(8240104, 4032169, 1, 4, 0, 200000),
(8240104, 4032171, 1, 4, 0, 200000),
(8240104, 4441300, 1, 1, 0, 200000),
(8240104, 4443300, 1, 1, 0, 200000),
(8240105, 4032169, 1, 4, 0, 200000),
(8240105, 4032171, 1, 4, 0, 200000),
(8240105, 4441300, 1, 1, 0, 200000),
(8240105, 4443300, 1, 1, 0, 200000),
(8240107, 4032169, 1, 4, 0, 200000),
(8240107, 4032171, 1, 4, 0, 200000),
(8240107, 4441300, 1, 1, 0, 200000),
(8240107, 4443300, 1, 1, 0, 200000),
(8240108, 4032169, 1, 4, 0, 200000),
(8240108, 4032171, 1, 4, 0, 200000),
(8240108, 4441300, 1, 1, 0, 200000),
(8240108, 4443300, 1, 1, 0, 200000),
(8240109, 4032169, 1, 4, 0, 200000),
(8240109, 4032171, 1, 4, 0, 200000),
(8240109, 4441300, 1, 1, 0, 200000),
(8240109, 4443300, 1, 1, 0, 200000)
ON DUPLICATE KEY UPDATE minimum_quantity=VALUES(minimum_quantity), maximum_quantity=VALUES(maximum_quantity), chance=VALUES(chance);

-- ========== 进阶挑战副本 ==========
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(8910100, 4032169, 1, 4, 0, 50000),
(8910100, 4032171, 1, 4, 0, 50000),
(8910100, 4310064, 1, 1, 0, 150000),
(8900100, 4032169, 1, 4, 0, 50000),
(8900100, 4032171, 1, 4, 0, 50000),
(8900100, 4310064, 1, 1, 0, 150000),
(8920100, 4032169, 1, 4, 0, 100000),
(8920100, 4032171, 1, 4, 0, 100000),
(8920100, 4310064, 1, 1, 0, 150000),
(8930100, 4032169, 1, 4, 0, 100000),
(8930100, 4032171, 1, 4, 0, 100000),
(8930100, 4310064, 1, 1, 0, 150000),
(8850011, 4032169, 1, 4, 0, 200000),
(8850011, 4032171, 1, 4, 0, 200000),
(8850011, 2028154, 1, 1, 0, 80000),
(8800102, 4032169, 1, 4, 0, 200000),
(8800102, 4032171, 1, 4, 0, 200000),
(8800102, 1002357, 1, 1, 0, 50000)
ON DUPLICATE KEY UPDATE minimum_quantity=VALUES(minimum_quantity), maximum_quantity=VALUES(maximum_quantity), chance=VALUES(chance);

-- 混沌四皇
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(8910000, 4310065, 1, 1, 0, 150000),
(8910000, 1142902, 1, 1, 0, 30000),
(8900000, 4310065, 1, 1, 0, 150000),
(8900000, 1142903, 1, 1, 0, 30000),
(8920000, 4310065, 1, 1, 0, 150000),
(8920000, 1142904, 1, 1, 0, 30000),
(8930000, 4310065, 1, 1, 0, 150000)
ON DUPLICATE KEY UPDATE minimum_quantity=VALUES(minimum_quantity), maximum_quantity=VALUES(maximum_quantity), chance=VALUES(chance);

-- ========== 团队副本 ==========
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(8870000, 4032169, 1, 4, 0, 50000),
(8870000, 4032171, 1, 4, 0, 50000),
(8870000, 4442101, 1, 1, 0, 50000),
(8880000, 4032169, 1, 4, 0, 50000),
(8880000, 4032171, 1, 4, 0, 50000),
(8880000, 4441101, 1, 1, 0, 50000),
(8880002, 4032169, 1, 4, 0, 100000),
(8880002, 4032171, 1, 4, 0, 100000),
(8880002, 1082543, 1, 1, 0, 30000),
(8880830, 4032169, 1, 4, 0, 100000),
(8880830, 4032171, 1, 4, 0, 100000),
(8880830, 4440001, 1, 1, 0, 30000),
(8880831, 4032169, 1, 4, 0, 200000),
(8880831, 4032171, 1, 4, 0, 200000),
(8880831, 4442001, 1, 1, 0, 30000),
(8880832, 4032169, 1, 4, 0, 200000),
(8880832, 4032171, 1, 4, 0, 200000),
(8880832, 4443001, 1, 1, 0, 30000)
ON DUPLICATE KEY UPDATE minimum_quantity=VALUES(minimum_quantity), maximum_quantity=VALUES(maximum_quantity), chance=VALUES(chance);

-- ========== 远征 · 巨型蝙蝠怪（70~100 升级） ==========
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(8830003, 2340000, 1, 1, 0, 100000),
(8830003, 4001261, 1, 2, 0, 500000),
(8830003, 1072375, 1, 1, 0, 80000),
(8830007, 2340000, 1, 1, 0, 100000),
(8830007, 4001261, 1, 2, 0, 500000),
(8830007, 1072375, 1, 1, 0, 80000)
ON DUPLICATE KEY UPDATE minimum_quantity=VALUES(minimum_quantity), maximum_quantity=VALUES(maximum_quantity), chance=VALUES(chance);
