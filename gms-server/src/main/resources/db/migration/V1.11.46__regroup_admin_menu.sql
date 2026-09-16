-- 管理后台侧栏按业务重分组：系统管理 / 游戏管理 / 活动 / 玩家 / 强化 / 玩法
-- 仅调整 sys_admin_menu 树与 path；前端路由 name 保持不变，旧 URL 由前端 redirect 兼容

-- 1) 根目录：日常系统 → 系统管理
UPDATE `sys_admin_menu`
SET `name`       = 'system',
    `path`       = '/system',
    `locale_key` = 'menu.system',
    `icon`       = 'icon-settings',
    `sort_order` = 1,
    `remark`     = '系统管理'
WHERE `name` = 'daily';

-- 2) 根目录：成长系统 → 强化
UPDATE `sys_admin_menu`
SET `name`       = 'enhance',
    `path`       = '/enhance',
    `locale_key` = 'menu.enhance',
    `icon`       = 'icon-thunderbolt',
    `sort_order` = 5,
    `remark`     = '强化'
WHERE `name` = 'growth';

-- 3) 根目录：会员中心 → 玩家（路径仍为 /member）
UPDATE `sys_admin_menu`
SET `locale_key` = 'menu.member',
    `sort_order` = 4,
    `remark`     = '玩家'
WHERE `name` = 'member';

-- 4) 游戏管理排序
UPDATE `sys_admin_menu`
SET `sort_order` = 2,
    `remark`     = '游戏管理'
WHERE `name` = 'game';

-- 5) 新增「活动」「玩法」根目录
INSERT INTO `sys_admin_menu`
(`parent_id`, `name`, `path`, `locale_key`, `icon`, `sort_order`, `menu_type`, `roles`, `requires_auth`, `hide_in_menu`, `enabled`, `remark`)
SELECT 0, 'activity', '/activity', 'menu.activity', 'icon-gift', 3, 0, 'admin', 1, 0, 1, '活动'
WHERE NOT EXISTS (SELECT 1 FROM `sys_admin_menu` WHERE `name` = 'activity');

INSERT INTO `sys_admin_menu`
(`parent_id`, `name`, `path`, `locale_key`, `icon`, `sort_order`, `menu_type`, `roles`, `requires_auth`, `hide_in_menu`, `enabled`, `remark`)
SELECT 0, 'gameplay', '/gameplay', 'menu.gameplay', 'icon-trophy', 6, 0, 'admin', 1, 0, 1, '玩法'
WHERE NOT EXISTS (SELECT 1 FROM `sys_admin_menu` WHERE `name` = 'gameplay');

-- 6) 系统管理子项
UPDATE `sys_admin_menu` c
    JOIN `sys_admin_menu` p ON p.`name` = 'system'
SET c.`parent_id`  = p.`id`,
    c.`path`       = 'sysMenu',
    c.`sort_order` = 0
WHERE c.`name` = 'SysMenu';

UPDATE `sys_admin_menu` c
    JOIN `sys_admin_menu` p ON p.`name` = 'system'
SET c.`parent_id`  = p.`id`,
    c.`path`       = 'file',
    c.`sort_order` = 1
WHERE c.`name` = 'DailyFile';

UPDATE `sys_admin_menu` c
    JOIN `sys_admin_menu` p ON p.`name` = 'system'
SET c.`parent_id`  = p.`id`,
    c.`path`       = 'autoban',
    c.`sort_order` = 2
WHERE c.`name` = 'DailyAutoban';

UPDATE `sys_admin_menu` c
    JOIN `sys_admin_menu` p ON p.`name` = 'system'
SET c.`parent_id`  = p.`id`,
    c.`path`       = 'commandInfo',
    c.`sort_order` = 3
WHERE c.`name` = 'DailyCommandInfo';

-- 角色权限页前端尚未落地：挂到系统管理并隐藏，避免侧栏 404
UPDATE `sys_admin_menu` c
    JOIN `sys_admin_menu` p ON p.`name` = 'system'
SET c.`parent_id`    = p.`id`,
    c.`path`         = 'sysRole',
    c.`sort_order`   = 99,
    c.`hide_in_menu` = 1
WHERE c.`name` = 'SysRole';

-- 7) 游戏管理子项（保留原有 + 迁入天气/爆率/右边栏）
UPDATE `sys_admin_menu` c
    JOIN `sys_admin_menu` p ON p.`name` = 'game'
