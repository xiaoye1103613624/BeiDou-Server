-- ============================================
-- 跑环系统建表（物品池 + 里程碑奖励）
-- ============================================

-- 跑环物品池配置表
CREATE TABLE IF NOT EXISTS xy_paohuan_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    item_id INT NOT NULL COMMENT '要求的物品ID',
    quantity INT NOT NULL DEFAULT 1 COMMENT '要求的数量',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序顺序（升序）',
    enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用(0=禁用 1=启用)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_item_id (item_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='跑环物品池配置表';

-- 跑环里程碑奖励表
CREATE TABLE IF NOT EXISTS xy_paohuan_reward (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    ring_count INT NOT NULL COMMENT '完成第几环时触发奖励(如5=完成5环后)',
    reward_desc VARCHAR(100) DEFAULT '' COMMENT '奖励描述（如"完成5环奖励"）',
    item_id INT NOT NULL DEFAULT 0 COMMENT '奖励道具ID(0=金币奖励)',
    quantity INT NOT NULL DEFAULT 1 COMMENT '奖励数量',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '同环内排序',
    KEY idx_ring_count (ring_count)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='跑环里程碑奖励表';
