-- 角色赞助双字段：总赞助（只增不减，档位领奖）+ 可消费赞助（商店扣减）
CREATE TABLE IF NOT EXISTS `xy_character_sponsor` (
    `id`              BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `character_id`    INT          NOT NULL COMMENT '角色ID（角色独立）',
    `total_sponsor`   INT          NOT NULL DEFAULT 0 COMMENT '总赞助（累计充值，只增不减，用于档位达标）',
    `spendable_sponsor` INT        NOT NULL DEFAULT 0 COMMENT '可消费赞助（充值增加、购买扣减）',
    `create_time`     DATETIME     NOT NULL COMMENT '创建时间',
    `update_time`     DATETIME     NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_character_id` (`character_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色赞助余额（总赞助+可消费赞助）';

-- 赞助档位配置
CREATE TABLE IF NOT EXISTS `xy_sponsor_config` (
    `id`          INT          NOT NULL AUTO_INCREMENT COMMENT '档位配置ID',
    `name`        VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '档位名称，如赞助满100元',
    `amount`      INT          NOT NULL COMMENT '达标所需总赞助额（元）',
    `enabled`     TINYINT      NOT NULL DEFAULT 1 COMMENT '是否启用：1启用 0停用',
    `sort_order`  INT          NOT NULL DEFAULT 0 COMMENT '排序，越小越靠前',
    `create_time` DATETIME     NOT NULL COMMENT '创建时间',
    `update_time` DATETIME     NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赞助档位配置（按总赞助达标领奖）';

-- 档位奖励明细
CREATE TABLE IF NOT EXISTS `xy_sponsor_reward` (
    `id`          INT          NOT NULL AUTO_INCREMENT COMMENT '奖励行ID',
    `config_id`   INT          NOT NULL COMMENT '所属档位配置ID',
    `type`        VARCHAR(16)  NOT NULL COMMENT '奖励类型：nx点券 / maple抵用 / meso金币 / item道具',
    `item_id`     INT          NOT NULL DEFAULT 0 COMMENT '道具ID（type=item时有效）',
    `qty`         INT          NOT NULL DEFAULT 0 COMMENT '数量',
    `create_time` DATETIME     NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_config_id` (`config_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赞助档位奖励明细';

-- 角色已领档位记录（每档限领一次）
CREATE TABLE IF NOT EXISTS `xy_sponsor_claim` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `character_id` INT          NOT NULL COMMENT '角色ID',
    `config_id`    INT          NOT NULL COMMENT '已领档位配置ID',
    `claim_time`   DATETIME     NOT NULL COMMENT '领取时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_char_config` (`character_id`, `config_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赞助档位领取记录';
