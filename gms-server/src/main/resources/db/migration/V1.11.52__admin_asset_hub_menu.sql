-- 客户端菜单：游戏资源中心；窗口商城展示名改为新商城

INSERT INTO `sys_admin_menu`
(`parent_id`, `name`, `path`, `locale_key`, `icon`, `sort_order`, `menu_type`, `roles`, `requires_auth`, `hide_in_menu`, `enabled`, `remark`)
SELECT p.`id`,
       'ClientAssetHub',
       'assetHub',
       'menu.client.assetHub',
       NULL,
       2,
       1,
       'admin,operator',
       1,
       0,
       1,
       '游戏资源中心（统一补齐图标到 game-assets）'
FROM `sys_admin_menu` p
WHERE p.`name` = 'client'
  AND NOT EXISTS (SELECT 1 FROM `sys_admin_menu` WHERE `name` = 'ClientAssetHub');

UPDATE `sys_admin_menu`
SET `remark` = '新商城数据（服务端库）'
WHERE `name` = 'windowCashShop';

UPDATE `sys_admin_menu`
SET `remark` = '新商城客户端同步（读客户端 Data）'
WHERE `name` = 'ClientWindowCashShopSync';

INSERT INTO `sys_admin_role_menu` (`role_id`, `menu_id`)
SELECT 1, m.`id`
FROM `sys_admin_menu` m
WHERE m.`name` = 'ClientAssetHub'
  AND EXISTS (SELECT 1 FROM `sys_admin_role` r WHERE r.`id` = 1)
ON DUPLICATE KEY UPDATE `menu_id` = VALUES(`menu_id`);

INSERT INTO `sys_admin_role_menu` (`role_id`, `menu_id`)
SELECT 2, m.`id`
FROM `sys_admin_menu` m
WHERE m.`name` = 'ClientAssetHub'
  AND EXISTS (SELECT 1 FROM `sys_admin_role` r WHERE r.`id` = 2)
ON DUPLICATE KEY UPDATE `menu_id` = VALUES(`menu_id`);
