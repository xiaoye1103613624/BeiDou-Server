-- ============================================
-- 等级奖励系统 建表SQL
-- 请手动在数据库中执行此脚本
-- ============================================

-- 等级奖励配置主表
CREATE TABLE IF NOT EXISTS xy_level_reward (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    level INT NOT NULL COMMENT '要求等级',
    meso INT DEFAULT 0 COMMENT '金币奖励',
    nx_credit INT DEFAULT 0 COMMENT '点卷（NX_CREDIT=1）',
    maple_point INT DEFAULT 0 COMMENT '抵用券（MAPLE_POINT=2）',
    nx_prepaid INT DEFAULT 0 COMMENT '信用券（NX_PREPAID=4）',
    enabled INT DEFAULT 1 COMMENT '是否启用（0=禁用 1=启用）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='等级奖励配置表';

-- 等级奖励道具子表
CREATE TABLE IF NOT EXISTS xy_level_reward_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    reward_id BIGINT NOT NULL COMMENT '关联等级奖励ID（FK → xy_level_reward.id）',
    item_id INT NOT NULL COMMENT '道具ID',
    quantity INT DEFAULT 1 COMMENT '发放数量',
    INDEX idx_reward_id (reward_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='等级奖励道具表';
