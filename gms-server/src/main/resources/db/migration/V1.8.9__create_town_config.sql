CREATE TABLE IF NOT EXISTS `xy_town_config`
(
    `id`          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
    `map_id`      INT          NOT NULL COMMENT '城镇地图ID',
    `town_name`   VARCHAR(128) NOT NULL COMMENT '城镇名称',
    `enabled`     TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否开放（0=关闭 1=开放）',
    `create_time` TIMESTAMP            DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` TIMESTAMP            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    UNIQUE KEY `uk_map_id` (`map_id`)
) COMMENT '城镇开放配置表' ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

INSERT INTO `xy_town_config` (`map_id`, `town_name`, `enabled`)
VALUES (100000000, '射手村', 1),
       (101000000, '魔法密林', 1),
       (102000000, '勇士部落', 1),
       (103000000, '废弃都市', 1),
       (104000000, '港口', 1),
       (120000000, '诺特勒斯', 1),
       (130000000, '圣地', 1),
       (140000000, '里恩', 1),
       (200000000, '天空之城', 1),
       (211000000, '冰封雪域', 1),
       (220000000, '玩具城', 1),
       (230000000, '水下世界', 1),
       (240000000, '神木村', 1),
       (250000000, '武陵', 1),
       (251000000, '百草村', 1),
       (260000000, '阿里安特', 1),
       (261000000, '玛加提亚', 1),
       (310000000, '埃德尔斯坦', 1);
