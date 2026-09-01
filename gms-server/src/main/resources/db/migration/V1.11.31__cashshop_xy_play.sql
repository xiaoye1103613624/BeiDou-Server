-- 窗口商城：tab10「伤害皮肤」→「XY玩法」；下架皮肤本体虚拟 SKU，改卖自定义玩法入口道具

-- ---------- 分类改名 ----------
UPDATE `xy_cashshop_category`
SET `name`   = 'XY玩法',
    `sort`   = 20,
    `remark` = 'xy-play'
WHERE `legacy_tab` = 10
  AND `legacy_category` = 0;

UPDATE `xy_cashshop_category`
SET `name`   = 'XY玩法',
    `legacy_tab` = 10,
    `legacy_category` = 0,
    `click_type` = 'SHOW_ITEMS',
    `sort` = 20,
    `remark` = 'xy-play'
WHERE `name` IN ('伤害皮肤', 'XY玩法');

INSERT INTO `xy_cashshop_category` (`name`, `parent_id`, `sort`, `enabled`, `click_type`, `is_hot`,
                                    `legacy_tab`, `legacy_category`, `remark`)
SELECT 'XY玩法', NULL, 20, 1, 'SHOW_ITEMS', 0, 10, 0, 'xy-play'
WHERE NOT EXISTS (SELECT 1 FROM `xy_cashshop_category` WHERE `legacy_tab` = 10 AND `legacy_category` = 0);

-- ---------- 从 XY玩法 卸下伤害皮肤本体（5920001–5929999）并禁用 ----------
DELETE ci
FROM `xy_cashshop_category_item` ci
         INNER JOIN `xy_cashshop_category` c ON c.`id` = ci.`category_id`
WHERE c.`legacy_tab` = 10
  AND c.`legacy_category` = 0
  AND ci.`item_id` > 5920000
  AND ci.`item_id` <= 5929999;

UPDATE `xy_cashshop_item`
SET `enabled` = 0,
    `remark`  = CONCAT(IFNULL(`remark`, ''), CASE
                                                 WHEN IFNULL(`remark`, '') = '' THEN 'disabled:damage-skin-body'
                                                 ELSE ';disabled:damage-skin-body' END)
WHERE `item_id` > 5920000
  AND `item_id` <= 5929999;

-- ---------- 入口道具 upsert ----------
INSERT INTO `xy_cashshop_item` (`item_id`, `price`, `count`, `period`, `gender`, `name`, `enabled`, `remark`)
VALUES (5910000, 50000, 1, 0, 2, '伤害皮肤栏', 1, 'xy-play:damage-skin-opener'),
       (5900000, 50000, 1, 0, 2, '融合外观锻造锤', 1, 'xy-play:fusion-anvil'),
       (5782000, 50000, 1, 0, 2, '七彩棱镜', 1, 'xy-play:color-prism'),
       (5920000, 50000, 1, 0, 2, '美容院解锁券', 1, 'xy-play:beauty-unlock'),
       (9110000, 6400, 1, 0, 2, '装备栏扩展券(+8)', 1, 'xy-play:slot-equip'),
       (9111000, 6400, 1, 0, 2, '消耗栏扩展券(+8)', 1, 'xy-play:slot-use'),
       (9112000, 6400, 1, 0, 2, '设置栏扩展券(+8)', 1, 'xy-play:slot-setup'),
       (9113000, 6400, 1, 0, 2, '其他栏扩展券(+8)', 1, 'xy-play:slot-etc')
ON DUPLICATE KEY UPDATE `price`   = VALUES(`price`),
                        `enabled` = 1,
                        `name`    = VALUES(`name`),
                        `remark`  = VALUES(`remark`);

-- ---------- 挂到 XY玩法；从「游戏」分类移除扩展券（避免重复） ----------
DELETE ci
FROM `xy_cashshop_category_item` ci
         INNER JOIN `xy_cashshop_category` c ON c.`id` = ci.`category_id`
WHERE c.`legacy_tab` = 5
  AND c.`legacy_category` = 2
  AND ci.`item_id` IN (9110000, 9111000, 9112000, 9113000);

INSERT INTO `xy_cashshop_category_item` (`category_id`, `item_id`, `sort`, `enabled`)
SELECT c.`id`, v.`item_id`, v.`sort`, 1
FROM `xy_cashshop_category` c
         CROSS JOIN (SELECT 5910000 AS item_id, 10 AS sort
                     UNION ALL SELECT 5900000, 20
                     UNION ALL SELECT 5782000, 30
                     UNION ALL SELECT 5920000, 40
                     UNION ALL SELECT 9110000, 50
                     UNION ALL SELECT 9111000, 51
                     UNION ALL SELECT 9112000, 52
                     UNION ALL SELECT 9113000, 53) v
WHERE c.`legacy_tab` = 10
  AND c.`legacy_category` = 0
  AND NOT EXISTS (SELECT 1
                  FROM `xy_cashshop_category_item` ci
                  WHERE ci.`category_id` = c.`id`
                    AND ci.`item_id` = v.`item_id`);

UPDATE `xy_cashshop_category_item` ci
    INNER JOIN `xy_cashshop_category` c ON c.`id` = ci.`category_id`
    INNER JOIN (SELECT 5910000 AS item_id, 10 AS sort
                UNION ALL SELECT 5900000, 20
                UNION ALL SELECT 5782000, 30
                UNION ALL SELECT 5920000, 40
                UNION ALL SELECT 9110000, 50
                UNION ALL SELECT 9111000, 51
                UNION ALL SELECT 9112000, 52
                UNION ALL SELECT 9113000, 53) v ON v.`item_id` = ci.`item_id`
SET ci.`sort` = v.`sort`, ci.`enabled` = 1
WHERE c.`legacy_tab` = 10
  AND c.`legacy_category` = 0;
