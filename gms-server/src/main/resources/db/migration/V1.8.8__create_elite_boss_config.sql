CREATE TABLE IF NOT EXISTS `xy_elite_boss_config`
(
    `id`          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
    `map_id`      INT          NOT NULL COMMENT '所在地图ID',
    `boss_id`     INT          NOT NULL COMMENT '怪物ID',
    `boss_name`   VARCHAR(128) NOT NULL COMMENT 'BOSS名称',
    `boss_time`   INT          NOT NULL DEFAULT 180 COMMENT '刷新时间（分钟）',
    `script_name` VARCHAR(256) COMMENT '对应事件脚本名称',
    `enabled`     TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用（0=禁用 1=启用）',
    `create_time` TIMESTAMP            DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` TIMESTAMP            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    UNIQUE KEY `uk_boss_id_map_id` (`boss_id`, `map_id`)
) COMMENT '精英BOSS（野外BOSS）配置表' ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 初始数据：从27个AreaBoss事件脚本中提取
INSERT INTO `xy_elite_boss_config` (`map_id`, `boss_id`, `boss_name`, `boss_time`, `script_name`, `enabled`)
VALUES (104000400, 2220000, '红蜗牛王', 180, 'AreaBossMano.js', 1),
       (101030404, 3220000, '树妖王', 180, 'AreaBossStumpy.js', 1),
       (251010102, 5220004, '巨型蜈蚣', 180, 'AreaBossCentipede.js', 1),
       (800020120, 6090002, '青竹武士', 180, 'AreaBossBamboo.js', 1),
       (260010201, 3220001, '大宇', 180, 'AreaBossDeo.js', 1),
       (107000300, 6220000, '多尔', 180, 'AreaBossDyle.js', 1),
       (200010300, 8220000, '艾利杰', 180, 'AreaBossEliza1.js', 1),
       (100040105, 5220002, '浮士德', 180, 'AreaBossFaust1.js', 1),
       (100040106, 5220002, '浮士德', 180, 'AreaBossFaust2.js', 1),
       (261030000, 8220002, '吉米拉', 180, 'AreaBossKimera.js', 1),
       (110040000, 5220001, '巨居蟹', 180, 'AreaBossKingClang.js', 1),
       (250010504, 7220002, '妖怪禅师', 180, 'AreaBossKingSageCat.js', 1),
       (240040401, 8220003, '大海兽', 180, 'AreaBossLeviathan.js', 1),
       (222010310, 7220001, '九尾狐', 180, 'AreaBossNineTailedFox.js', 1),
       (230020100, 4220001, '歇尔夫', 180, 'AreaBossSeruf.js', 1),
       (105090310, 8220009, '小吃店', 180, 'AreaBossSnackBar.js', 1),
       (250010304, 7220000, '肯德熊', 180, 'AreaBossTaeRoon.js', 1),
       (220050000, 5220003, '提莫', 180, 'AreaBossTimer1.js', 1),
       (220050100, 5220003, '提莫', 180, 'AreaBossTimer2.js', 1),
       (220050200, 5220003, '提莫', 180, 'AreaBossTimer3.js', 1),
       (221040301, 6220001, '朱诺', 180, 'AreaBossZeno.js', 1),
       (677000003, 9400610, '黑暗独角兽', 180, 'AreaBossDoor1.js', 1),
       (677000005, 9400609, '印第安老斑鸠', 180, 'AreaBossDoor2.js', 1),
       (677000009, 9400613, '沃勒福', 180, 'AreaBossDoor3.js', 1),
       (677000012, 9400633, '牛魔王', 180, 'AreaBossDoor4.js', 1),
       (677000001, 9400612, '牛魔王', 180, 'AreaBossDoor5.js', 1),
       (677000007, 9400611, '雪之猫女', 180, 'AreaBossDoor6.js', 1);