SET c.`parent_id`  = p.`id`,
    c.`path`       = 'config',
    c.`sort_order` = 0
WHERE c.`name` = 'Config';

UPDATE `sys_admin_menu` c
    JOIN `sys_admin_menu` p ON p.`name` = 'game'
SET c.`parent_id`  = p.`id`,
    c.`path`       = 'npcShop',
    c.`sort_order` = 1
WHERE c.`name` = 'NpcShop';

UPDATE `sys_admin_menu` c
    JOIN `sys_admin_menu` p ON p.`name` = 'game'
SET c.`parent_id`  = p.`id`,
    c.`path`       = 'windowCashShop',
    c.`sort_order` = 2
WHERE c.`name` = 'windowCashShop';

UPDATE `sys_admin_menu` c
    JOIN `sys_admin_menu` p ON p.`name` = 'game'
SET c.`parent_id`  = p.`id`,
    c.`path`       = 'weather',
    c.`sort_order` = 3
WHERE c.`name` = 'DailyWeather';

UPDATE `sys_admin_menu` c
    JOIN `sys_admin_menu` p ON p.`name` = 'game'
SET c.`parent_id`  = p.`id`,
    c.`path`       = 'drop',
    c.`sort_order` = 4
WHERE c.`name` = 'GrowthDrop';

UPDATE `sys_admin_menu` c
    JOIN `sys_admin_menu` p ON p.`name` = 'game'
SET c.`parent_id`  = p.`id`,
    c.`path`       = 'drop/global',
    c.`sort_order` = 5
WHERE c.`name` = 'GrowthGlobalDrop';

UPDATE `sys_admin_menu` c
    JOIN `sys_admin_menu` p ON p.`name` = 'game'
SET c.`parent_id`  = p.`id`,
    c.`path`       = 'sidebarTool',
    c.`sort_order` = 6
WHERE c.`name` = 'DailySidebarTool';

-- 8) 活动
UPDATE `sys_admin_menu` c
    JOIN `sys_admin_menu` p ON p.`name` = 'activity'
SET c.`parent_id`  = p.`id`,
    c.`path`       = 'activity',
    c.`sort_order` = 0
WHERE c.`name` = 'DailyActivity';

UPDATE `sys_admin_menu` c
    JOIN `sys_admin_menu` p ON p.`name` = 'activity'
SET c.`parent_id`  = p.`id`,
    c.`path`       = 'dailyCheckin',
    c.`sort_order` = 1
WHERE c.`name` = 'DailyCheckin';

-- 9) 玩家（商城迁出）
UPDATE `sys_admin_menu` c
    JOIN `sys_admin_menu` p ON p.`name` = 'member'
SET c.`parent_id`  = p.`id`,
    c.`path`       = 'list',
    c.`sort_order` = 0
WHERE c.`name` = 'MemberAccountList';

UPDATE `sys_admin_menu` c
    JOIN `sys_admin_menu` p ON p.`name` = 'member'
SET c.`parent_id`  = p.`id`,
    c.`path`       = 'player',
    c.`sort_order` = 1
WHERE c.`name` = 'MemberPlayerList';

UPDATE `sys_admin_menu` c
    JOIN `sys_admin_menu` p ON p.`name` = 'member'
SET c.`parent_id`  = p.`id`,
    c.`path`       = 'inventory',
    c.`sort_order` = 2
WHERE c.`name` = 'MemberInventory';

UPDATE `sys_admin_menu` c
    JOIN `sys_admin_menu` p ON p.`name` = 'member'
SET c.`parent_id`  = p.`id`,
    c.`path`       = 'ranking',
    c.`sort_order` = 3
WHERE c.`name` = 'MemberRanking';

-- 10) 强化
UPDATE `sys_admin_menu` c
    JOIN `sys_admin_menu` p ON p.`name` = 'enhance'
SET c.`parent_id`  = p.`id`,
    c.`path`       = 'setItem',
    c.`sort_order` = 0
WHERE c.`name` = 'GrowthSetItem';

UPDATE `sys_admin_menu` c
    JOIN `sys_admin_menu` p ON p.`name` = 'enhance'
SET c.`parent_id`  = p.`id`,
    c.`path`       = 'petGrowth',
    c.`sort_order` = 1
