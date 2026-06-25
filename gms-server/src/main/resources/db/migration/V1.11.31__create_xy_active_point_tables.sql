-- 每日活跃度积分系统：在原"每日活跃"13项任务基础上叠加的积分阶梯奖励
-- 积分本身=当日全部任务完成次数之和(由xy_daily_active_progress.progress按角色+log_date求和得出，不重复存储)
-- 本表只记录"每日4个积分阶梯(1/5/10/20点)是否已领取"及"月度3个阶梯(200/300/500点)是否已领取"

CREATE TABLE xy_active_point_daily_claim
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    character_id  INT          NOT NULL COMMENT '角色ID',
    log_date      DATE         NOT NULL COMMENT '记录日期，用于跨天懒重置(与log_date不同即视为新的一天，4个档位重新可领)',
    tier1_claimed TINYINT      NOT NULL DEFAULT 0 COMMENT '每日积分阶梯1(1点，奖励100抵用券)是否已领取：0=未领取 1=已领取',
    tier2_claimed TINYINT      NOT NULL DEFAULT 0 COMMENT '每日积分阶梯2(5点，奖励200抵用券)是否已领取：0=未领取 1=已领取',
    tier3_claimed TINYINT      NOT NULL DEFAULT 0 COMMENT '每日积分阶梯3(10点，奖励300抵用券)是否已领取：0=未领取 1=已领取',
    tier4_claimed TINYINT      NOT NULL DEFAULT 0 COMMENT '每日积分阶梯4(20点，奖励600点券)是否已领取：0=未领取 1=已领取',
    create_time   DATETIME     NOT NULL COMMENT '创建时间',
    update_time   DATETIME     NOT NULL COMMENT '更新时间',
    UNIQUE KEY uk_character_date (character_id, log_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日活跃度积分阶梯领取记录(懒重置)';

CREATE TABLE xy_active_point_monthly
(
    id            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    character_id  INT          NOT NULL COMMENT '角色ID',
    year_month    CHAR(7)      NOT NULL COMMENT '所属年月，格式yyyy-MM，用于跨月懒重置',
    total_points  INT          NOT NULL DEFAULT 0 COMMENT '本月累计活跃度积分(每次领取每日阶梯奖励时，把该阶梯的点数阈值叠加进来)',
    tier1_claimed TINYINT      NOT NULL DEFAULT 0 COMMENT '月度积分阶梯1(200点，奖励2000点券)是否已领取：0=未领取 1=已领取',
    tier2_claimed TINYINT      NOT NULL DEFAULT 0 COMMENT '月度积分阶梯2(300点，奖励6000点券)是否已领取：0=未领取 1=已领取',
    tier3_claimed TINYINT      NOT NULL DEFAULT 0 COMMENT '月度积分阶梯3(500点，奖励10000点券)是否已领取：0=未领取 1=已领取',
    create_time   DATETIME     NOT NULL COMMENT '创建时间',
    update_time   DATETIME     NOT NULL COMMENT '更新时间',
    UNIQUE KEY uk_character_month (character_id, year_month)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='月度活跃度积分累计与阶梯领取记录(懒重置)';
