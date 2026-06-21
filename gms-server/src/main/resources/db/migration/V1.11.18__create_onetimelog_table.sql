-- 永久一次性记录表（不会跨天重置，用于一次性奖励/成就等永久标记）
CREATE TABLE IF NOT EXISTS `onetimelog`
(
    `id`          BIGINT(20)  NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `characterid` INT(11)     NOT NULL COMMENT '角色ID(对应characters.id)',
    `logid`       VARCHAR(64) NOT NULL COMMENT '业务自定义记录标识(永久key，如"怪怪卡片-射手村")',
    `count`       INT(11)     NOT NULL DEFAULT 1 COMMENT '当前计数(默认1，作为标记位或累积计数)',
    `time`        TIMESTAMP   NULL     DEFAULT CURRENT_TIMESTAMP COMMENT '创建/更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_character_logid` (`characterid`, `logid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '永久一次性记录表(按角色+自定义key计数，不会跨天重置)';
