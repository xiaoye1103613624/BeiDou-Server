-- ================================================================
-- 仓库系统全局配置
-- 用途：控制玩家仓库的共享模式和存储上限
-- 参数说明：
--   warehouse_account_shared = 仓库是否账号共享（true=同账号所有角色共用, false=每个角色独立仓库）
--   warehouse_max_stack     = 可叠加物品单格最大存放数量（默认30000，如药水/矿石等）
-- 关联表：xy_warehouse_config（仓库物品配置表）
-- ================================================================
-- 仓库是否账号共享（true=同一账号下所有角色共享仓库，false=角色仓库独立）
INSERT INTO `game_config`(`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`, `update_time`)
SELECT 'server', 'Game Mechanics', 'java.lang.Boolean', 'warehouse_account_shared', 'false', '仓库是否账号共享(true=账号共享 false=角色独立)', NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM `game_config` WHERE `config_code` = 'warehouse_account_shared'
);

-- 仓库可叠加物品的最大存放数量（默认30000）
INSERT INTO `game_config`(`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`, `update_time`)
SELECT 'server', 'Game Mechanics', 'java.lang.Integer', 'warehouse_max_stack', '30000', '仓库可叠加物品最大存放数量', NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM `game_config` WHERE `config_code` = 'warehouse_max_stack'
);

-- 中文内容
INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'zh-CN', 'game_config', 'warehouse_account_shared', '仓库是否账号共享（true=账号下所有角色共享仓库，false=角色仓库独立）', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'zh-CN' AND `lang_code` = 'warehouse_account_shared'
);

INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'zh-CN', 'game_config', 'warehouse_max_stack', '仓库可叠加物品最大存放数量', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'zh-CN' AND `lang_code` = 'warehouse_max_stack'
);

-- 英文内容
INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'en-US', 'game_config', 'warehouse_account_shared', 'Warehouse account shared (true=all characters share, false=per-character).', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'en-US' AND `lang_code` = 'warehouse_account_shared'
);

INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'en-US', 'game_config', 'warehouse_max_stack', 'Max stackable item quantity in warehouse.', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'en-US' AND `lang_code` = 'warehouse_max_stack'
);
