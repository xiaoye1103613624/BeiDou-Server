-- ============================================
-- 仓库管理系统建表
-- ============================================

-- 仓库物品配置表（白名单：允许存入仓库的物品列表）
CREATE TABLE IF NOT EXISTS xy_warehouse_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    item_id INT NOT NULL COMMENT '物品ID',
    item_name VARCHAR(128) DEFAULT NULL COMMENT '物品名称（可为空，管理员手动输入，便于识别）',
    inventory_type TINYINT NOT NULL COMMENT '物品栏类型(1=装备 2=消耗 3=设置 4=其他 5=现金)',
    enabled TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否启用(0=禁用 1=启用)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_item_id (item_id),
    KEY idx_inventory_type (inventory_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库物品配置表';

-- 仓库物品存储表（实际存放的物品记录）
CREATE TABLE IF NOT EXISTS xy_warehouse_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    account_id INT NOT NULL COMMENT '账号ID',
    character_id INT NOT NULL COMMENT '存入角色ID',
    item_id INT NOT NULL COMMENT '物品ID',
    inventory_type TINYINT NOT NULL COMMENT '物品栏类型(1=装备 2=消耗 3=设置 4=其他 5=现金)',
    quantity INT NOT NULL DEFAULT 1 COMMENT '存放数量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    KEY idx_account_id (account_id),
    KEY idx_character_id (character_id),
    KEY idx_item_id (item_id),
    KEY idx_inventory_type (inventory_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='仓库物品存储表';
