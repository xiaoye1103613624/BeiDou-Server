-- ============================================
-- 每日副本系统重构：增加地图名称、完成次数、每日奖励、VIP配置
-- ============================================

-- 1. 修改每日副本配置表：增加地图名称和每日需完成次数
ALTER TABLE xy_daily_dungeon_config
    ADD COLUMN map_name VARCHAR(100) DEFAULT '' COMMENT '地图名称（WZ自动解析，展示用）' AFTER map_id,
    ADD COLUMN complete_count INT NOT NULL DEFAULT 3 COMMENT '每日需完成次数' AFTER map_name;

-- 2. 每日副本完成奖励表（所有副本完成后可领取的额外奖励）
CREATE TABLE IF NOT EXISTS xy_daily_dungeon_daily_reward (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    item_id INT NOT NULL COMMENT '奖励道具ID（0=金币）',
    quantity INT NOT NULL DEFAULT 1 COMMENT '奖励数量',
    reward_desc VARCHAR(100) DEFAULT '' COMMENT '奖励描述',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序顺序（升序）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日副本完成奖励表（所有副本完成后领取）';

-- 3. 每日副本VIP物品配置表（玩家持有指定物品可解锁VIP功能，如直接传送）
CREATE TABLE IF NOT EXISTS xy_daily_dungeon_vip_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    item_id INT NOT NULL COMMENT 'VIP物品ID（玩家持有此物品可启用VIP功能）',
    description VARCHAR(100) DEFAULT '' COMMENT 'VIP功能描述',
    enabled TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用（0=禁用 1=启用）',
    sort_order INT NOT NULL DEFAULT 0 COMMENT '排序顺序（升序）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日副本VIP物品配置表';
