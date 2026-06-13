-- 卷轴分解配置表（白名单：哪些卷轴可被分解）
CREATE TABLE xy_scroll_decompose_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    scroll_id INT NOT NULL UNIQUE COMMENT '卷轴物品ID',
    scroll_name VARCHAR(100) DEFAULT '' COMMENT '卷轴名称（可为空，WZ自动识别）',
    enabled TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用(0=禁用 1=启用)',
    sort_order INT NOT NULL DEFAULT 200 COMMENT '排序号，越小越靠前',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='卷轴分解配置表';

-- 卷轴兑换配置表（碎片→卷轴，配置碎片价格）
CREATE TABLE xy_scroll_exchange_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    scroll_id INT NOT NULL UNIQUE COMMENT '卷轴物品ID',
    scroll_name VARCHAR(100) DEFAULT '' COMMENT '卷轴名称（可为空，WZ自动识别）',
    cost INT NOT NULL DEFAULT 100 COMMENT '兑换所需碎片数量',
    enabled TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用(0=禁用 1=启用)',
    sort_order INT NOT NULL DEFAULT 200 COMMENT '排序号，越小越靠前',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='卷轴兑换配置表';
