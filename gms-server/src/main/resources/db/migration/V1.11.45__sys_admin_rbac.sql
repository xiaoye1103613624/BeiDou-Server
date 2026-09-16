-- 管理后台简易 RBAC：角色 / 角色菜单 / 账号角色（绑定游戏 accounts）

CREATE TABLE IF NOT EXISTS `sys_admin_role` (
    `id`         BIGINT       NOT NULL AUTO_INCREMENT,
    `code`       VARCHAR(32)  NOT NULL COMMENT '角色编码：admin / operator',
    `name`       VARCHAR(64)  NOT NULL COMMENT '显示名',
    `remark`     VARCHAR(255)          DEFAULT NULL,
    `enabled`    TINYINT      NOT NULL DEFAULT 1,
    `created_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sys_admin_role_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='管理后台角色';

CREATE TABLE IF NOT EXISTS `sys_admin_role_menu` (
    `role_id` BIGINT NOT NULL,
    `menu_id` BIGINT NOT NULL,
    PRIMARY KEY (`role_id`, `menu_id`),
    KEY `idx_sys_admin_role_menu_menu` (`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色-菜单绑定';

CREATE TABLE IF NOT EXISTS `sys_admin_account_role` (
    `account_id` INT    NOT NULL COMMENT 'accounts.id',
    `role_id`    BIGINT NOT NULL,
    PRIMARY KEY (`account_id`),
    KEY `idx_sys_admin_account_role_role` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='后台账号角色（一账号一角色）';

INSERT INTO `sys_admin_role` (`id`, `code`, `name`, `remark`, `enabled`)
VALUES
(1, 'admin', '管理员', '全部菜单与后台配置权限', 1),
(2, 'operator', '运营', '日常运营菜单，不含系统配置/菜单/账号授权', 1)
ON DUPLICATE KEY UPDATE `name` = VALUES(`name`);

-- 角色管理菜单（挂在「游戏管理」下）
INSERT INTO `sys_admin_menu`
(`id`, `parent_id`, `name`, `path`, `locale_key`, `icon`, `sort_order`, `menu_type`, `roles`, `requires_auth`, `hide_in_menu`, `enabled`, `remark`)
VALUES
(30, 23, 'SysRole', 'sysRole', 'menu.game.sysRole', NULL, 4, 1, 'admin', 1, 0, 1, '角色权限')
ON DUPLICATE KEY UPDATE `path` = VALUES(`path`), `locale_key` = VALUES(`locale_key`);

-- admin：全部现有菜单
INSERT INTO `sys_admin_role_menu` (`role_id`, `menu_id`)
SELECT 1, `id` FROM `sys_admin_menu`
ON DUPLICATE KEY UPDATE `menu_id` = VALUES(`menu_id`);

-- operator：排除账号列表 / 参数管理 / 菜单管理 / 角色权限
INSERT INTO `sys_admin_role_menu` (`role_id`, `menu_id`)
SELECT 2, `id` FROM `sys_admin_menu`
WHERE `id` NOT IN (18, 24, 27, 30)
ON DUPLICATE KEY UPDATE `menu_id` = VALUES(`menu_id`);

-- 已有 webadmin=1 账号默认授予 admin
INSERT INTO `sys_admin_account_role` (`account_id`, `role_id`)
SELECT `id`, 1 FROM `accounts` WHERE `webadmin` = 1
ON DUPLICATE KEY UPDATE `role_id` = VALUES(`role_id`);
