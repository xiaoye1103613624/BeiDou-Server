-- ============================================
-- 玩具收集系统 建表SQL
-- ============================================

-- 玩具收集分类配置表
CREATE TABLE IF NOT EXISTS xy_toy_collection_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    name VARCHAR(64) NOT NULL COMMENT '分类名称',
    icon VARCHAR(128) DEFAULT '' COMMENT '图标标识',
    sort_order INT DEFAULT 0 COMMENT '排序序号（升序）',
    enabled TINYINT(1) DEFAULT 1 COMMENT '是否启用（0=禁用 1=启用）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩具收集分类配置表';

-- 玩具收集物品配置表
CREATE TABLE IF NOT EXISTS xy_toy_collection_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    category_id BIGINT NOT NULL COMMENT '所属分类ID（FK → xy_toy_collection_category.id）',
    item_id INT NOT NULL COMMENT '收集物品ID',
    required_quantity INT DEFAULT 1 COMMENT '需要收集的数量',
    reward_item_id INT DEFAULT 0 COMMENT '奖励物品ID（0=无奖励）',
    reward_quantity INT DEFAULT 1 COMMENT '奖励物品数量',
    sort_order INT DEFAULT 0 COMMENT '排序序号（升序）',
    enabled TINYINT(1) DEFAULT 1 COMMENT '是否启用（0=禁用 1=启用）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_category_id (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩具收集物品配置表';

-- 玩具收集进度表（角色隔离）
CREATE TABLE IF NOT EXISTS xy_toy_collection_progress (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    character_id INT NOT NULL COMMENT '角色ID',
    item_config_id BIGINT NOT NULL COMMENT '关联收集物品配置ID（FK → xy_toy_collection_item.id）',
    submitted_quantity INT DEFAULT 0 COMMENT '已提交数量',
    reward_claimed TINYINT(1) DEFAULT 0 COMMENT '奖励是否已领取（0=未领 1=已领）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE INDEX idx_char_item (character_id, item_config_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='玩具收集进度表';
