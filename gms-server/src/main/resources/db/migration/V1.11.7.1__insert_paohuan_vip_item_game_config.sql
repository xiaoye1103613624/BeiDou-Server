-- ================================================================
-- 跑环VIP物品传送配置
-- 用途：拥有指定VIP物品的玩家，开启跑环任务后可直接传送到物品掉落地图，
--       省去手动跑图时间
-- 参数：paohuan_vip_item_id = VIP物品ID
--       0 = 禁用传送功能（所有玩家需手动跑图打怪）
--       >0 = 拥有该物品的玩家开启跑环后可一键传送至怪物地图
-- 关联表：xy_paohuan_config（跑环配置表）
-- ================================================================
INSERT INTO `game_config`(`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`, `update_time`)
SELECT 'server', 'Game Mechanics', 'java.lang.Integer', 'paohuan_vip_item_id', '0', '跑环VIP物品ID（0=禁用传送）', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `game_config` WHERE `config_code` = 'paohuan_vip_item_id');

-- 中文
INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'zh-CN', 'game_config', 'paohuan_vip_item_id', '跑环VIP物品ID（0=禁用传送）', NULL
WHERE NOT EXISTS (SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'zh-CN' AND `lang_code` = 'paohuan_vip_item_id');

-- 英文
INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'en-US', 'game_config', 'paohuan_vip_item_id', 'Paohuan VIP item ID (0=disable teleport)', NULL
WHERE NOT EXISTS (SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'en-US' AND `lang_code` = 'paohuan_vip_item_id');
