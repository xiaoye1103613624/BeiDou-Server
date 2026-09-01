-- 活动奖励：档位 / 成绩 / 领取

CREATE TABLE IF NOT EXISTS `activity_reward_tier` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT,
    `activity_code`   VARCHAR(64)  NOT NULL,
    `tier_code`       VARCHAR(64)  NOT NULL COMMENT '档位编码，同活动内唯一',
    `tier_name`       VARCHAR(128) NOT NULL,
    `priority`        INT          NOT NULL DEFAULT 100 COMMENT '越小越先匹配，互斥组内只取最高优',
    `exclusive_group` VARCHAR(64)  NULL COMMENT '互斥组，同组只发一档',
    `match_json`      VARCHAR(512) NOT NULL DEFAULT '{}' COMMENT '条件JSON',
    `grant_mode`      VARCHAR(16)  NOT NULL DEFAULT 'CLAIM_NPC' COMMENT 'AUTO_BAG/AUTO_MAIL/CLAIM_NPC',
    `mesos`           BIGINT       NOT NULL DEFAULT 0,
    `exp`             INT          NOT NULL DEFAULT 0,
    `item_id`         INT          NOT NULL DEFAULT 0,
    `item_qty`        INT          NOT NULL DEFAULT 0,
    `item2_id`        INT          NOT NULL DEFAULT 0,
    `item2_qty`       INT          NOT NULL DEFAULT 0,
    `announce_name`   TINYINT      NOT NULL DEFAULT 0 COMMENT '是否全服公示名字',
    `announce_tpl`    VARCHAR(255) NULL COMMENT '公示模板，占位符0名字1活动2档位',
    `enabled`         TINYINT      NOT NULL DEFAULT 1,
    `remark`          VARCHAR(255) NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_activity_tier` (`activity_code`, `tier_code`),
    KEY `idx_reward_tier_code` (`activity_code`, `enabled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动奖励档位';

