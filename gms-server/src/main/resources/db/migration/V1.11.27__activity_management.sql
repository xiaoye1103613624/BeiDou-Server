-- 活动管理系统：目录 / 排期 / 场次 / 报名

CREATE TABLE IF NOT EXISTS `activity_def` (
    `code`                VARCHAR(64)  NOT NULL COMMENT '活动编码',
    `name_zh`             VARCHAR(128) NOT NULL COMMENT '中文名',
    `name_en`             VARCHAR(128) NOT NULL COMMENT '英文名',
    `category`            VARCHAR(32)  NOT NULL COMMENT 'TEAM/JUMP/QUIZ/TREASURE/MINIGAME/OTHER',
    `lobby_map_id`        INT          NOT NULL COMMENT '报名/集合地图',
    `event_map_id`        INT          NOT NULL COMMENT '开赛地图（可与lobby相同）',
    `related_maps`        VARCHAR(512) NOT NULL DEFAULT '[]' COMMENT '相关地图JSON数组，清场/人数统计用',
    `team_event`          TINYINT      NOT NULL DEFAULT 0 COMMENT '是否组队对抗',
    `supports_map_start`  TINYINT      NOT NULL DEFAULT 0 COMMENT '是否支持MapleMap.startEvent',
    `enabled`             TINYINT      NOT NULL DEFAULT 1 COMMENT '是否启用',
    `sort_order`          INT          NOT NULL DEFAULT 0 COMMENT '排序',
    `default_max_players` INT          NOT NULL DEFAULT 30 COMMENT '默认人数上限',
    `remark`              VARCHAR(255) NULL COMMENT '备注',
    PRIMARY KEY (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动目录';

CREATE TABLE IF NOT EXISTS `activity_schedule` (
    `id`                   BIGINT       NOT NULL AUTO_INCREMENT,
    `activity_code`        VARCHAR(64)  NOT NULL COMMENT '活动编码',
    `world_id`             INT          NOT NULL DEFAULT 0,
    `channel_id`           INT          NOT NULL COMMENT '频道号，从1开始',
    `schedule_type`        VARCHAR(16)  NOT NULL COMMENT 'ONCE/DAILY/WEEKLY',
    `start_at`             DATETIME     NULL COMMENT 'ONCE：计划开始时间',
    `cron_time`            TIME         NULL COMMENT 'DAILY/WEEKLY：每日时刻',
    `days_of_week`         VARCHAR(32)  NULL COMMENT 'WEEKLY：1-7逗号分隔，1=周一',
    `max_players`          INT          NOT NULL DEFAULT 30,
    `notify_minutes`       INT          NOT NULL DEFAULT 30 COMMENT '开始前通知时长(分)',
    `notify_interval_sec`  INT          NOT NULL DEFAULT 60 COMMENT '通知间隔(秒)',
    `prewarp_minutes`      INT          NOT NULL DEFAULT 5 COMMENT '开始前自动传送(分)',
    `enabled`              TINYINT      NOT NULL DEFAULT 1,
    `next_run_at`          DATETIME     NULL COMMENT '下次执行时间（服务端维护）',
    `created_at`           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at`           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_activity_schedule_next` (`enabled`, `next_run_at`),
    KEY `idx_activity_schedule_code` (`activity_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动排期';

CREATE TABLE IF NOT EXISTS `activity_session` (
    `id`               BIGINT       NOT NULL AUTO_INCREMENT,
    `schedule_id`      BIGINT       NULL COMMENT '来源排期，手动开为NULL',
    `activity_code`    VARCHAR(64)  NOT NULL,
    `world_id`         INT          NOT NULL DEFAULT 0,
    `channel_id`       INT          NOT NULL,
    `status`           VARCHAR(32)  NOT NULL COMMENT 'NOTIFYING/REGISTERING/PREWARP/RUNNING/STOPPED',
    `max_players`      INT          NOT NULL DEFAULT 30,
    `planned_start_at` DATETIME     NULL,
    `opened_at`        DATETIME     NULL,
    `started_at`       DATETIME     NULL,
    `ended_at`         DATETIME     NULL,
    `extra_info`       VARCHAR(512) NULL,
    `created_at`       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_activity_session_active` (`world_id`, `channel_id`, `status`),
    KEY `idx_activity_session_code` (`activity_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动场次';

CREATE TABLE IF NOT EXISTS `activity_registration` (
    `id`             BIGINT       NOT NULL AUTO_INCREMENT,
    `session_id`     BIGINT       NOT NULL,
    `character_id`   INT          NOT NULL,
    `character_name` VARCHAR(32)  NOT NULL,
    `registered_at`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `warped`         TINYINT      NOT NULL DEFAULT 0,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_session_character` (`session_id`, `character_id`),
    KEY `idx_activity_reg_session` (`session_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动报名';

INSERT INTO `activity_def`
(`code`, `name_zh`, `name_en`, `category`, `lobby_map_id`, `event_map_id`, `related_maps`,
 `team_event`, `supports_map_start`, `enabled`, `sort_order`, `default_max_players`, `remark`)
VALUES
('coconut', '打椰子', 'Coconut Harvest', 'TEAM', 109080000, 109080000, '[109080000,109080001,109080002,109080003]', 1, 1, 1, 10, 30, NULL),
('snowball', '滚雪球', 'Snowball', 'TEAM', 109060001, 109060000, '[109060000,109060001,109060002,109060003,109060004,109060005]', 1, 1, 1, 20, 30, NULL),
('ola_random', '上楼～上楼～随机赛道', 'Ola Ola Random', 'JUMP', 109030001, 109030001, '[109030001,109030002,109030003]', 0, 0, 1, 30, 30, NULL),
('ola_1', '上楼～上楼～赛道1', 'Ola Ola Track 1', 'JUMP', 109030101, 109030101, '[109030101,109030102,109030103]', 0, 1, 1, 40, 30, NULL),
('ola_2', '上楼～上楼～赛道2', 'Ola Ola Track 2', 'JUMP', 109030201, 109030201, '[109030201,109030202,109030203]', 0, 1, 1, 50, 30, NULL),
('ola_3', '上楼～上楼～赛道3', 'Ola Ola Track 3', 'JUMP', 109030301, 109030301, '[109030301,109030302,109030303]', 0, 1, 1, 60, 30, NULL),
('ola_4', '上楼～上楼～赛道4', 'Ola Ola Track 4', 'JUMP', 109030401, 109030401, '[109030401,109030402,109030403]', 0, 1, 1, 70, 30, NULL),
('fitness', '向高地（体能测试）', 'Physical Fitness', 'JUMP', 109040000, 109040000, '[109040000,109040001,109040002,109040003,109040004]', 0, 1, 1, 80, 30, NULL),
('ox_quiz', 'OX问答', 'OX Quiz', 'QUIZ', 109020001, 109020001, '[109020001]', 0, 1, 1, 90, 30, NULL),
('treasure', '寻宝', 'Find the Jewel', 'TREASURE', 109010000, 109010000, '[109010000,109010100,109010200,109010300,109010400]', 0, 0, 1, 100, 30, NULL),
('coke_play', '打瓶盖', 'Coke Play', 'TEAM', 109080010, 109080010, '[109080010,109080011,109080012]', 1, 0, 1, 110, 30, NULL),
('sheep_ranch', '羊狼牧场', 'Sheep Ranch', 'MINIGAME', 109090000, 109090000, '[109090000]', 0, 0, 1, 120, 30, NULL),
('minigame_challenge', '小游戏挑战', 'Minigame Challenge', 'MINIGAME', 109070000, 109070000, '[109070000]', 0, 0, 1, 130, 30, NULL),
('jump_fitness_stage', '高地跳跳', 'Fitness Stage', 'JUMP', 109040001, 109040001, '[109040001,109040002,109040003,109040004]', 0, 0, 1, 200, 50, NULL),
('jump_ola', '上楼跳跳', 'Ola Jump', 'JUMP', 109030001, 109030001, '[109030001,109030002,109030003]', 0, 0, 1, 210, 50, NULL),
('jump_forest', '森林跳跳', 'Forest Jump', 'JUMP', 105040316, 105040316, '[105040316]', 0, 0, 1, 220, 50, NULL),
('jump_subway', '地铁跳跳', 'Subway Jump', 'JUMP', 103000900, 103000900, '[103000900]', 0, 0, 1, 230, 50, NULL),
('jump_volcano', '火山跳跳', 'Volcano Jump', 'JUMP', 280020000, 280020000, '[280020000]', 0, 0, 1, 240, 50, NULL),
('jump_patience', '忍苦跳跳', 'Patience Jump', 'JUMP', 101000100, 101000100, '[101000100]', 0, 0, 1, 250, 50, NULL)
ON DUPLICATE KEY UPDATE
    `name_zh` = VALUES(`name_zh`),
    `name_en` = VALUES(`name_en`),
    `related_maps` = VALUES(`related_maps`);
