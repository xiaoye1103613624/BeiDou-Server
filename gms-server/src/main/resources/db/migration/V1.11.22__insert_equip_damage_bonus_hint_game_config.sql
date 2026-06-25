-- ================================================================
-- 装备伤害加成HINT提示开关
-- 用途：控制打怪命中时是否弹出"装备名: 伤害+xxx"明细提示气泡(逐件装备显示)
--       关闭后装备伤害加成本身仍正常生效，只是不再弹HINT
-- 关联表：xy_equip_damage_bonus_config（装备伤害加成配置表）
-- 关联代码：EquipDamageBonusManager / AbstractDealDamageHandler
-- ================================================================
INSERT INTO `game_config`(`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`, `update_time`)
SELECT 'server', 'Game Mechanics', 'java.lang.Boolean', 'equip_damage_bonus_hint_enabled', 'true', '是否在打怪命中时弹出装备伤害加成明细HINT提示', NOW()
WHERE NOT EXISTS (SELECT 1 FROM `game_config` WHERE `config_code` = 'equip_damage_bonus_hint_enabled');

-- 中文
INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'zh-CN', 'game_config', 'equip_damage_bonus_hint_enabled', '是否在打怪命中时弹出装备伤害加成明细HINT提示', NULL
WHERE NOT EXISTS (SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'zh-CN' AND `lang_code` = 'equip_damage_bonus_hint_enabled');

-- 英文
INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'en-US', 'game_config', 'equip_damage_bonus_hint_enabled', 'Whether to show the equip damage bonus breakdown HINT on hit', NULL
WHERE NOT EXISTS (SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'en-US' AND `lang_code` = 'equip_damage_bonus_hint_enabled');
