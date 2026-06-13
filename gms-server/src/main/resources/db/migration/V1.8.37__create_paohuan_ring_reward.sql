-- ============================================
-- 跑环每环随机奖励池
-- ============================================

-- 每环随机奖励配置表（完成每一环时随机获取 ~3 种物品，数量随机 1~max）
CREATE TABLE IF NOT EXISTS xy_paohuan_ring_reward (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    item_id INT NOT NULL COMMENT '奖励道具ID(0=金币)',
    min_quantity INT NOT NULL DEFAULT 1 COMMENT '最小随机数量',
    max_quantity INT NOT NULL DEFAULT 1 COMMENT '最大随机数量',
    weight INT NOT NULL DEFAULT 1 COMMENT '权重（越高越容易抽中，0=禁用）',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序顺序（升序）',
    enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用(0=禁用 1=启用)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_item_id (item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='跑环每环随机奖励池';
