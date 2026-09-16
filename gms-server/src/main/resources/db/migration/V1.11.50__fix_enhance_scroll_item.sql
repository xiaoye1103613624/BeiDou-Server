-- 纠正曾误用 2040805（手套攻击卷）作为 T5 强化卷/签到奖的库数据，统一回 2049300

UPDATE `daily_checkin_reward`
SET `item_id` = 2049300,
    `remark` = 'T5装备强化卷',
    `icon_item_id` = IFNULL(`icon_item_id`, 2000000)
WHERE `day` = 6
  AND `item_id` IN (2040805, 2049300);

UPDATE `xy_equip_enhance_cost` c
INNER JOIN `xy_equip_enhance_level` l ON l.id = c.level_id
SET c.`item_id` = 2049300
WHERE l.config_id = 1
  AND c.`item_id` = 2040805;
