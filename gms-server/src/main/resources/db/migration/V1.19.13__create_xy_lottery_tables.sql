-- 独立抽奖管理（与百宝箱 gachapon_* 分离）
CREATE TABLE IF NOT EXISTS `xy_lottery_machine` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `npc_id` INT NOT NULL COMMENT '绑定 NPC ID，唯一',
    `name` VARCHAR(64) NOT NULL DEFAULT '' COMMENT '展示名称',
    `comment` VARCHAR(255) NULL DEFAULT NULL COMMENT '备注',
    `enabled` TINYINT NOT NULL DEFAULT 1 COMMENT '0禁用 1启用',
    `multi_draws` VARCHAR(64) NOT NULL DEFAULT '[1,10]' COMMENT '几连抽 JSON，如 [1,10,20]',
    `cost_type` VARCHAR(16) NOT NULL DEFAULT 'NX' COMMENT 'MESO/ITEM/NX/MAPLE_POINT',
    `cost_item_id` INT NULL DEFAULT NULL COMMENT 'ITEM 消耗时的道具 ID',
    `cost_amount` BIGINT NOT NULL DEFAULT 0 COMMENT '一连消耗量；N连=amount*N',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_npc_id` (`npc_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='抽奖机（按 NPC）';

CREATE TABLE IF NOT EXISTS `xy_lottery_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `npc_id` INT NOT NULL COMMENT '所属抽奖 NPC',
    `item_id` INT NOT NULL COMMENT '物品 ID',
    `quantity` INT NOT NULL DEFAULT 1 COMMENT '单次数量',
    `weight` INT NOT NULL DEFAULT 1 COMMENT '权重（相对同 NPC 归一）',
    `announce` TINYINT NOT NULL DEFAULT 0 COMMENT '是否广播',
    `announce_channel` INT NOT NULL DEFAULT 6 COMMENT 'serverNotice type',
    `announce_banner` TINYINT NOT NULL DEFAULT 0 COMMENT '是否横幅广播',
    `announce_label` VARCHAR(64) NULL DEFAULT NULL COMMENT '广播前缀',
    `random_stats` TINYINT NOT NULL DEFAULT 0 COMMENT '装备属性是否波动',
    `untradeable` TINYINT NOT NULL DEFAULT 0 COMMENT '不可交易',
    `account_bound` TINYINT NOT NULL DEFAULT 0 COMMENT '固有道具(账号绑定)',
    `unique_equip` TINYINT NOT NULL DEFAULT 0 COMMENT '固有装备(LOCK)',
    `enabled` TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用',
    `from_comment` TINYINT NOT NULL DEFAULT 0 COMMENT '是否来自脚本注释行',
    `item_valid` TINYINT NOT NULL DEFAULT 1 COMMENT 'WZ 是否存在',
    `item_type` TINYINT NOT NULL DEFAULT 4 COMMENT '1特殊 2装备 3消耗 4其它',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '同类型内排序，越小越前',
    PRIMARY KEY (`id`),
    KEY `idx_npc_sort` (`npc_id`, `item_type`, `sort_order`, `id`),
    KEY `idx_item_id` (`item_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='抽奖奖品';

-- 金猪默认机：NPC 9310022，点卷 10000/次，默认 1/10 连（奖池由管理端导入 303）
INSERT INTO `xy_lottery_machine`
(`npc_id`, `name`, `comment`, `enabled`, `multi_draws`, `cost_type`, `cost_item_id`, `cost_amount`, `updated_at`)
VALUES
(9310022, '自由金猪', '从 9310022_303 迁移', 1, '[1,10]', 'NX', NULL, 10000, NOW());
