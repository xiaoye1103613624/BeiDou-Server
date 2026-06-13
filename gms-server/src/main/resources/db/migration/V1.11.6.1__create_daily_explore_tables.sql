-- 每日探索系统：地图池、每轮随机奖励、完成奖励
-- 参考 paohuan 模式（V1.8.35+V1.8.37）

-- 每日探索地图池（每轮随机选择目标地图）
CREATE TABLE IF NOT EXISTS `xy_daily_explore_map` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `map_id` INT NOT NULL COMMENT '目标地图ID',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序顺序（升序）',
    `enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用(0=禁用 1=启用)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY `uk_map_id` (`map_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日探索地图池表';

-- 每日探索每轮随机奖励池（完成每轮后按权重随机抽取）
CREATE TABLE IF NOT EXISTS `xy_daily_explore_reward` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `item_id` INT NOT NULL COMMENT '奖励道具ID(0=金币)',
    `min_quantity` INT NOT NULL DEFAULT 1 COMMENT '最小随机数量',
    `max_quantity` INT NOT NULL DEFAULT 1 COMMENT '最大随机数量',
    `weight` INT NOT NULL DEFAULT 1 COMMENT '选中权重（越高越容易抽中，0=禁用）',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序顺序（升序）',
    `enabled` TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用(0=禁用 1=启用)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日探索每轮随机奖励池';

-- 每日探索完成奖励（完成指定次数后触发，里程碑式奖励）
CREATE TABLE IF NOT EXISTS `xy_daily_explore_final_reward` (
    `id` BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    `explore_count` INT NOT NULL COMMENT '完成第几次探索时触发奖励',
    `reward_desc` VARCHAR(100) DEFAULT '' COMMENT '奖励描述文案',
    `item_id` INT NOT NULL DEFAULT 0 COMMENT '奖励道具ID(0=金币)',
    `quantity` INT NOT NULL DEFAULT 1 COMMENT '奖励数量',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序顺序（升序）',
    KEY `idx_explore_count` (`explore_count`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日探索完成奖励表';
