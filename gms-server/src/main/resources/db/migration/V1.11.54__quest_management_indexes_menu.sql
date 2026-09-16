-- 任务管理：进度表索引 + 管理菜单

CREATE INDEX `idx_queststatus_char_quest` ON `queststatus` (`characterid`, `quest`);
CREATE INDEX `idx_queststatus_quest_status` ON `queststatus` (`quest`, `status`);
CREATE INDEX `idx_questprogress_statusid` ON `questprogress` (`queststatusid`);
CREATE INDEX `idx_questprogress_char` ON `questprogress` (`characterid`);

INSERT INTO `sys_admin_menu`
(`parent_id`, `name`, `path`, `locale_key`, `icon`, `sort_order`, `menu_type`, `roles`, `requires_auth`, `hide_in_menu`, `enabled`, `remark`)
SELECT p.`id`,
       'GameQuest',
       'quest',
       'menu.game.quest',
       NULL,
       15,
       1,
       'admin,operator',
       1,
       0,
       1,
       '任务管理（WZ 定义 + 完成态 + 可选客户端同步）'
FROM `sys_admin_menu` p
WHERE p.`name` = 'game'
  AND NOT EXISTS (SELECT 1 FROM `sys_admin_menu` WHERE `name` = 'GameQuest');

INSERT INTO `sys_admin_role_menu` (`role_id`, `menu_id`)
SELECT 1, m.`id`
FROM `sys_admin_menu` m
WHERE m.`name` = 'GameQuest'
  AND EXISTS (SELECT 1 FROM `sys_admin_role` r WHERE r.`id` = 1)
ON DUPLICATE KEY UPDATE `menu_id` = VALUES(`menu_id`);

INSERT INTO `sys_admin_role_menu` (`role_id`, `menu_id`)
SELECT 2, m.`id`
FROM `sys_admin_menu` m
WHERE m.`name` = 'GameQuest'
  AND EXISTS (SELECT 1 FROM `sys_admin_role` r WHERE r.`id` = 2)
ON DUPLICATE KEY UPDATE `menu_id` = VALUES(`menu_id`);
