-- 通用每日/累计次数记录表(从079整合版移植，原表名bosslog)
-- 与既有的bosslog_daily/bosslog_weekly(固定枚举:ZAKUM/HORNTAIL/PINKBEAN/SCARGA/PAPULATUS，仅供远征队Boss使用)不同，
-- 本表按"角色ID+任意业务自定义字符串key"记录次数，用于副本每日进入次数限制等通用场景(如"每日挖矿地图")
CREATE TABLE IF NOT EXISTS `bosslog`
(
    `bosslogid`   BIGINT(20)  NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `characterid` INT(11)     NOT NULL COMMENT '角色ID(对应characters.id)',
    `bossid`      VARCHAR(64) NOT NULL COMMENT '业务自定义记录标识(通用key，如副本/活动名称，非固定枚举)',
    `count`       INT(11)     NOT NULL DEFAULT 0 COMMENT '当前计数(如已进入次数)',
    `type`        INT(11)     NOT NULL DEFAULT 0 COMMENT '记录类型，由业务自行定义含义(默认0)',
    `time`        TIMESTAMP   NULL     DEFAULT NULL COMMENT '最近一次更新时间，用于判断是否跨天需重置计数',
    PRIMARY KEY (`bosslogid`),
    UNIQUE KEY `uk_character_bossid` (`characterid`, `bossid`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COMMENT = '通用每日/累计次数记录表(按角色+自定义key计数，从079移植)';
