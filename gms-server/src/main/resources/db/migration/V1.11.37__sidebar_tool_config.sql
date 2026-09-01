-- 游戏内右边栏（ijl15 SideToolbar）ServerTool 配置：脚本路径 + tip，可热重载

CREATE TABLE IF NOT EXISTS `sidebar_tool_config` (
    `tool_index`    TINYINT       NOT NULL COMMENT 'ServerTool 序号 0..9，与客户端 SIDEBAR_TOOL 发包一致',
    `label`         VARCHAR(64)   NOT NULL DEFAULT '' COMMENT '管理后台显示名',
    `script_path`   VARCHAR(255)  NOT NULL DEFAULT '' COMMENT 'BeiDouSpecial 下脚本路径，空则不挂载并隐藏图标',
    `tip_title`     VARCHAR(64)   NOT NULL DEFAULT '' COMMENT '悬停标题',
    `tip_desc`      VARCHAR(255)  NOT NULL DEFAULT '' COMMENT '悬停描述',
    `enabled`       TINYINT       NOT NULL DEFAULT 1 COMMENT '0关闭 1开启；脚本为空时视为关闭',
    `updated_at`    DATETIME      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`tool_index`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='游戏右边栏 ServerTool 配置';

INSERT INTO `sidebar_tool_config`
(`tool_index`, `label`, `script_path`, `tip_title`, `tip_desc`, `enabled`)
VALUES
(0, '便民工具', 'xy/portal/便民工具', '便民工具', '仓库、发型、时装、答题、宠物', 1),
(1, '装备中心', 'xy/portal/装备中心', '装备中心', '强化、洗练、套装、转生', 1),
(2, '兑换中心', 'xy/portal/兑换中心', '兑换中心', '物品、封印、抽奖、口令礼包', 1),
(3, 'VIP会员',  'xy/portal/VIP会员',  'VIP 会员', '开通会员、专属商店、赞助', 1),
(4, '成长系统', 'xy/portal/成长系统', '成长系统', '新人福利、转职、等级奖励', 1),
(5, '每日任务', 'xy/portal/每日任务', '每日任务', '探索、副本、跑环、Boss', 1),
(6, '社交系统', 'xy/portal/社交系统', '社交系统', '师徒、家族等社交玩法', 1),
(7, '收集系统', 'xy/portal/收集系统', '收集系统', '卡片、勋章、钓鱼、戒指', 1),
(8, 'GM工具',   'xy/portal/GM工具',   'GM工具',   '管理员专用（长按会员分类亦可）', 1),
(9, '在线奖励', '在线奖励_nextlevel',  '在线奖励', '在线累计时长，领取阶段奖励', 1)
ON DUPLICATE KEY UPDATE `tool_index` = `tool_index`;

-- Web 侧栏菜单入口（日常系统）
INSERT INTO `sys_admin_menu`
(`id`, `parent_id`, `name`, `path`, `locale_key`, `icon`, `sort_order`, `menu_type`, `roles`, `requires_auth`, `hide_in_menu`, `enabled`, `remark`)
SELECT 30, 4, 'DailySidebarTool', 'sidebarTool', 'menu.game.sidebarTool', NULL, 6, 1, 'admin', 1, 0, 1, '游戏右边栏'
WHERE NOT EXISTS (SELECT 1 FROM `sys_admin_menu` WHERE `name` = 'DailySidebarTool');
