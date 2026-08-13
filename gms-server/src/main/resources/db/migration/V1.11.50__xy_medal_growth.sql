-- 成长勋章：怪物卡注入属性 + 勋章池幻化
CREATE TABLE IF NOT EXISTS `xy_medal_growth` (
    `character_id`      INT          NOT NULL COMMENT '角色ID',
    `illusion_medal_id` INT          NOT NULL DEFAULT 0 COMMENT '当前幻化外观勋章ID，0=默认1142747',
    `region_flags`      VARCHAR(512) NOT NULL DEFAULT '' COMMENT '已注入地区id，逗号分隔',
    `elite_flags`       VARCHAR(512) NOT NULL DEFAULT '' COMMENT '已注入野外Boss卡ID，逗号分隔',
    `exped_flags`       VARCHAR(512) NOT NULL DEFAULT '' COMMENT '已注入远征Boss卡ID，逗号分隔',
    `pool_json`         VARCHAR(2048) NOT NULL DEFAULT '[]' COMMENT '勋章池JSON数组',
    `stat_str`          INT          NOT NULL DEFAULT 0 COMMENT '卡注入累计力量',
    `stat_dex`          INT          NOT NULL DEFAULT 0 COMMENT '卡注入累计敏捷',
    `stat_int`          INT          NOT NULL DEFAULT 0 COMMENT '卡注入累计智力',
    `stat_luk`          INT          NOT NULL DEFAULT 0 COMMENT '卡注入累计运气',
    `stat_watk`         INT          NOT NULL DEFAULT 0 COMMENT '卡注入累计攻击',
    `stat_matk`         INT          NOT NULL DEFAULT 0 COMMENT '卡注入累计魔力',
    `update_time`       DATETIME     NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`character_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成长勋章进度（地区/野外Boss/远征卡注入+勋章池）';
