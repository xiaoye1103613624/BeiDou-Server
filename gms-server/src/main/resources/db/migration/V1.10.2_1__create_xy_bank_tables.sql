-- ============================================
-- 银行系统建表
-- ============================================

-- 银行账户表（角色隔离，每个角色独立开户）
CREATE TABLE IF NOT EXISTS xy_bank_account (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '银行账户ID（主键，自增）',
    character_id INT NOT NULL COMMENT '所属角色ID',
    password VARCHAR(100) NOT NULL COMMENT '账户密码（BCrypt加密存储）',
    balance BIGINT NOT NULL DEFAULT 0 COMMENT '账户余额（金币）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '开户时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    UNIQUE KEY uk_character_id (character_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='银行账户表';

-- 银行操作日志表（存入/取出/转账记录）
CREATE TABLE IF NOT EXISTS xy_bank_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '日志ID（主键，自增）',
    bank_id BIGINT NOT NULL COMMENT '银行账户ID（关联xy_bank_account.id）',
    type TINYINT NOT NULL COMMENT '操作类型(1=存入 2=取出 3=转账)',
    from_character_id INT NOT NULL COMMENT '操作发起角色ID（转账时为转出方）',
    to_character_id INT DEFAULT NULL COMMENT '对方角色ID（仅转账类型有值，存入/取出为空）',
    amount BIGINT NOT NULL COMMENT '操作金额（不含手续费的本金）',
    fee BIGINT NOT NULL DEFAULT 0 COMMENT '手续费（存入5%，转账10%）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    KEY idx_bank_id (bank_id),
    KEY idx_from_character_id (from_character_id),
    KEY idx_to_character_id (to_character_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='银行操作日志表';
