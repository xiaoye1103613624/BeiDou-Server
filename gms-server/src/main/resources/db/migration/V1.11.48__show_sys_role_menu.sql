-- 角色权限前端已落地：在系统管理下显示 SysRole
UPDATE `sys_admin_menu`
SET `hide_in_menu` = 0,
    `sort_order`   = 4
WHERE `name` = 'SysRole';
