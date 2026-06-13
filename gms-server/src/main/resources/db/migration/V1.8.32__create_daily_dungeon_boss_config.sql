-- ============================================
-- 每日副本 & 每日Boss 配置系统建表
-- ============================================

-- 每日副本配置主表
CREATE TABLE IF NOT EXISTS xy_daily_dungeon_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    dungeon_key VARCHAR(100) NOT NULL COMMENT '副本键值（脚本唯一标识，如每日_月妙副本）',
    dungeon_name VARCHAR(100) NOT NULL COMMENT '副本显示名称',
    map_id INT NOT NULL COMMENT '副本地图ID',
    sweep_item_id INT NOT NULL DEFAULT 0 COMMENT '扫荡券道具ID（0=不可扫荡）',
    sweep_item_cost INT NOT NULL DEFAULT 1 COMMENT '单次扫荡消耗道具数量',
    max_sweep INT NOT NULL DEFAULT 0 COMMENT '每日扫荡上限（0=不可扫荡）',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序顺序（升序）',
    enabled TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用（0=禁用 1=启用）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日副本配置表';

-- 每日副本里程碑奖励子表
CREATE TABLE IF NOT EXISTS xy_daily_dungeon_reward (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    config_id BIGINT NOT NULL COMMENT '关联副本配置ID（FK → xy_daily_dungeon_config.id）',
    complete_count INT NOT NULL COMMENT '需完成次数（达到此次数可领取）',
    reward_desc VARCHAR(100) DEFAULT '' COMMENT '里程碑描述（如"完成3次"）',
    item_id INT NOT NULL COMMENT '奖励道具ID',
    quantity INT NOT NULL DEFAULT 1 COMMENT '发放数量',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '同里程碑内排序',
    INDEX idx_config_id (config_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日副本里程碑奖励表';

-- 每日Boss配置主表
CREATE TABLE IF NOT EXISTS xy_daily_boss_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    boss_key VARCHAR(100) NOT NULL COMMENT 'Boss键值（脚本唯一标识，如每日_扎昆）',
    boss_name VARCHAR(100) NOT NULL COMMENT 'Boss显示名称',
    boss_mob_id INT NOT NULL COMMENT 'Boss怪物ID',
    sweep_item_id INT NOT NULL DEFAULT 0 COMMENT '扫荡券道具ID（0=不可扫荡）',
    sweep_item_cost INT NOT NULL DEFAULT 1 COMMENT '单次扫荡消耗道具数量',
    max_sweep INT NOT NULL DEFAULT 0 COMMENT '每日扫荡上限（0=不可扫荡）',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序顺序（升序）',
    enabled TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用（0=禁用 1=启用）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日Boss配置表';

-- 每日Boss里程碑奖励子表
CREATE TABLE IF NOT EXISTS xy_daily_boss_reward (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    config_id BIGINT NOT NULL COMMENT '关联Boss配置ID（FK → xy_daily_boss_config.id）',
    complete_count INT NOT NULL COMMENT '需完成次数（达到此次数可领取）',
    reward_desc VARCHAR(100) DEFAULT '' COMMENT '里程碑描述（如"完成5次"）',
    item_id INT NOT NULL COMMENT '奖励道具ID',
    quantity INT NOT NULL DEFAULT 1 COMMENT '发放数量',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '同里程碑内排序',
    INDEX idx_boss_config_id (config_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日Boss里程碑奖励表';
