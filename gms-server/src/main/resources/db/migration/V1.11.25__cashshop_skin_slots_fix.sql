-- 窗口商城：补婚礼/效果分类；皮肤/伤害皮肤大类与上架；扩展背包栏券；修正旧「动漫皮肤」命名

-- ---------- missing kCats: 婚礼 / 效果 ----------
INSERT INTO `xy_cashshop_category` (`name`, `parent_id`, `sort`, `enabled`, `click_type`, `is_hot`, `legacy_tab`, `legacy_category`, `remark`)
SELECT '婚礼', NULL, 340, 1, 'SHOW_ITEMS', 0, 5, 4, 'catalog-fix'
WHERE NOT EXISTS (SELECT 1 FROM `xy_cashshop_category` WHERE `legacy_tab` = 5 AND `legacy_category` = 4);

INSERT INTO `xy_cashshop_category` (`name`, `parent_id`, `sort`, `enabled`, `click_type`, `is_hot`, `legacy_tab`, `legacy_category`, `remark`)
SELECT '效果', NULL, 350, 1, 'SHOW_ITEMS', 0, 5, 5, 'catalog-fix'
WHERE NOT EXISTS (SELECT 1 FROM `xy_cashshop_category` WHERE `legacy_tab` = 5 AND `legacy_category` = 5);

-- Relink effect items (501xxxx / 528xxxx) that were wrongly under 宠装(6,1) into 效果(5,5)
UPDATE `xy_cashshop_category_item` ci
    INNER JOIN `xy_cashshop_category` wrong
        ON wrong.`id` = ci.`category_id` AND wrong.`legacy_tab` = 6 AND wrong.`legacy_category` = 1
    INNER JOIN `xy_cashshop_category` effect
        ON effect.`legacy_tab` = 5 AND effect.`legacy_category` = 5
SET ci.`category_id` = effect.`id`
WHERE ci.`item_id` BETWEEN 5010000 AND 5019999
   OR ci.`item_id` IN (5281000, 5281001);

-- 热门让出 9:0（先改到 8:0，客户端不单独展示）
UPDATE `xy_cashshop_category`
SET `legacy_tab` = 8, `legacy_category` = 0
WHERE `name` = '热门' AND `is_hot` = 1;

-- ---------- 皮肤大类（阿尔泰，legacy 9:0）----------
UPDATE `xy_cashshop_category`
SET `name` = '皮肤',
    `legacy_tab` = 9,
    `legacy_category` = 0,
    `click_type` = 'SHOW_ITEMS',
    `click_param` = NULL,
    `sort` = 10,
    `remark` = 'skin-altair'
WHERE `name` IN ('动漫皮肤', '皮肤')
   OR (`legacy_tab` = 9 AND `legacy_category` = 1);

INSERT INTO `xy_cashshop_category` (`name`, `parent_id`, `sort`, `enabled`, `click_type`, `is_hot`, `legacy_tab`, `legacy_category`, `remark`)
SELECT '皮肤', NULL, 10, 1, 'SHOW_ITEMS', 0, 9, 0, 'skin-altair'
WHERE NOT EXISTS (SELECT 1 FROM `xy_cashshop_category` WHERE `legacy_tab` = 9 AND `legacy_category` = 0 AND `name` = '皮肤')
  AND NOT EXISTS (SELECT 1 FROM `xy_cashshop_category` WHERE `name` = '皮肤');

-- ---------- 伤害皮肤大类（legacy 10:0）----------
UPDATE `xy_cashshop_category`
SET `name` = '伤害皮肤',
    `legacy_tab` = 10,
    `legacy_category` = 0,
    `click_type` = 'SHOW_ITEMS',
    `click_param` = NULL,
    `sort` = 20,
    `remark` = 'damage-skin-cash'
WHERE `name` = '伤害皮肤'
   OR (`legacy_tab` = 9 AND `legacy_category` = 2);

