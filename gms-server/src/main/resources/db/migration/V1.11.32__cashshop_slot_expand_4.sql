-- XY玩法：补背包栏 +4 扩展券（虚拟 SKU 911x004，4000 NX）

INSERT INTO `xy_cashshop_item` (`item_id`, `price`, `count`, `period`, `gender`, `name`, `enabled`, `remark`)
VALUES (9110004, 4000, 1, 0, 2, '装备栏扩展券(+4)', 1, 'xy-play:slot-equip-4'),
       (9111004, 4000, 1, 0, 2, '消耗栏扩展券(+4)', 1, 'xy-play:slot-use-4'),
       (9112004, 4000, 1, 0, 2, '设置栏扩展券(+4)', 1, 'xy-play:slot-setup-4'),
       (9113004, 4000, 1, 0, 2, '其他栏扩展券(+4)', 1, 'xy-play:slot-etc-4')
ON DUPLICATE KEY UPDATE `price`   = VALUES(`price`),
                        `enabled` = 1,
                        `name`    = VALUES(`name`),
                        `remark`  = VALUES(`remark`);

-- 同步 +8 备注命名
UPDATE `xy_cashshop_item`
SET `remark` = 'xy-play:slot-equip-8', `name` = '装备栏扩展券(+8)'
WHERE `item_id` = 9110000;
UPDATE `xy_cashshop_item`
SET `remark` = 'xy-play:slot-use-8', `name` = '消耗栏扩展券(+8)'
WHERE `item_id` = 9111000;
UPDATE `xy_cashshop_item`
SET `remark` = 'xy-play:slot-setup-8', `name` = '设置栏扩展券(+8)'
WHERE `item_id` = 9112000;
UPDATE `xy_cashshop_item`
SET `remark` = 'xy-play:slot-etc-8', `name` = '其他栏扩展券(+8)'
WHERE `item_id` = 9113000;

INSERT INTO `xy_cashshop_category_item` (`category_id`, `item_id`, `sort`, `enabled`)
SELECT c.`id`, v.`item_id`, v.`sort`, 1
FROM `xy_cashshop_category` c
         CROSS JOIN (SELECT 9110004 AS item_id, 50 AS sort
                     UNION ALL SELECT 9111004, 51
                     UNION ALL SELECT 9112004, 52
                     UNION ALL SELECT 9113004, 53
                     UNION ALL SELECT 9110000, 54
                     UNION ALL SELECT 9111000, 55
                     UNION ALL SELECT 9112000, 56
                     UNION ALL SELECT 9113000, 57) v
WHERE c.`legacy_tab` = 10
  AND c.`legacy_category` = 0
  AND NOT EXISTS (SELECT 1
                  FROM `xy_cashshop_category_item` ci
                  WHERE ci.`category_id` = c.`id`
                    AND ci.`item_id` = v.`item_id`);

UPDATE `xy_cashshop_category_item` ci
    INNER JOIN `xy_cashshop_category` c ON c.`id` = ci.`category_id`
    INNER JOIN (SELECT 9110004 AS item_id, 50 AS sort
                UNION ALL SELECT 9111004, 51
                UNION ALL SELECT 9112004, 52
                UNION ALL SELECT 9113004, 53
                UNION ALL SELECT 9110000, 54
                UNION ALL SELECT 9111000, 55
                UNION ALL SELECT 9112000, 56
                UNION ALL SELECT 9113000, 57) v ON v.`item_id` = ci.`item_id`
SET ci.`sort` = v.`sort`, ci.`enabled` = 1
WHERE c.`legacy_tab` = 10
  AND c.`legacy_category` = 0;
