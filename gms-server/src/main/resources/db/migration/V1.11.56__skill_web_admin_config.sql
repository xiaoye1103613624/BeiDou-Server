-- 技能 Web 管理：开发态写服务端 / 写客户端开关 + 菜单

INSERT INTO `game_config` (`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`)
SELECT 'server', 'Client', 'java.lang.Boolean', 'allow_skill_editor_write', 'false', 'allow_skill_editor_write'
WHERE NOT EXISTS (SELECT 1 FROM `game_config` WHERE `config_code` = 'allow_skill_editor_write');

INSERT INTO `game_config` (`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`)
SELECT 'server', 'Client', 'java.lang.Boolean', 'allow_skill_client_write', 'false', 'allow_skill_client_write'
WHERE NOT EXISTS (SELECT 1 FROM `game_config` WHERE `config_code` = 'allow_skill_client_write');

INSERT INTO `game_config` (`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`)
SELECT 'server', 'Client', 'java.lang.String', 'skill_client_en_path', '', 'skill_client_en_path'
WHERE NOT EXISTS (SELECT 1 FROM `game_config` WHERE `config_code` = 'skill_client_en_path');

INSERT INTO `game_config` (`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`)
SELECT 'server', 'Client', 'java.lang.String', 'skill_xml_img_patcher_path', '', 'skill_xml_img_patcher_path'
WHERE NOT EXISTS (SELECT 1 FROM `game_config` WHERE `config_code` = 'skill_xml_img_patcher_path');

INSERT INTO `lang_resources` (`lang_type`, `lang_base`, `lang_code`, `lang_value`)
SELECT 'zh-CN', 'game_config', 'allow_skill_editor_write', '是否允许管理端写服务端 Skill/String WZ 并热重载（开发态；生产请保持 false）'
WHERE NOT EXISTS (SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'zh-CN' AND `lang_code` = 'allow_skill_editor_write');

INSERT INTO `lang_resources` (`lang_type`, `lang_base`, `lang_code`, `lang_value`)
SELECT 'en-US', 'game_config', 'allow_skill_editor_write', 'Allow admin to write Skill/String WZ and reloadSkills (dev only; keep false in production)'
WHERE NOT EXISTS (SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'en-US' AND `lang_code` = 'allow_skill_editor_write');

INSERT INTO `lang_resources` (`lang_type`, `lang_base`, `lang_code`, `lang_value`)
SELECT 'zh-CN', 'game_config', 'allow_skill_client_write', '是否允许管理端把技能 WZ 写入客户端（开发态；生产请保持 false）'
WHERE NOT EXISTS (SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'zh-CN' AND `lang_code` = 'allow_skill_client_write');

INSERT INTO `lang_resources` (`lang_type`, `lang_base`, `lang_code`, `lang_value`)
SELECT 'en-US', 'game_config', 'allow_skill_client_write', 'Allow admin to write skill WZ into the client (dev only; keep false in production)'
WHERE NOT EXISTS (SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'en-US' AND `lang_code` = 'allow_skill_client_write');

INSERT INTO `lang_resources` (`lang_type`, `lang_base`, `lang_code`, `lang_value`)
SELECT 'zh-CN', 'game_config', 'skill_client_en_path', '客户端 EN 根目录（可选；空则尝试 Data 同级 EN）'
WHERE NOT EXISTS (SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'zh-CN' AND `lang_code` = 'skill_client_en_path');

INSERT INTO `lang_resources` (`lang_type`, `lang_base`, `lang_code`, `lang_value`)
SELECT 'en-US', 'game_config', 'skill_client_en_path', 'Client EN root (optional; empty = sibling EN of Data)'
WHERE NOT EXISTS (SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'en-US' AND `lang_code` = 'skill_client_en_path');

INSERT INTO `lang_resources` (`lang_type`, `lang_base`, `lang_code`, `lang_value`)
SELECT 'zh-CN', 'game_config', 'skill_xml_img_patcher_path', 'xml-img-patcher.exe 绝对路径（可选；空则复用任务路径或仓库默认）'
WHERE NOT EXISTS (SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'zh-CN' AND `lang_code` = 'skill_xml_img_patcher_path');

INSERT INTO `lang_resources` (`lang_type`, `lang_base`, `lang_code`, `lang_value`)
SELECT 'en-US', 'game_config', 'skill_xml_img_patcher_path', 'Absolute path to xml-img-patcher.exe (optional)'
WHERE NOT EXISTS (SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'en-US' AND `lang_code` = 'skill_xml_img_patcher_path');

INSERT INTO `sys_admin_menu`
(`parent_id`, `name`, `path`, `locale_key`, `icon`, `sort_order`, `menu_type`, `roles`, `requires_auth`, `hide_in_menu`, `enabled`, `remark`)
SELECT p.`id`,
       'ClientSkillResources',
       'skillResources',
       'menu.client.skillResources',
       NULL,
       3,
       1,
       'admin,operator',
       1,
       0,
       1,
       '技能资源（浏览/开发态改 WZ/同步客户端）'
FROM `sys_admin_menu` p
WHERE p.`name` = 'client'
  AND NOT EXISTS (SELECT 1 FROM `sys_admin_menu` WHERE `name` = 'ClientSkillResources');

INSERT INTO `sys_admin_role_menu` (`role_id`, `menu_id`)
SELECT 1, m.`id`
FROM `sys_admin_menu` m
WHERE m.`name` = 'ClientSkillResources'
  AND EXISTS (SELECT 1 FROM `sys_admin_role` r WHERE r.`id` = 1)
ON DUPLICATE KEY UPDATE `menu_id` = VALUES(`menu_id`);

INSERT INTO `sys_admin_role_menu` (`role_id`, `menu_id`)
SELECT 2, m.`id`
FROM `sys_admin_menu` m
WHERE m.`name` = 'ClientSkillResources'
  AND EXISTS (SELECT 1 FROM `sys_admin_role` r WHERE r.`id` = 2)
ON DUPLICATE KEY UPDATE `menu_id` = VALUES(`menu_id`);
