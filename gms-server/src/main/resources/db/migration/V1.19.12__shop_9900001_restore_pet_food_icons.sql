-- 拍卖快捷商店 9900001：保留宠物食品/美味饲料/新年贺卡/测谎仪。
-- 客户端 Consume/0212|0216|0219 图标已从完整 Item.wz 节点合并恢复；此处确保货架行存在。
DELETE FROM shopitems
WHERE shopid = 9900001
  AND itemid IN (2120000, 2120008, 2160101, 2190000);

INSERT INTO shopitems (shopid, itemid, price, pitch, position)
VALUES (9900001, 2120000, 30, 0, 109),
       (9900001, 2120008, 200, 0, 110),
       (9900001, 2160101, 200, 0, 111),
       (9900001, 2190000, 200, 0, 112);
