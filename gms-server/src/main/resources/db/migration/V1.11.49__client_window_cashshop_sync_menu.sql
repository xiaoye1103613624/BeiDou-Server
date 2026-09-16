-- 客户端菜单：窗口商城客户端同步（读客户端 Data）；窗口商城服务端页备注澄清

INSERT INTO `sys_admin_menu`
(`parent_id`, `name`, `path`, `locale_key`, `icon`, `sort_order`, `menu_type`, `roles`, `requires_auth`, `hide_in_menu`, `enabled`, `remark`)
SELECT p.`id`,
       'ClientWindowCashShopSync',
       'windowCashShopSync',
       'menu.client.windowCashShopSync',
       NULL,
       1,
       1,
       'admin,operator',
       1,
       0,
       1,
       '窗口商城客户端同步（读客户端 Data）'
FROM `sys_admin_menu` p
WHERE p.`name` = 'client'
  AND NOT EXISTS (SELECT 1 FROM `sys_admin_menu` WHERE `name` = 'ClientWindowCashShopSync');

UPDATE `sys_admin_menu`
SET `roles`      = 'admin,operator',
    `sort_order` = 0,
    `remark`     = '客户端路径'
WHERE `name` = 'ClientPath';

UPDATE `sys_admin_menu`
SET `remark` = '窗口商城数据（服务端库）'
WHERE `name` = 'windowCashShop';

INSERT INTO `sys_admin_role_menu` (`role_id`, `menu_id`)
SELECT 1, m.`id`
FROM `sys_admin_menu` m
WHERE m.`name` = 'ClientWindowCashShopSync'
  AND EXISTS (SELECT 1 FROM `sys_admin_role` r WHERE r.`id` = 1)
ON DUPLICATE KEY UPDATE `menu_id` = VALUES(`menu_id`);

INSERT INTO `sys_admin_role_menu` (`role_id`, `menu_id`)
SELECT 2, m.`id`
FROM `sys_admin_menu` m
WHERE m.`name` = 'ClientWindowCashShopSync'
  AND EXISTS (SELECT 1 FROM `sys_admin_role` r WHERE r.`id` = 2)
ON DUPLICATE KEY UPDATE `menu_id` = VALUES(`menu_id`);
