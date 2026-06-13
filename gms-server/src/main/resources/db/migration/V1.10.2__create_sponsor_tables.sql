-- 赞助系统：奖励配置表
CREATE TABLE IF NOT EXISTS `xy_sponsor_config` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `name` VARCHAR(64) NOT NULL COMMENT '配置名称（如"初级赞助""高级赞助"，用于游戏内展示）',
    `amount` INT NOT NULL COMMENT '赞助金额阈值（累计达到此金额后可领取）',
    `rewards_json` TEXT NOT NULL COMMENT '奖励JSON：[{type:"item"/"nx"/"meso", id:物品ID, qty:数量}]',
    `enabled` INT DEFAULT 1 COMMENT '是否启用（0=禁用 1=启用）',
    `comment` VARCHAR(255) DEFAULT NULL COMMENT '备注说明',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    UNIQUE KEY `uk_amount` (`amount`),
    INDEX `idx_enabled` (`enabled`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赞助奖励配置表（存储各赞助金额档位对应的奖励）';

-- 赞助系统：玩家赞助记录表（角色级隔离）
CREATE TABLE IF NOT EXISTS `xy_sponsor_record` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `player_id` INT NOT NULL COMMENT '角色ID',
    `player_name` VARCHAR(32) DEFAULT NULL COMMENT '角色名称',
    `account_id` INT DEFAULT NULL COMMENT '账号ID',
    `account_name` VARCHAR(32) DEFAULT NULL COMMENT '账号名称',
    `total_sponsor` INT DEFAULT 0 COMMENT '累计赞助金额',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '首次赞助时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    UNIQUE KEY `uk_player_id` (`player_id`),
    INDEX `idx_account_id` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩家赞助记录表（角色级隔离，存储每个角色的累计赞助金额）';

-- 赞助系统：赞助日志表
CREATE TABLE IF NOT EXISTS `xy_sponsor_log` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    `player_id` INT DEFAULT NULL COMMENT '角色ID',
    `player_name` VARCHAR(32) DEFAULT NULL COMMENT '角色名称',
    `account_id` INT DEFAULT NULL COMMENT '账号ID',
    `type` INT NOT NULL COMMENT '赞助类型（1=CDK兑换 2=管理员添加）',
    `amount` INT NOT NULL COMMENT '变动金额',
    `detail` VARCHAR(512) DEFAULT NULL COMMENT '变动详情',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    INDEX `idx_player_id` (`player_id`),
    INDEX `idx_account_id` (`account_id`),
    INDEX `idx_type` (`type`),
    INDEX `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赞助日志表（记录所有赞助金额变动，用于审计对账）';
