-- CDK兑换码系统：配置主表
CREATE TABLE IF NOT EXISTS `xy_cdk_config` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `code` VARCHAR(64) NOT NULL COMMENT 'CDK兑换码（唯一）',
    `batch_no` VARCHAR(64) DEFAULT NULL COMMENT '批次号（批量生成时共用，用于分组管理）',
    `type` INT NOT NULL DEFAULT 1 COMMENT 'CDK类型（1=普通 2=批量生成）',
    `nx_credit` INT DEFAULT 0 COMMENT '点券数量（NX_CREDIT=1，0表示不发放此类奖励）',
    `nx_prepaid` INT DEFAULT 0 COMMENT '抵用券数量（NX_PREPAID=4，0表示不发放此类奖励）',
    `meso` INT DEFAULT 0 COMMENT '金币数量（0表示不发放此类奖励）',
    `sponsor` INT DEFAULT 0 COMMENT '赞助金额（预留字段，用于充值赞助标识）',
    `max_use_count` INT DEFAULT 1 COMMENT '最大使用次数（1=单次使用，大于1=可多人共用）',
    `used_count` INT DEFAULT 0 COMMENT '已使用次数（兑换成功一次+1）',
    `expire_time` DATETIME DEFAULT NULL COMMENT '过期时间（NULL表示永不过期）',
    `enabled` INT DEFAULT 1 COMMENT '是否启用（0=禁用 1=启用）',
    `comment` VARCHAR(255) DEFAULT NULL COMMENT '备注说明（记录CDK用途或发放原因）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_code` (`code`),
    INDEX `idx_batch_no` (`batch_no`),
    INDEX `idx_expire_time` (`expire_time`),
    INDEX `idx_enabled` (`enabled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CDK兑换码配置表（存储CDK兑换码及奖励配置）';

-- CDK道具奖励子表（一个CDK可配置多个道具）
CREATE TABLE IF NOT EXISTS `xy_cdk_item` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `cdk_id` BIGINT NOT NULL COMMENT '关联CDK配置ID（FK→xy_cdk_config.id）',
    `item_id` INT NOT NULL COMMENT '道具ID（对应wz中的物品ID）',
    `quantity` INT DEFAULT 1 COMMENT '发放数量',
    INDEX `idx_cdk_id` (`cdk_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CDK道具奖励子表（存储每个CDK对应的道具奖励列表）';

-- CDK兑换日志表（记录所有兑换尝试，用于审计和反滥用检测）
CREATE TABLE IF NOT EXISTS `xy_cdk_log` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `cdk_id` BIGINT DEFAULT NULL COMMENT '关联CDK配置ID（FK→xy_cdk_config.id，码不存在时为NULL）',
    `code` VARCHAR(64) NOT NULL COMMENT 'CDK兑换码（冗余存储便于快速查询）',
    `player_name` VARCHAR(32) DEFAULT NULL COMMENT '兑换玩家名称',
    `player_id` INT DEFAULT NULL COMMENT '兑换玩家ID',
    `account_name` VARCHAR(32) DEFAULT NULL COMMENT '兑换账号名称',
    `account_id` INT DEFAULT NULL COMMENT '兑换账号ID',
    `ip` VARCHAR(64) DEFAULT NULL COMMENT '兑换时客户端IP地址',
    `result` INT NOT NULL COMMENT '兑换结果（0=成功 1=码不存在 2=已过期 3=已达使用上限 4=已禁用 5=背包已满 6=系统错误）',
    `result_msg` VARCHAR(255) DEFAULT NULL COMMENT '结果描述（错误时记录具体原因）',
    `detail` TEXT DEFAULT NULL COMMENT '兑换明细（JSON格式，成功时记录具体发放内容，用于审计对账）',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '兑换时间',
    INDEX `idx_cdk_id` (`cdk_id`),
    INDEX `idx_code` (`code`),
    INDEX `idx_player_name` (`player_name`),
    INDEX `idx_player_id` (`player_id`),
    INDEX `idx_account_id` (`account_id`),
    INDEX `idx_ip` (`ip`),
    INDEX `idx_result` (`result`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='CDK兑换日志表（记录所有兑换尝试成功和失败，用于审计和反滥用检测）';
