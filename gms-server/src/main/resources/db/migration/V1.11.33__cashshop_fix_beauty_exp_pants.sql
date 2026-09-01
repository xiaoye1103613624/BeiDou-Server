-- 双倍经验卡(521xxxx)误挂「美容」等桶 → 挪到「游戏」(legacy 5:2)

INSERT INTO `xy_cashshop_category` (`name`, `parent_id`, `sort`, `enabled`, `click_type`, `is_hot`,
                                    `legacy_tab`, `legacy_category`, `remark`)
SELECT '游戏', NULL, 320, 1, 'SHOW_ITEMS', 0, 5, 2, 'catalog-fix'
WHERE NOT EXISTS (SELECT 1 FROM `xy_cashshop_category` WHERE `legacy_tab` = 5 AND `legacy_category` = 2);

UPDATE `xy_cashshop_category_item` ci
    INNER JOIN `xy_cashshop_category` wrong
        ON wrong.`id` = ci.`category_id`
            AND NOT (wrong.`legacy_tab` = 5 AND wrong.`legacy_category` = 2)
    INNER JOIN `xy_cashshop_category` game
        ON game.`legacy_tab` = 5 AND game.`legacy_category` = 2
SET ci.`category_id` = game.`id`
WHERE ci.`item_id` BETWEEN 5210000 AND 5219999;

-- 裤裙分类名若写成「矿权」则纠正（legacy 2:5）
UPDATE `xy_cashshop_category`
SET `name` = '裤裙'
WHERE `legacy_tab` = 2
  AND `legacy_category` = 5
  AND `name` IN ('矿权', '裤装');
