-- ==========================================
-- 师徒系统数据库表
-- 功能：支持创建师门、收徒、出师、师徒奖励等
-- ==========================================

-- 1. 师徒系统配置表
CREATE TABLE IF NOT EXISTS xy_mentor_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    config_key VARCHAR(64) NOT NULL COMMENT '配置键（create_master_level=创建师门所需等级, max_disciples=最大收徒数, max_be_disciple_level=可拜师最高等级, graduate_level=出师所需等级）',
    config_value VARCHAR(255) NOT NULL COMMENT '配置值',
    description VARCHAR(255) DEFAULT '' COMMENT '配置说明',
    enabled INT DEFAULT 1 COMMENT '是否启用（0=禁用 1=启用）',
    UNIQUE INDEX idx_config_key (config_key)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='师徒系统配置表';

-- 插入默认配置
INSERT INTO xy_mentor_config (config_key, config_value, description, enabled) VALUES
('create_master_level', '70', '创建师门所需的最低等级', 1),
('max_disciples', '5', '每个师父最多可收的徒弟数量', 1),
('max_be_disciple_level', '50', '可以拜师的最高等级（超过此等级不可拜师）', 1),
('graduate_level', '70', '徒弟出师所需的最低等级', 1);

-- 2. 师门表（记录已创建师门的师父）
CREATE TABLE IF NOT EXISTS xy_mentor_master (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    character_id INT NOT NULL COMMENT '师父角色ID（FK → characters.id）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建师门时间',
    UNIQUE INDEX idx_character_id (character_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='师门表（记录哪些角色已创建师门成为师父）';

-- 3. 师徒关系表
CREATE TABLE IF NOT EXISTS xy_mentor_relationship (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    master_character_id INT NOT NULL COMMENT '师父角色ID（FK → characters.id）',
    disciple_character_id INT NOT NULL COMMENT '徒弟角色ID（FK → characters.id）',
    status INT DEFAULT 0 COMMENT '关系状态（0=在师门中 1=已出师 2=已退出）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '拜师时间',
    graduate_time DATETIME DEFAULT NULL COMMENT '出师时间',
    UNIQUE INDEX idx_disciple (disciple_character_id),
    INDEX idx_master (master_character_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='师徒关系表';

-- 4. 出师奖励配置表
CREATE TABLE IF NOT EXISTS xy_mentor_graduation_reward (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    reward_type INT NOT NULL COMMENT '奖励类型（0=师父奖励 1=徒弟奖励）',
    meso INT DEFAULT 0 COMMENT '金币奖励数量',
    nx_credit INT DEFAULT 0 COMMENT '点卷奖励（NX_CREDIT=1）',
    maple_point INT DEFAULT 0 COMMENT '抵用券奖励（MAPLE_POINT=2）',
    nx_prepaid INT DEFAULT 0 COMMENT '信用券奖励（NX_PREPAID=4）',
    enabled INT DEFAULT 1 COMMENT '是否启用（0=禁用 1=启用）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出师奖励配置表';

-- 插入默认出师奖励（师父和徒弟各一份默认配置）
INSERT INTO xy_mentor_graduation_reward (reward_type, meso, nx_credit, maple_point, nx_prepaid, enabled) VALUES
(0, 5000000, 0, 5000, 0, 1),
(1, 2000000, 0, 2000, 0, 1);

-- 5. 出师奖励道具表
CREATE TABLE IF NOT EXISTS xy_mentor_graduation_reward_item (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    reward_id BIGINT NOT NULL COMMENT '关联出师奖励ID（FK → xy_mentor_graduation_reward.id）',
    item_id INT NOT NULL COMMENT '道具ID',
    quantity INT DEFAULT 1 COMMENT '发放数量',
    INDEX idx_reward_id (reward_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='出师奖励道具表';
