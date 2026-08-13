-- Boss专属掉落: 186版移植Boss (2026-07-30)
-- 按Boss等级自动生成初始掉落，可后续手动调整

INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 0700003, 4000313, 3, 5, 0, 50000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=0700003 AND itemid=4000313);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 0700003, 2049100, 1, 1, 0, 1000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=0700003 AND itemid=2049100);
-- 金币掉落: 0700003 (Lv60) -> ~60000 meso (via global_drop or map config)
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 1110900, 4000313, 5, 10, 0, 40000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=1110900 AND itemid=4000313);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 1110900, 2049100, 1, 3, 0, 2000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=1110900 AND itemid=2049100);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 1110900, 2340000, 1, 1, 0, 500 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=1110900 AND itemid=2340000);
-- 金币掉落: 1110900 (Lv100) -> ~100000 meso (via global_drop or map config)
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 1110902, 4000313, 5, 10, 0, 40000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=1110902 AND itemid=4000313);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 1110902, 2049100, 1, 3, 0, 2000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=1110902 AND itemid=2049100);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 1110902, 2340000, 1, 1, 0, 500 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=1110902 AND itemid=2340000);
-- 金币掉落: 1110902 (Lv100) -> ~100000 meso (via global_drop or map config)
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 1110932, 4000313, 10, 20, 0, 30000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=1110932 AND itemid=4000313);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 1110932, 2049100, 3, 5, 0, 3000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=1110932 AND itemid=2049100);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 1110932, 2340000, 1, 3, 0, 1000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=1110932 AND itemid=2340000);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 1110932, 2049000, 1, 1, 0, 300 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=1110932 AND itemid=2049000);
-- 金币掉落: 1110932 (Lv150) -> ~150000 meso (via global_drop or map config)
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 1110933, 4000313, 10, 20, 0, 30000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=1110933 AND itemid=4000313);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 1110933, 2049100, 3, 5, 0, 3000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=1110933 AND itemid=2049100);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 1110933, 2340000, 1, 3, 0, 1000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=1110933 AND itemid=2340000);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 1110933, 2049000, 1, 1, 0, 300 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=1110933 AND itemid=2049000);
-- 金币掉落: 1110933 (Lv150) -> ~150000 meso (via global_drop or map config)
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 1110950, 4000313, 5, 10, 0, 40000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=1110950 AND itemid=4000313);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 1110950, 2049100, 1, 3, 0, 2000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=1110950 AND itemid=2049100);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 1110950, 2340000, 1, 1, 0, 500 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=1110950 AND itemid=2340000);
-- 金币掉落: 1110950 (Lv100) -> ~100000 meso (via global_drop or map config)
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 2400040, 4000313, 20, 50, 0, 20000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=2400040 AND itemid=4000313);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 2400040, 2049100, 5, 10, 0, 5000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=2400040 AND itemid=2049100);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 2400040, 2340000, 3, 5, 0, 2000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=2400040 AND itemid=2340000);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 2400040, 2049000, 1, 3, 0, 500 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=2400040 AND itemid=2049000);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 2400040, 2070007, 1, 1, 0, 200 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=2400040 AND itemid=2070007);
-- 金币掉落: 2400040 (Lv200) -> ~200000 meso (via global_drop or map config)
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 2400048, 4000313, 20, 50, 0, 20000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=2400048 AND itemid=4000313);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 2400048, 2049100, 5, 10, 0, 5000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=2400048 AND itemid=2049100);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 2400048, 2340000, 3, 5, 0, 2000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=2400048 AND itemid=2340000);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 2400048, 2049000, 1, 3, 0, 500 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=2400048 AND itemid=2049000);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 2400048, 2070007, 1, 1, 0, 200 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=2400048 AND itemid=2070007);
-- 金币掉落: 2400048 (Lv200) -> ~200000 meso (via global_drop or map config)
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 2400049, 4000313, 20, 50, 0, 20000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=2400049 AND itemid=4000313);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 2400049, 2049100, 5, 10, 0, 5000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=2400049 AND itemid=2049100);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 2400049, 2340000, 3, 5, 0, 2000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=2400049 AND itemid=2340000);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 2400049, 2049000, 1, 3, 0, 500 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=2400049 AND itemid=2049000);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 2400049, 2070007, 1, 1, 0, 200 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=2400049 AND itemid=2070007);
-- 金币掉落: 2400049 (Lv200) -> ~200000 meso (via global_drop or map config)
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 2400208, 4000313, 10, 20, 0, 30000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=2400208 AND itemid=4000313);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 2400208, 2049100, 3, 5, 0, 3000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=2400208 AND itemid=2049100);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 2400208, 2340000, 1, 3, 0, 1000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=2400208 AND itemid=2340000);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 2400208, 2049000, 1, 1, 0, 300 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=2400208 AND itemid=2049000);
-- 金币掉落: 2400208 (Lv190) -> ~190000 meso (via global_drop or map config)
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 2400209, 4000313, 10, 20, 0, 30000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=2400209 AND itemid=4000313);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 2400209, 2049100, 3, 5, 0, 3000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=2400209 AND itemid=2049100);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 2400209, 2340000, 1, 3, 0, 1000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=2400209 AND itemid=2340000);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 2400209, 2049000, 1, 1, 0, 300 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=2400209 AND itemid=2049000);
-- 金币掉落: 2400209 (Lv190) -> ~190000 meso (via global_drop or map config)
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 2400210, 4000313, 10, 20, 0, 30000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=2400210 AND itemid=4000313);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 2400210, 2049100, 3, 5, 0, 3000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=2400210 AND itemid=2049100);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 2400210, 2340000, 1, 3, 0, 1000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=2400210 AND itemid=2340000);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 2400210, 2049000, 1, 1, 0, 300 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=2400210 AND itemid=2049000);
-- 金币掉落: 2400210 (Lv190) -> ~190000 meso (via global_drop or map config)
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 2400211, 4000313, 10, 20, 0, 30000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=2400211 AND itemid=4000313);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 2400211, 2049100, 3, 5, 0, 3000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=2400211 AND itemid=2049100);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 2400211, 2340000, 1, 3, 0, 1000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=2400211 AND itemid=2340000);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 2400211, 2049000, 1, 1, 0, 300 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=2400211 AND itemid=2049000);
-- 金币掉落: 2400211 (Lv190) -> ~190000 meso (via global_drop or map config)
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 2400212, 4000313, 10, 20, 0, 30000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=2400212 AND itemid=4000313);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 2400212, 2049100, 3, 5, 0, 3000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=2400212 AND itemid=2049100);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 2400212, 2340000, 1, 3, 0, 1000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=2400212 AND itemid=2340000);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 2400212, 2049000, 1, 1, 0, 300 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=2400212 AND itemid=2049000);
-- 金币掉落: 2400212 (Lv190) -> ~190000 meso (via global_drop or map config)
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 2400259, 4000313, 5, 10, 0, 40000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=2400259 AND itemid=4000313);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 2400259, 2049100, 1, 3, 0, 2000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=2400259 AND itemid=2049100);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 2400259, 2340000, 1, 1, 0, 500 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=2400259 AND itemid=2340000);
-- 金币掉落: 2400259 (Lv100) -> ~100000 meso (via global_drop or map config)
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 2400263, 4000313, 20, 50, 0, 20000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=2400263 AND itemid=4000313);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 2400263, 2049100, 5, 10, 0, 5000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=2400263 AND itemid=2049100);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 2400263, 2340000, 3, 5, 0, 2000 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=2400263 AND itemid=2340000);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 2400263, 2049000, 1, 3, 0, 500 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=2400263 AND itemid=2049000);
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) SELECT 2400263, 2070007, 1, 1, 0, 200 FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM drop_data WHERE dropperid=2400263 AND itemid=2070007);
-- 金币掉落: 2400263 (Lv200) -> ~200000 meso (via global_drop or map config)