WHERE c.`name` = 'GrowthPetGrowth';

-- 补齐强化配方（前端已有，种子可能缺失）
INSERT INTO `sys_admin_menu`
(`parent_id`, `name`, `path`, `locale_key`, `icon`, `sort_order`, `menu_type`, `roles`, `requires_auth`, `hide_in_menu`, `enabled`, `remark`)
SELECT p.`id`, 'GrowthAlchemyRecipe', 'alchemyRecipe', 'menu.game.alchemyRecipe', NULL, 2, 1, 'admin', 1, 0, 1, NULL
FROM `sys_admin_menu` p
WHERE p.`name` = 'enhance'
  AND NOT EXISTS (SELECT 1 FROM `sys_admin_menu` WHERE `name` = 'GrowthAlchemyRecipe');

INSERT INTO `sys_admin_menu`
(`parent_id`, `name`, `path`, `locale_key`, `icon`, `sort_order`, `menu_type`, `roles`, `requires_auth`, `hide_in_menu`, `enabled`, `remark`)
SELECT p.`id`, 'GrowthAlchemistRecipe', 'alchemistRecipe', 'menu.game.alchemistRecipe', NULL, 3, 1, 'admin', 1, 0, 1, NULL
FROM `sys_admin_menu` p
WHERE p.`name` = 'enhance'
  AND NOT EXISTS (SELECT 1 FROM `sys_admin_menu` WHERE `name` = 'GrowthAlchemistRecipe');

INSERT INTO `sys_admin_menu`
(`parent_id`, `name`, `path`, `locale_key`, `icon`, `sort_order`, `menu_type`, `roles`, `requires_auth`, `hide_in_menu`, `enabled`, `remark`)
SELECT p.`id`, 'GrowthForgeRecipe', 'forgeRecipe', 'menu.game.forgeRecipe', NULL, 4, 1, 'admin', 1, 0, 1, NULL
FROM `sys_admin_menu` p
WHERE p.`name` = 'enhance'
  AND NOT EXISTS (SELECT 1 FROM `sys_admin_menu` WHERE `name` = 'GrowthForgeRecipe');

INSERT INTO `sys_admin_menu`
(`parent_id`, `name`, `path`, `locale_key`, `icon`, `sort_order`, `menu_type`, `roles`, `requires_auth`, `hide_in_menu`, `enabled`, `remark`)
SELECT p.`id`, 'GrowthAlchemyTier', 'alchemyTier', 'menu.game.alchemyTier', NULL, 5, 1, 'admin', 1, 0, 1, NULL
FROM `sys_admin_menu` p
WHERE p.`name` = 'enhance'
  AND NOT EXISTS (SELECT 1 FROM `sys_admin_menu` WHERE `name` = 'GrowthAlchemyTier');

-- 11) 玩法
UPDATE `sys_admin_menu` c
    JOIN `sys_admin_menu` p ON p.`name` = 'gameplay'
SET c.`parent_id`  = p.`id`,
    c.`path`       = 'cashShop',
    c.`sort_order` = 0
WHERE c.`name` = 'MemberCashShop';

UPDATE `sys_admin_menu` c
    JOIN `sys_admin_menu` p ON p.`name` = 'gameplay'
SET c.`parent_id`  = p.`id`,
    c.`path`       = 'gachapon',
    c.`sort_order` = 1
WHERE c.`name` = 'GrowthGachapon';

-- 12) 角色菜单绑定：补齐新增根/叶子给 admin；operator 同步可见的新菜单
INSERT INTO `sys_admin_role_menu` (`role_id`, `menu_id`)
SELECT 1, m.`id`
FROM `sys_admin_menu` m
WHERE EXISTS (SELECT 1 FROM `sys_admin_role` r WHERE r.`id` = 1)
ON DUPLICATE KEY UPDATE `menu_id` = VALUES(`menu_id`);

INSERT INTO `sys_admin_role_menu` (`role_id`, `menu_id`)
SELECT 2, m.`id`
FROM `sys_admin_menu` m
WHERE EXISTS (SELECT 1 FROM `sys_admin_role` r WHERE r.`id` = 2)
  AND m.`name` NOT IN ('MemberAccountList', 'Config', 'SysMenu', 'SysRole')
ON DUPLICATE KEY UPDATE `menu_id` = VALUES(`menu_id`);
