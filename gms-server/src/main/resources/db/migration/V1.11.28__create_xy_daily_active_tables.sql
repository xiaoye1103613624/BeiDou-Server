-- 每日活跃系统：任务配置表 + 角色每日进度表
-- 进度表采用"懒重置"方案(与 bosslog/onetimelog 表一致的约定)：每次读写时比对 log_date 是否还是今天，
-- 不是则视为0并刷新日期，不需要额外的每日0点清空定时任务。

CREATE TABLE `xy_daily_active_task` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `task_key` VARCHAR(64) NOT NULL COMMENT '任务标识(唯一)，供代码/脚本按key调用，如 zhuangbei_zhajuan',
    `task_name` VARCHAR(64) NOT NULL COMMENT '任务展示名称，如 装备砸卷',
    `target_count` INT NOT NULL DEFAULT 1 COMMENT '完成该任务所需的进度次数',
    `reward_meso` BIGINT NOT NULL DEFAULT 0 COMMENT '达成target_count后可领取的金币奖励',
    `reward_item_id` INT NOT NULL DEFAULT 0 COMMENT '达成后可领取的奖励物品ID，0表示无物品奖励',
    `reward_item_count` INT NOT NULL DEFAULT 0 COMMENT '奖励物品数量',
    `extra_config` VARCHAR(255) DEFAULT NULL COMMENT '任务专用扩展配置(纯文本)，如野外精英任务在此存逗号分隔的怪物ID列表',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '菜单显示顺序，越小越靠前',
    `enabled` TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用：0=禁用(不在菜单显示，事件钩子也跳过累计) 1=启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_task_key` (`task_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日活跃-任务配置表';

CREATE TABLE `xy_daily_active_progress` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `character_id` INT NOT NULL COMMENT '角色ID',
    `task_key` VARCHAR(64) NOT NULL COMMENT '任务标识，对应 xy_daily_active_task.task_key',
    `progress` INT NOT NULL DEFAULT 0 COMMENT '当日累计进度次数(跨天后懒重置为0)',
    `log_date` DATE NOT NULL COMMENT '本条进度对应的自然日，用于懒重置判断',
    `claimed` TINYINT NOT NULL DEFAULT 0 COMMENT '当日该任务奖励是否已领取：0=未领取 1=已领取(随log_date懒重置)',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_character_task` (`character_id`, `task_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='每日活跃-角色每日任务进度表';

-- 按截图数据预置13项任务(参照物品/奖励暂用占位，enabled先全部开启，无对应钩子的任务仅能通过脚本手动调用累计)
INSERT INTO `xy_daily_active_task`
(`task_key`, `task_name`, `target_count`, `reward_meso`, `reward_item_id`, `reward_item_count`, `extra_config`, `sort_order`, `enabled`)
VALUES
('zhuangbei_zhajuan', '装备砸卷', 1, 500000, 0, 0, NULL, 10, 1),
('face_expression', '使用聊天表情', 1, 300000, 0, 0, NULL, 20, 1),
('city_defense', '守护城镇', 1, 500000, 0, 0, NULL, 30, 1),
('pq_clear', '通关副本', 10, 1000000, 0, 0, NULL, 40, 1),
('laugh', '笑一笑，十年少', 1, 300000, 0, 0, NULL, 50, 1),
('bean_machine', '打豆豆机', 1, 300000, 0, 0, NULL, 60, 1),
('join_event', '参加活动', 1, 500000, 0, 0, NULL, 70, 1),
('minigame', '休闲娱乐', 1, 300000, 0, 0, NULL, 80, 1),
('challenge_boss', '挑战BOSS', 5, 1000000, 0, 0, NULL, 90, 1),
('kill_elite', '击杀野外精英', 5, 800000, 0, 0, '0' /* TODO: 改成实际野外精英怪物ID,逗号分隔，如 8800000,8800001 */, 100, 1),
('cash_shop_buy', '商城购买道具', 1, 500000, 0, 0, NULL, 110, 1),
('daily_loop', '每日跑环', 10, 1000000, 0, 0, NULL, 120, 1),
('online_reward', '在线奖励', 5, 1000000, 0, 0, NULL, 130, 1);
