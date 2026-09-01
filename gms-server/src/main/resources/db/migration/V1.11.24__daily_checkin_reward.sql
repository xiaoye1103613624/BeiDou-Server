-- 每日签到奖励表（Web 可配，热重载）

CREATE TABLE IF NOT EXISTS `daily_checkin_reward` (
    `day`           INT          NOT NULL COMMENT '签到天数 1..28',
    `icon_item_id`  INT          NOT NULL DEFAULT 2000000 COMMENT '格子展示图标物品ID',
    `mesos`         INT          NOT NULL DEFAULT 0 COMMENT '金币奖励',
    `item_id`       INT          NOT NULL DEFAULT 0 COMMENT '物品1 ID，0表示无',
    `item_qty`      INT          NOT NULL DEFAULT 0 COMMENT '物品1数量',
    `expire_days`   INT          NOT NULL DEFAULT 0 COMMENT '物品1有效天数，0永久',
    `item2_id`      INT          NOT NULL DEFAULT 0 COMMENT '物品2 ID，0表示无',
    `item2_qty`     INT          NOT NULL DEFAULT 0 COMMENT '物品2数量',
    `item2_expire`  INT          NOT NULL DEFAULT 0 COMMENT '物品2有效天数，0永久',
    `slot_type`     INT          NOT NULL DEFAULT 0 COMMENT '扩容栏位类型 1装备2消耗3设置4其他5现金',
    `slot_count`    INT          NOT NULL DEFAULT 0 COMMENT '扩容格数',
    `remark`        VARCHAR(128) NULL COMMENT '备注',
    PRIMARY KEY (`day`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日签到奖励配置';

INSERT INTO `daily_checkin_reward`
    (`day`, `icon_item_id`, `mesos`, `item_id`, `item_qty`, `expire_days`,
     `item2_id`, `item2_qty`, `item2_expire`, `slot_type`, `slot_count`, `remark`)
SELECT d.day, 2000000, 1, 0, 0, 0, 0, 0, 0, 0, 0, NULL
FROM (
    SELECT 1 AS day UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5 UNION SELECT 6 UNION SELECT 7
    UNION SELECT 8 UNION SELECT 9 UNION SELECT 10 UNION SELECT 11 UNION SELECT 12 UNION SELECT 13 UNION SELECT 14
    UNION SELECT 15 UNION SELECT 16 UNION SELECT 17 UNION SELECT 18 UNION SELECT 19 UNION SELECT 20 UNION SELECT 21
    UNION SELECT 22 UNION SELECT 23 UNION SELECT 24 UNION SELECT 25 UNION SELECT 26 UNION SELECT 27 UNION SELECT 28
) d
WHERE NOT EXISTS (SELECT 1 FROM `daily_checkin_reward` r WHERE r.day = d.day);
