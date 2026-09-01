-- 窗口商城：伤害皮肤分类上架皮肤本体（5920000+skinId），移出入口券 5910000；补阿尔泰 Cap 商品

-- ---------- 伤害皮肤本体：从 catalog 灌入虚拟 SKU ----------
INSERT INTO `xy_cashshop_item` (`item_id`, `price`, `count`, `period`, `gender`, `name`, `enabled`, `remark`)
SELECT 5920000 + d.`skinId`,
       50000,
       1,
       0,
       2,
       CONCAT('伤害皮肤 #', d.`skinId`),
       1,
       CONCAT('damage-skin-body:', d.`skinId`)
FROM `xy_damageskin_catalog` d
WHERE d.`skinId` > 0
  AND NOT EXISTS (SELECT 1 FROM `xy_cashshop_item` i WHERE i.`item_id` = 5920000 + d.`skinId`);

UPDATE `xy_cashshop_item` i
    INNER JOIN `xy_damageskin_catalog` d ON i.`item_id` = 5920000 + d.`skinId`
SET i.`price`   = 50000,
    i.`enabled` = 1,
    i.`name`    = CONCAT('伤害皮肤 #', d.`skinId`),
    i.`remark`  = CONCAT('damage-skin-body:', d.`skinId`)
WHERE d.`skinId` > 0;

INSERT INTO `xy_cashshop_category_item` (`category_id`, `item_id`, `sort`, `enabled`)
SELECT c.`id`, 5920000 + d.`skinId`, d.`skinId`, 1
FROM `xy_cashshop_category` c
         CROSS JOIN `xy_damageskin_catalog` d
WHERE c.`legacy_tab` = 10
  AND c.`legacy_category` = 0
  AND d.`skinId` > 0
  AND NOT EXISTS (SELECT 1
                  FROM `xy_cashshop_category_item` ci
                  WHERE ci.`category_id` = c.`id`
                    AND ci.`item_id` = 5920000 + d.`skinId`);

-- 入口券 5910000 移出「伤害皮肤」分类（商品行可保留供别处出售）
DELETE ci
FROM `xy_cashshop_category_item` ci
         INNER JOIN `xy_cashshop_category` c ON c.`id` = ci.`category_id`
WHERE c.`legacy_tab` = 10
  AND c.`legacy_category` = 0
  AND ci.`item_id` = 5910000;

UPDATE `xy_cashshop_item`
SET `remark` = 'damage-skin-opener',
    `name`   = '伤害皮肤栏'
WHERE `item_id` = 5910000;

-- ---------- 阿尔泰 Cap 1008900-1009999：仅链接已有商品行；缺失由 seedDefaults / 脚本补 ----------
UPDATE `xy_cashshop_item`
SET `price` = 100000, `enabled` = 1
WHERE `item_id` BETWEEN 1008900 AND 1009999;

INSERT INTO `xy_cashshop_category_item` (`category_id`, `item_id`, `sort`, `enabled`)
SELECT c.`id`, i.`item_id`, 0, 1
FROM `xy_cashshop_category` c
         CROSS JOIN `xy_cashshop_item` i
WHERE c.`legacy_tab` = 9
  AND c.`legacy_category` = 0
  AND i.`item_id` BETWEEN 1008900 AND 1009999
  AND NOT EXISTS (SELECT 1
                  FROM `xy_cashshop_category_item` ci
                  WHERE ci.`category_id` = c.`id`
                    AND ci.`item_id` = i.`item_id`);
