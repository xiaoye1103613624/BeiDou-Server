-- 阿尔泰皮肤回滚：点装武器 1708900-1709999 → 帽子 Cap 1008900-1009999（反向 V1.11.39）
-- 清理 Weapon 段孤儿/重复行，保留 Cap 段商品；背包 itemid 迁回 100xxxx
-- 幂等：已落在 Cap 区间的行不再改动；Weapon 段重复行直接删除

SET @db := DATABASE();
SET FOREIGN_KEY_CHECKS = 0;

-- ---------- 1. 背包 / 装备实例：170xxxx → 100xxxx ----------
UPDATE `inventoryitems`
SET `itemid` = `itemid` - 700000
WHERE `itemid` BETWEEN 1708900 AND 1709999;

-- ---------- 2. 窗口商城：先删 category_item 中 Weapon 段重复（Cap 段已存在） ----------
DELETE ci
FROM `xy_cashshop_category_item` ci
WHERE ci.`item_id` BETWEEN 1708900 AND 1709999
  AND EXISTS (
    SELECT 1
    FROM `xy_cashshop_category_item` cap
    WHERE cap.`category_id` = ci.`category_id`
      AND cap.`item_id` = ci.`item_id` - 700000
);

UPDATE `xy_cashshop_category_item`
SET `item_id` = `item_id` - 700000
WHERE `item_id` BETWEEN 1708900 AND 1709999;

-- 仍残留的 Weapon 段 category_item（Cap 商品行缺失时上面 UPDATE 已处理；此处删孤儿）
DELETE ci
FROM `xy_cashshop_category_item` ci
WHERE ci.`item_id` BETWEEN 1708900 AND 1709999;

-- ---------- 3. xy_cashshop_item：删 Weapon 段重复，余下迁回 Cap ----------
DELETE w
FROM `xy_cashshop_item` w
WHERE w.`item_id` BETWEEN 1708900 AND 1709999
  AND EXISTS (
    SELECT 1 FROM `xy_cashshop_item` c WHERE c.`item_id` = w.`item_id` - 700000
);

UPDATE `xy_cashshop_item`
SET `item_id` = `item_id` - 700000,
    `price`   = 100000,
    `enabled` = 1
WHERE `item_id` BETWEEN 1708900 AND 1709999;

DELETE FROM `xy_cashshop_item`
WHERE `item_id` BETWEEN 1708900 AND 1709999;

-- ---------- 4. 经典商城 modified_cash_item ----------
UPDATE `modified_cash_item`
SET `item_id` = `item_id` - 700000
WHERE `item_id` BETWEEN 1708900 AND 1709999;

DELETE FROM `modified_cash_item`
WHERE `item_id` BETWEEN 1708900 AND 1709999;

-- ---------- 5. 补链 Cap 商品到「皮肤」分类（legacy_tab=9） ----------
INSERT INTO `xy_cashshop_category_item` (`category_id`, `item_id`, `sort`, `enabled`)
SELECT c.`id`, i.`item_id`, i.`item_id` - 1008900, 1
FROM `xy_cashshop_category` c
         CROSS JOIN `xy_cashshop_item` i
WHERE c.`legacy_tab` = 9
  AND c.`legacy_category` = 0
  AND i.`item_id` BETWEEN 1008900 AND 1009999
  AND NOT EXISTS (
    SELECT 1
    FROM `xy_cashshop_category_item` ci
    WHERE ci.`category_id` = c.`id`
      AND ci.`item_id` = i.`item_id`
);

-- 缺失 Cap 商品行时插入（seedDefaults 亦会补，此处保证迁移后即有）
INSERT INTO `xy_cashshop_item` (`item_id`, `price`, `count`, `period`, `gender`, `name`, `enabled`, `remark`)
SELECT cap.`item_id`, 100000, 1, 0, 2, CONCAT('阿尔泰皮肤 #', cap.`item_id`), 1, 'altair-cap-skin'
FROM (
    SELECT `itemid` AS `item_id`
    FROM `inventoryitems`
    WHERE `itemid` BETWEEN 1008900 AND 1009999
    UNION
    SELECT `item_id`
    FROM `xy_cashshop_category_item`
    WHERE `item_id` BETWEEN 1008900 AND 1009999
) cap
WHERE NOT EXISTS (
    SELECT 1 FROM `xy_cashshop_item` i WHERE i.`item_id` = cap.`item_id`
);

SET FOREIGN_KEY_CHECKS = 1;
