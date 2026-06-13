-- =====================================================
-- 新手礼包系统配置表
-- 支持：管理员配置礼包（物品+金币+点卷+抵用券）
--   每个角色每个礼包仅可领取一次
--   可设置等级限制
-- =====================================================

-- 礼包配置主表
CREATE TABLE IF NOT EXISTS `xy_newbie_gift_config`
(
    `id`          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
    `gift_name`   VARCHAR(128) NOT NULL COMMENT '礼包名称',
    `min_level`   INT          NOT NULL DEFAULT 1 COMMENT '最低领取等级',
    `max_level`   INT          NOT NULL DEFAULT 200 COMMENT '最高领取等级',
    `enabled`     TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '是否启用（0=禁用 1=启用）',
    `create_time` TIMESTAMP             DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` TIMESTAMP             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间'
) COMMENT '新手礼包配置主表' ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 物品奖励（每个礼包可配置多个物品）
CREATE TABLE IF NOT EXISTS `xy_newbie_gift_item`
(
    `id`          BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
    `gift_id`     BIGINT NOT NULL COMMENT '关联 xy_newbie_gift_config.id',
    `item_id`     INT    NOT NULL COMMENT '奖励物品ID',
    `quantity`    INT    NOT NULL DEFAULT 1 COMMENT '奖励数量',
    `create_time` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (`gift_id`) REFERENCES `xy_newbie_gift_config` (`id`) ON DELETE CASCADE
) COMMENT '新手礼包物品奖励表' ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 货币奖励（金币/点卷/抵用券）
CREATE TABLE IF NOT EXISTS `xy_newbie_gift_currency`
(
    `id`            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
    `gift_id`       BIGINT      NOT NULL COMMENT '关联 xy_newbie_gift_config.id',
    `currency_type` VARCHAR(16) NOT NULL COMMENT '货币类型（meso/cash/credit）',
    `amount`        INT         NOT NULL DEFAULT 0 COMMENT '数量',
    `create_time`   TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (`gift_id`) REFERENCES `xy_newbie_gift_config` (`id`) ON DELETE CASCADE
) COMMENT '新手礼包货币奖励表' ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;

-- 领取记录（每角色每礼包唯一，防止重复领取）
CREATE TABLE IF NOT EXISTS `xy_newbie_gift_record`
(
    `id`            BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '自增主键',
    `character_id`  INT       NOT NULL COMMENT '角色ID',
    `gift_id`       BIGINT    NOT NULL COMMENT '关联 xy_newbie_gift_config.id',
    `claim_time`    TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '领取时间',
    UNIQUE KEY `uk_char_gift` (`character_id`, `gift_id`),
    FOREIGN KEY (`gift_id`) REFERENCES `xy_newbie_gift_config` (`id`) ON DELETE CASCADE
) COMMENT '新手礼包领取记录表' ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4;