INSERT INTO `xy_cashshop_category` (`name`, `parent_id`, `sort`, `enabled`, `click_type`, `is_hot`, `legacy_tab`, `legacy_category`, `remark`)
SELECT '伤害皮肤', NULL, 20, 1, 'SHOW_ITEMS', 0, 10, 0, 'damage-skin-cash'
WHERE NOT EXISTS (SELECT 1 FROM `xy_cashshop_category` WHERE `legacy_tab` = 10 AND `legacy_category` = 0)
  AND NOT EXISTS (SELECT 1 FROM `xy_cashshop_category` WHERE `name` = '伤害皮肤');

-- ---------- 阿尔泰皮肤商品：10 万点券；挂到皮肤分类 ----------
-- 仅更新已存在的 1008900-1009999；缺失的由 seedDefaults / 管理端导入
UPDATE `xy_cashshop_item`
SET `price` = 100000
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

-- ---------- 伤害皮肤栏 5910000：5 万点券 ----------
INSERT INTO `xy_cashshop_item` (`item_id`, `price`, `count`, `period`, `gender`, `name`, `enabled`)
SELECT 5910000, 50000, 1, 0, 2, '伤害皮肤栏', 1
WHERE NOT EXISTS (SELECT 1 FROM `xy_cashshop_item` WHERE `item_id` = 5910000);

UPDATE `xy_cashshop_item`
SET `price` = 50000, `name` = '伤害皮肤栏', `enabled` = 1
WHERE `item_id` = 5910000;

INSERT INTO `xy_cashshop_category_item` (`category_id`, `item_id`, `sort`, `enabled`)
SELECT c.`id`, 5910000, 0, 1
FROM `xy_cashshop_category` c
WHERE c.`legacy_tab` = 10
  AND c.`legacy_category` = 0
  AND NOT EXISTS (SELECT 1
                  FROM `xy_cashshop_category_item` ci
                  WHERE ci.`category_id` = c.`id`
                    AND ci.`item_id` = 5910000);

-- ---------- 扩展背包栏券（9110xxx，购买时服务端直接 +8 格，不发实物）----------
INSERT INTO `xy_cashshop_item` (`item_id`, `price`, `count`, `period`, `gender`, `name`, `enabled`)
SELECT 9110000, 6400, 1, 0, 2, '装备栏扩展券(+8)', 1
WHERE NOT EXISTS (SELECT 1 FROM `xy_cashshop_item` WHERE `item_id` = 9110000);
INSERT INTO `xy_cashshop_item` (`item_id`, `price`, `count`, `period`, `gender`, `name`, `enabled`)
SELECT 9111000, 6400, 1, 0, 2, '消耗栏扩展券(+8)', 1
WHERE NOT EXISTS (SELECT 1 FROM `xy_cashshop_item` WHERE `item_id` = 9111000);
INSERT INTO `xy_cashshop_item` (`item_id`, `price`, `count`, `period`, `gender`, `name`, `enabled`)
SELECT 9112000, 6400, 1, 0, 2, '设置栏扩展券(+8)', 1
WHERE NOT EXISTS (SELECT 1 FROM `xy_cashshop_item` WHERE `item_id` = 9112000);
INSERT INTO `xy_cashshop_item` (`item_id`, `price`, `count`, `period`, `gender`, `name`, `enabled`)
SELECT 9113000, 6400, 1, 0, 2, '其他栏扩展券(+8)', 1
WHERE NOT EXISTS (SELECT 1 FROM `xy_cashshop_item` WHERE `item_id` = 9113000);

INSERT INTO `xy_cashshop_category_item` (`category_id`, `item_id`, `sort`, `enabled`)
SELECT c.`id`, v.`item_id`, v.`sort`, 1
FROM `xy_cashshop_category` c
         CROSS JOIN (SELECT 9110000 AS item_id, 200 AS sort
                     UNION ALL SELECT 9111000, 201
                     UNION ALL SELECT 9112000, 202
                     UNION ALL SELECT 9113000, 203) v
WHERE c.`legacy_tab` = 5
  AND c.`legacy_category` = 2
  AND NOT EXISTS (SELECT 1
                  FROM `xy_cashshop_category_item` ci
                  WHERE ci.`category_id` = c.`id`
                    AND ci.`item_id` = v.`item_id`);
