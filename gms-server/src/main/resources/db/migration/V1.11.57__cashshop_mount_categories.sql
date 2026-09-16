-- 窗口商城：坐骑一级标注 + 二级（坐骑/鞍具/坐骑道具）；商品由服务端 seedMountCatalog 灌入

-- ---------- L1 坐骑（标注，无商品）：复用已有同名顶级分类，纠偏 legacy 11:0 ----------
UPDATE `xy_cashshop_category`
SET `legacy_tab`      = 11,
    `legacy_category` = 0,
    `click_type`      = 'SHOW_ITEMS',
    `enabled`         = 1,
    `sort`            = 700,
    `remark`          = 'mount-root'
WHERE `name` = '坐骑'
  AND `parent_id` IS NULL;

INSERT INTO `xy_cashshop_category` (`name`, `parent_id`, `sort`, `enabled`, `click_type`, `is_hot`,
                                    `legacy_tab`, `legacy_category`, `remark`)
SELECT '坐骑', NULL, 700, 1, 'SHOW_ITEMS', 0, 11, 0, 'mount-root'
WHERE NOT EXISTS (SELECT 1
                  FROM `xy_cashshop_category`
                  WHERE `name` = '坐骑'
                    AND `parent_id` IS NULL)
  AND NOT EXISTS (SELECT 1
                  FROM `xy_cashshop_category`
                  WHERE `legacy_tab` = 11
                    AND `legacy_category` = 0);

-- 若仅有 legacy 11:0 而无名为「坐骑」的顶级行，纠名为坐骑
UPDATE `xy_cashshop_category`
SET `name`   = '坐骑',
    `sort`   = 700,
    `remark` = 'mount-root'
WHERE `legacy_tab` = 11
  AND `legacy_category` = 0
  AND `parent_id` IS NULL
  AND (`name` IS NULL OR `name` <> '坐骑');

-- ---------- L2：坐骑本体 11:1 ----------
INSERT INTO `xy_cashshop_category` (`name`, `parent_id`, `sort`, `enabled`, `click_type`, `is_hot`,
                                    `legacy_tab`, `legacy_category`, `remark`)
SELECT '坐骑',
       (SELECT `id`
        FROM `xy_cashshop_category`
        WHERE `legacy_tab` = 11
          AND `legacy_category` = 0
        ORDER BY `id`
        LIMIT 1),
       710,
       1,
       'SHOW_ITEMS',
       0,
       11,
       1,
       'mount-body'
WHERE NOT EXISTS (SELECT 1
                  FROM `xy_cashshop_category`
                  WHERE `legacy_tab` = 11
                    AND `legacy_category` = 1);

UPDATE `xy_cashshop_category` c
    INNER JOIN `xy_cashshop_category` p
    ON p.`legacy_tab` = 11 AND p.`legacy_category` = 0
SET c.`parent_id` = p.`id`,
    c.`name`      = '坐骑',
    c.`sort`      = 710,
    c.`enabled`   = 1,
    c.`click_type`= 'SHOW_ITEMS',
    c.`remark`    = 'mount-body'
WHERE c.`legacy_tab` = 11
  AND c.`legacy_category` = 1;

-- ---------- L2：鞍具 11:2 ----------
INSERT INTO `xy_cashshop_category` (`name`, `parent_id`, `sort`, `enabled`, `click_type`, `is_hot`,
                                    `legacy_tab`, `legacy_category`, `remark`)
SELECT '鞍具',
       (SELECT `id`
        FROM `xy_cashshop_category`
        WHERE `legacy_tab` = 11
          AND `legacy_category` = 0
        ORDER BY `id`
        LIMIT 1),
       720,
       1,
       'SHOW_ITEMS',
       0,
       11,
       2,
       'mount-eq'
WHERE NOT EXISTS (SELECT 1
                  FROM `xy_cashshop_category`
                  WHERE `legacy_tab` = 11
                    AND `legacy_category` = 2);

UPDATE `xy_cashshop_category` c
    INNER JOIN `xy_cashshop_category` p
    ON p.`legacy_tab` = 11 AND p.`legacy_category` = 0
SET c.`parent_id` = p.`id`,
    c.`name`      = '鞍具',
    c.`sort`      = 720,
    c.`enabled`   = 1,
    c.`click_type`= 'SHOW_ITEMS',
    c.`remark`    = 'mount-eq'
WHERE c.`legacy_tab` = 11
  AND c.`legacy_category` = 2;

-- ---------- L2：坐骑道具 11:3 ----------
INSERT INTO `xy_cashshop_category` (`name`, `parent_id`, `sort`, `enabled`, `click_type`, `is_hot`,
                                    `legacy_tab`, `legacy_category`, `remark`)
SELECT '坐骑道具',
       (SELECT `id`
        FROM `xy_cashshop_category`
        WHERE `legacy_tab` = 11
          AND `legacy_category` = 0
        ORDER BY `id`
        LIMIT 1),
       730,
       1,
       'SHOW_ITEMS',
       0,
       11,
       3,
       'mount-use'
WHERE NOT EXISTS (SELECT 1
                  FROM `xy_cashshop_category`
                  WHERE `legacy_tab` = 11
                    AND `legacy_category` = 3);

UPDATE `xy_cashshop_category` c
    INNER JOIN `xy_cashshop_category` p
    ON p.`legacy_tab` = 11 AND p.`legacy_category` = 0
SET c.`parent_id` = p.`id`,
    c.`name`      = '坐骑道具',
    c.`sort`      = 730,
    c.`enabled`   = 1,
    c.`click_type`= 'SHOW_ITEMS',
    c.`remark`    = 'mount-use'
WHERE c.`legacy_tab` = 11
  AND c.`legacy_category` = 3;
