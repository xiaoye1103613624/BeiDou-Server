-- 成长戒指材料：确保野外Boss特有掉落（供十字旅团戒指进阶）
-- chance 基数 1,000,000；ON DUPLICATE KEY 不覆盖已有更高概率

-- 红蜗牛王：彩虹色蜗牛壳儿
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(2220000, 2210006, 1, 1, 0, 999999)
ON DUPLICATE KEY UPDATE chance = GREATEST(chance, VALUES(chance));

-- 蘑菇王：蘑菇王芽孢
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(6130101, 4000040, 1, 1, 0, 800000)
ON DUPLICATE KEY UPDATE chance = GREATEST(chance, VALUES(chance));

-- 僵尸蘑菇王：毒菇
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(6300005, 4000176, 1, 1, 0, 800000)
ON DUPLICATE KEY UPDATE chance = GREATEST(chance, VALUES(chance));

-- 树妖王：苗木
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(3220000, 4000195, 1, 1, 0, 800000)
ON DUPLICATE KEY UPDATE chance = GREATEST(chance, VALUES(chance));

-- 仙人掌王：仙人球
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(3220001, 4000329, 1, 1, 0, 800000)
ON DUPLICATE KEY UPDATE chance = GREATEST(chance, VALUES(chance));

-- 歇尔夫（4220000/4220001）：歇尔夫的珍珠
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(4220000, 4032474, 1, 1, 0, 500000),
(4220001, 4032474, 1, 1, 0, 500000)
ON DUPLICATE KEY UPDATE chance = GREATEST(chance, VALUES(chance));

-- 浮士德：香蕉（Boss高掉）
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(5220002, 4000029, 1, 1, 0, 800000)
ON DUPLICATE KEY UPDATE chance = GREATEST(chance, VALUES(chance));

-- 提莫：提莫的蛋
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(5220003, 4031991, 1, 1, 0, 500000)
ON DUPLICATE KEY UPDATE chance = GREATEST(chance, VALUES(chance));

-- 多尔：鳄鱼皮
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(6220000, 4000032, 1, 1, 0, 800000)
ON DUPLICATE KEY UPDATE chance = GREATEST(chance, VALUES(chance));

-- 泽诺：太空食品
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(6220001, 4000117, 1, 1, 0, 800000)
ON DUPLICATE KEY UPDATE chance = GREATEST(chance, VALUES(chance));

-- 肯德熊：熊掌
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(7220000, 4000283, 1, 1, 0, 800000)
ON DUPLICATE KEY UPDATE chance = GREATEST(chance, VALUES(chance));

-- 九尾狐：九尾狐的尾巴
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(7220001, 4031793, 1, 1, 0, 500000)
ON DUPLICATE KEY UPDATE chance = GREATEST(chance, VALUES(chance));

-- 妖怪禅师：解毒珠子
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(7220002, 4031789, 1, 1, 0, 500000)
ON DUPLICATE KEY UPDATE chance = GREATEST(chance, VALUES(chance));

-- 艾利杰：独角狮硬角
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(8220000, 4000073, 1, 1, 0, 800000)
ON DUPLICATE KEY UPDATE chance = GREATEST(chance, VALUES(chance));

-- 吉米拉：长颈瓶
INSERT INTO drop_data (dropperid, itemid, minimum_quantity, maximum_quantity, questid, chance) VALUES
(8220002, 4000356, 1, 1, 0, 800000)
ON DUPLICATE KEY UPDATE chance = GREATEST(chance, VALUES(chance));