CREATE TABLE IF NOT EXISTS `activity_participant_result` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT,
    `session_id`      BIGINT       NOT NULL,
    `character_id`    INT          NOT NULL,
    `character_name`  VARCHAR(32)  NOT NULL,
    `team_id`         INT          NULL,
    `rank_no`         INT          NULL,
    `score`           INT          NULL,
    `finish_time_ms`  BIGINT       NULL,
    `outcome`         VARCHAR(32)  NOT NULL COMMENT 'WIN/LOSE/DRAW/COMPLETE/ELIMINATED/PARTICIPATED',
    `tags`            VARCHAR(255) NULL COMMENT '逗号标签 mvp,perfect',
    `created_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_session_chr_result` (`session_id`, `character_id`),
    KEY `idx_result_session` (`session_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动场次成绩';

CREATE TABLE IF NOT EXISTS `activity_reward_claim` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT,
    `session_id`      BIGINT       NOT NULL,
    `tier_id`         BIGINT       NOT NULL,
    `tier_code`       VARCHAR(64)  NOT NULL,
    `character_id`    INT          NOT NULL,
    `character_name`  VARCHAR(32)  NOT NULL,
    `grant_mode`      VARCHAR(16)  NOT NULL,
    `status`          VARCHAR(16)  NOT NULL DEFAULT 'PENDING' COMMENT 'PENDING/CLAIMED/EXPIRED',
    `mesos`           BIGINT       NOT NULL DEFAULT 0,
    `exp`             INT          NOT NULL DEFAULT 0,
    `item_id`         INT          NOT NULL DEFAULT 0,
    `item_qty`        INT          NOT NULL DEFAULT 0,
    `item2_id`        INT          NOT NULL DEFAULT 0,
    `item2_qty`       INT          NOT NULL DEFAULT 0,
    `announce_name`   TINYINT      NOT NULL DEFAULT 0,
    `created_at`      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `claimed_at`      DATETIME     NULL,
    `expire_at`       DATETIME     NULL,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_claim_once` (`session_id`, `tier_code`, `character_id`),
    KEY `idx_claim_chr_status` (`character_id`, `status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='活动奖励领取单';

-- 种子：团队对抗（INSERT IGNORE，避免 INSERT SELECT + ON DUPLICATE 语法问题）
INSERT IGNORE INTO `activity_reward_tier`
(`activity_code`,`tier_code`,`tier_name`,`priority`,`exclusive_group`,`match_json`,`grant_mode`,`mesos`,`exp`,`item_id`,`item_qty`,`announce_name`,`announce_tpl`,`enabled`)
VALUES
('coconut','PARTICIPATE','参与奖',100,NULL,'{"outcomes":["PARTICIPATED","WIN","LOSE","DRAW","COMPLETE"]}','AUTO_BAG',10000,0,0,0,0,NULL,1),
('coconut','TEAM_WIN','胜方奖',10,'result','{"outcomes":["WIN"]}','CLAIM_NPC',0,0,4031019,1,1,'[活动] 恭喜 {0} 在【{1}】中获得【{2}】！',1),
('coconut','TEAM_LOSE','败方安慰',20,'result','{"outcomes":["LOSE"]}','AUTO_BAG',5000,0,0,0,0,NULL,1),
('snowball','PARTICIPATE','参与奖',100,NULL,'{"outcomes":["PARTICIPATED","WIN","LOSE","DRAW","COMPLETE"]}','AUTO_BAG',10000,0,0,0,0,NULL,1),
('snowball','TEAM_WIN','胜方奖',10,'result','{"outcomes":["WIN"]}','CLAIM_NPC',0,0,4031019,1,1,'[活动] 恭喜 {0} 在【{1}】中获得【{2}】！',1),
('snowball','TEAM_LOSE','败方安慰',20,'result','{"outcomes":["LOSE"]}','AUTO_BAG',5000,0,0,0,0,NULL,1),
('coke_play','PARTICIPATE','参与奖',100,NULL,'{"outcomes":["PARTICIPATED","WIN","LOSE","DRAW","COMPLETE"]}','AUTO_BAG',10000,0,0,0,0,NULL,1),
('coke_play','TEAM_WIN','胜方奖',10,'result','{"outcomes":["WIN"]}','CLAIM_NPC',0,0,4031019,1,1,'[活动] 恭喜 {0} 在【{1}】中获得【{2}】！',1);

INSERT IGNORE INTO `activity_reward_tier`
(`activity_code`,`tier_code`,`tier_name`,`priority`,`exclusive_group`,`match_json`,`grant_mode`,`mesos`,`exp`,`item_id`,`item_qty`,`announce_name`,`announce_tpl`,`enabled`)
VALUES
('ox_quiz','PARTICIPATE','参与奖',100,NULL,'{"outcomes":["PARTICIPATED","WIN","LOSE","COMPLETE","ELIMINATED"]}','AUTO_BAG',8000,0,0,0,0,NULL,1),
('ox_quiz','RANK1','冠军',10,'rank','{"rankFrom":1,"rankTo":1}','CLAIM_NPC',0,0,4031019,1,1,'[活动] 恭喜 {0} 在【{1}】夺得第一！',1),
('ox_quiz','RANK23','亚军季军',20,'rank','{"rankFrom":2,"rankTo":3}','CLAIM_NPC',20000,0,0,0,0,NULL,1),
('treasure','PARTICIPATE','参与奖',100,NULL,'{"outcomes":["PARTICIPATED","COMPLETE","WIN"]}','AUTO_BAG',8000,0,0,0,0,NULL,1),
('treasure','FINDER','寻得宝藏',10,'result','{"outcomes":["COMPLETE","WIN"]}','CLAIM_NPC',0,0,4031019,1,1,'[活动] 恭喜 {0} 在【{1}】寻得宝藏！',1);

INSERT IGNORE INTO `activity_reward_tier`
(`activity_code`,`tier_code`,`tier_name`,`priority`,`exclusive_group`,`match_json`,`grant_mode`,`mesos`,`exp`,`item_id`,`item_qty`,`announce_name`,`announce_tpl`,`enabled`)
SELECT a.code, t.tier_code, t.tier_name, t.priority, t.exclusive_group, t.match_json, t.grant_mode, t.mesos, t.exp, t.item_id, t.item_qty, t.announce_name, t.announce_tpl, 1
FROM (
    SELECT 'ola_random' AS code UNION SELECT 'ola_1' UNION SELECT 'ola_2' UNION SELECT 'ola_3' UNION SELECT 'ola_4' UNION SELECT 'fitness'
) a
CROSS JOIN (
    SELECT 'PARTICIPATE' AS tier_code, '参与奖' AS tier_name, 100 AS priority, NULL AS exclusive_group,
           '{"outcomes":["PARTICIPATED","COMPLETE","WIN"]}' AS match_json, 'AUTO_BAG' AS grant_mode,
           8000 AS mesos, 0 AS exp, 0 AS item_id, 0 AS item_qty, 0 AS announce_name, NULL AS announce_tpl
    UNION ALL
    SELECT 'COMPLETE', '通关奖', 30, NULL,
           '{"outcomes":["COMPLETE","WIN"]}', 'AUTO_BAG',
           20000, 0, 0, 0, 0, NULL
    UNION ALL
    SELECT 'RANK1', '第一名', 10, 'rank',
           '{"rankFrom":1,"rankTo":1}', 'CLAIM_NPC',
           0, 0, 4031019, 1, 1, '[活动] 恭喜 {0} 在【{1}】夺得第一！'
    UNION ALL
    SELECT 'RANK23', '第二三名', 20, 'rank',
           '{"rankFrom":2,"rankTo":3}', 'CLAIM_NPC',
           30000, 0, 0, 0, 0, NULL
) t;

INSERT IGNORE INTO `activity_reward_tier`
(`activity_code`,`tier_code`,`tier_name`,`priority`,`exclusive_group`,`match_json`,`grant_mode`,`mesos`,`exp`,`item_id`,`item_qty`,`announce_name`,`announce_tpl`,`enabled`)
SELECT a.code, t.tier_code, t.tier_name, t.priority, t.exclusive_group, t.match_json, t.grant_mode, t.mesos, t.exp, t.item_id, t.item_qty, t.announce_name, t.announce_tpl, 1
FROM (
    SELECT 'jump_fitness_stage' AS code UNION SELECT 'jump_ola' UNION SELECT 'jump_forest'
    UNION SELECT 'jump_subway' UNION SELECT 'jump_volcano' UNION SELECT 'jump_patience'
    UNION SELECT 'sheep_ranch' UNION SELECT 'minigame_challenge'
) a
CROSS JOIN (
    SELECT 'PARTICIPATE' AS tier_code, '参与奖' AS tier_name, 100 AS priority, NULL AS exclusive_group,
           '{"outcomes":["PARTICIPATED","COMPLETE","WIN"]}' AS match_json, 'AUTO_BAG' AS grant_mode,
           5000 AS mesos, 0 AS exp, 0 AS item_id, 0 AS item_qty, 0 AS announce_name, NULL AS announce_tpl
    UNION ALL
    SELECT 'COMPLETE', '通关奖', 20, NULL,
           '{"outcomes":["COMPLETE","WIN"]}', 'AUTO_BAG',
           15000, 0, 0, 0, 0, NULL
) t;
