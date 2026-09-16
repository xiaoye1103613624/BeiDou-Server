-- 客户端 Data 路径：归入 Client 子类；侧栏新增「客户端」分组与路径配置页

UPDATE `game_config`
SET `config_sub_type` = 'Client',
    `config_desc`     = '客户端 Data 根目录（窗口商城等客户端操作共用；空=跳过）',
    `update_time`     = NOW()
WHERE `config_code` = 'window_cashshop_client_data_path';

UPDATE `lang_resources`
SET `lang_value` = '客户端 Data 根目录（窗口商城等客户端操作共用；空=跳过）'
WHERE `lang_type` = 'zh-CN'
  AND `lang_code` = 'window_cashshop_client_data_path';

UPDATE `lang_resources`
SET `lang_value` = 'Client Data root (shared by window cash shop and other client ops; empty=skip)'
WHERE `lang_type` = 'en-US'
  AND `lang_code` = 'window_cashshop_client_data_path';

INSERT INTO `sys_admin_menu`
(`parent_id`, `name`, `path`, `locale_key`, `icon`, `sort_order`, `menu_type`, `roles`, `requires_auth`, `hide_in_menu`, `enabled`, `remark`)
SELECT 0, 'client', '/client', 'menu.client', 'icon-storage', 7, 0, 'admin', 1, 0, 1, '客户端'
WHERE NOT EXISTS (SELECT 1 FROM `sys_admin_menu` WHERE `name` = 'client');

INSERT INTO `sys_admin_menu`
(`parent_id`, `name`, `path`, `locale_key`, `icon`, `sort_order`, `menu_type`, `roles`, `requires_auth`, `hide_in_menu`, `enabled`, `remark`)
SELECT p.`id`, 'ClientPath', 'path', 'menu.client.path', NULL, 0, 1, 'admin', 1, 0, 1, '客户端路径'
FROM `sys_admin_menu` p
WHERE p.`name` = 'client'
  AND NOT EXISTS (SELECT 1 FROM `sys_admin_menu` WHERE `name` = 'ClientPath');

INSERT INTO `sys_admin_role_menu` (`role_id`, `menu_id`)
SELECT 1, m.`id`
FROM `sys_admin_menu` m
WHERE m.`name` IN ('client', 'ClientPath')
  AND EXISTS (SELECT 1 FROM `sys_admin_role` r WHERE r.`id` = 1)
ON DUPLICATE KEY UPDATE `menu_id` = VALUES(`menu_id`);

INSERT INTO `sys_admin_role_menu` (`role_id`, `menu_id`)
SELECT 2, m.`id`
FROM `sys_admin_menu` m
WHERE m.`name` IN ('client', 'ClientPath')
  AND EXISTS (SELECT 1 FROM `sys_admin_role` r WHERE r.`id` = 2)
ON DUPLICATE KEY UPDATE `menu_id` = VALUES(`menu_id`);
