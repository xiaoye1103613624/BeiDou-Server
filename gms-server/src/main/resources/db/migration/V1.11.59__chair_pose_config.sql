-- 座椅/骑宠姿态管理：开发态写服务端 / 写客户端开关 + 菜单

INSERT INTO `game_config` (`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`)
SELECT 'server', 'Client', 'java.lang.Boolean', 'allow_chair_editor_write', 'false', 'allow_chair_editor_write'
WHERE NOT EXISTS (SELECT 1 FROM `game_config` WHERE `config_code` = 'allow_chair_editor_write');

INSERT INTO `game_config` (`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`)
SELECT 'server', 'Client', 'java.lang.Boolean', 'allow_chair_client_write', 'false', 'allow_chair_client_write'
WHERE NOT EXISTS (SELECT 1 FROM `game_config` WHERE `config_code` = 'allow_chair_client_write');

INSERT INTO `game_config` (`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`)
SELECT 'server', 'Client', 'java.lang.String', 'chair_client_en_path', '', 'chair_client_en_path'
WHERE NOT EXISTS (SELECT 1 FROM `game_config` WHERE `config_code` = 'chair_client_en_path');

INSERT INTO `game_config` (`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`)
SELECT 'server', 'Client', 'java.lang.String', 'chair_xml_img_patcher_path', '', 'chair_xml_img_patcher_path'
WHERE NOT EXISTS (SELECT 1 FROM `game_config` WHERE `config_code` = 'chair_xml_img_patcher_path');

INSERT INTO `lang_resources` (`lang_type`, `lang_base`, `lang_code`, `lang_value`)
SELECT 'zh-CN', 'game_config', 'allow_chair_editor_write', '是否允许管理端写服务端 Install/TamingMob WZ（开发态；生产请保持 false）'
WHERE NOT EXISTS (SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'zh-CN' AND `lang_code` = 'allow_chair_editor_write');

INSERT INTO `lang_resources` (`lang_type`, `lang_base`, `lang_code`, `lang_value`)
SELECT 'en-US', 'game_config', 'allow_chair_editor_write', 'Allow admin to write Install/TamingMob WZ (dev only; keep false in production)'
WHERE NOT EXISTS (SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'en-US' AND `lang_code` = 'allow_chair_editor_write');

INSERT INTO `lang_resources` (`lang_type`, `lang_base`, `lang_code`, `lang_value`)
SELECT 'zh-CN', 'game_config', 'allow_chair_client_write', '是否允许管理端把座椅/骑宠姿态写入客户端（开发态；生产请保持 false）'
WHERE NOT EXISTS (SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'zh-CN' AND `lang_code` = 'allow_chair_client_write');

INSERT INTO `lang_resources` (`lang_type`, `lang_base`, `lang_code`, `lang_value`)
SELECT 'en-US', 'game_config', 'allow_chair_client_write', 'Allow admin to write chair/taming pose into the client (dev only; keep false in production)'
WHERE NOT EXISTS (SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'en-US' AND `lang_code` = 'allow_chair_client_write');

INSERT INTO `lang_resources` (`lang_type`, `lang_base`, `lang_code`, `lang_value`)
SELECT 'zh-CN', 'game_config', 'chair_client_en_path', '客户端 EN 根目录（可选；空则尝试 Data 同级 EN）'
WHERE NOT EXISTS (SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'zh-CN' AND `lang_code` = 'chair_client_en_path');

INSERT INTO `lang_resources` (`lang_type`, `lang_base`, `lang_code`, `lang_value`)
SELECT 'en-US', 'game_config', 'chair_client_en_path', 'Client EN root (optional; empty = sibling EN of Data)'
WHERE NOT EXISTS (SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'en-US' AND `lang_code` = 'chair_client_en_path');

INSERT INTO `lang_resources` (`lang_type`, `lang_base`, `lang_code`, `lang_value`)
SELECT 'zh-CN', 'game_config', 'chair_xml_img_patcher_path', 'xml-img-patcher.exe 绝对路径（可选；空则复用任务/技能路径或仓库默认）'
WHERE NOT EXISTS (SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'zh-CN' AND `lang_code` = 'chair_xml_img_patcher_path');

INSERT INTO `lang_resources` (`lang_type`, `lang_base`, `lang_code`, `lang_value`)
SELECT 'en-US', 'game_config', 'chair_xml_img_patcher_path', 'Absolute path to xml-img-patcher.exe (optional)'
WHERE NOT EXISTS (SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'en-US' AND `lang_code` = 'chair_xml_img_patcher_path');

INSERT INTO `sys_admin_menu`
(`parent_id`, `name`, `path`, `locale_key`, `icon`, `sort_order`, `menu_type`, `roles`, `requires_auth`, `hide_in_menu`, `enabled`, `remark`)
SELECT p.`id`,
       'ClientChairPose',
       'chairPose',
       'menu.client.chairPose',
       NULL,
       4,
       1,
       'admin,operator',
       1,
       0,
       1,
       '座椅设置（椅子/骑宠姿态与娃娃预览）'
FROM `sys_admin_menu` p
WHERE p.`name` = 'client'
  AND NOT EXISTS (SELECT 1 FROM `sys_admin_menu` WHERE `name` = 'ClientChairPose');

INSERT INTO `sys_admin_role_menu` (`role_id`, `menu_id`)
SELECT 1, m.`id`
FROM `sys_admin_menu` m
WHERE m.`name` = 'ClientChairPose'
  AND EXISTS (SELECT 1 FROM `sys_admin_role` r WHERE r.`id` = 1)
ON DUPLICATE KEY UPDATE `menu_id` = VALUES(`menu_id`);

INSERT INTO `sys_admin_role_menu` (`role_id`, `menu_id`)
SELECT 2, m.`id`
FROM `sys_admin_menu` m
WHERE m.`name` = 'ClientChairPose'
  AND EXISTS (SELECT 1 FROM `sys_admin_role` r WHERE r.`id` = 2)
ON DUPLICATE KEY UPDATE `menu_id` = VALUES(`menu_id`);
