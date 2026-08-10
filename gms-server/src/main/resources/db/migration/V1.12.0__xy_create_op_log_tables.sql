-- 统一操作日志系统：日志表 + 操作类型样式绑定表
CREATE TABLE IF NOT EXISTS `xy_op_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `op_type` INT NOT NULL DEFAULT 0 COMMENT '操作类型(见 xy_op_log_type.op_type)',
    `op_type_name` VARCHAR(32) NOT NULL DEFAULT '' COMMENT '操作类型名称快照',
    `character_id` INT NOT NULL DEFAULT 0 COMMENT '角色ID',
    `character_name` VARCHAR(32) NOT NULL DEFAULT '' COMMENT '角色名',
    `account_id` INT NOT NULL DEFAULT 0 COMMENT '账号ID',
    `summary` VARCHAR(256) NOT NULL DEFAULT '' COMMENT '摘要(聊天广播内容, 如:兑换[星石*10])',
    `detail` VARCHAR(2048) NOT NULL DEFAULT '' COMMENT '完整详情(审计用)',
    `chat_type` INT NOT NULL DEFAULT 0 COMMENT '聊天样式快照(serverNotice type 值)',
    `broadcast` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已全服广播',
    `ip` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '来源IP',
    `world_channel` VARCHAR(16) NOT NULL DEFAULT '' COMMENT '世界-频道',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_op_type` (`op_type`),
    KEY `idx_character_id` (`character_id`),
    KEY `idx_character_name` (`character_name`),
    KEY `idx_account_id` (`account_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

CREATE TABLE IF NOT EXISTS `xy_op_log_type` (
    `id` INT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `op_type` INT NOT NULL COMMENT '操作类型码',
    `name` VARCHAR(32) NOT NULL COMMENT '类型名称(如:兑换)',
    `notice_tag` VARCHAR(32) NOT NULL DEFAULT '' COMMENT '公告标签(如:兑换系统), 广播时显示为【兑换系统】',
    `chat_type` INT NOT NULL DEFAULT 0 COMMENT '聊天样式: PacketCreator.serverNotice(type,msg) 的 type 值; 0=白底黑字(默认) 1=红字提示 5=蓝底白字大公告 6=白底蓝字',
    `broadcast` TINYINT NOT NULL DEFAULT 0 COMMENT '是否全服聊天广播',
    `enabled` TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序',
    `remark` VARCHAR(255) NOT NULL DEFAULT '' COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_op_type` (`op_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志类型样式绑定表';
