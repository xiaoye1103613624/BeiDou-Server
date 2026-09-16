-- 任务管理：开发态允许写客户端开关（默认关闭）+ EN/patcher 可选路径

INSERT INTO `game_config` (`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`)
SELECT 'server', 'Client', 'java.lang.Boolean', 'allow_quest_client_write', 'false', 'allow_quest_client_write'
WHERE NOT EXISTS (SELECT 1 FROM `game_config` WHERE `config_code` = 'allow_quest_client_write');

INSERT INTO `game_config` (`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`)
SELECT 'server', 'Client', 'java.lang.String', 'quest_client_en_path', '', 'quest_client_en_path'
WHERE NOT EXISTS (SELECT 1 FROM `game_config` WHERE `config_code` = 'quest_client_en_path');

INSERT INTO `game_config` (`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`)
SELECT 'server', 'Client', 'java.lang.String', 'quest_xml_img_patcher_path', '', 'quest_xml_img_patcher_path'
WHERE NOT EXISTS (SELECT 1 FROM `game_config` WHERE `config_code` = 'quest_xml_img_patcher_path');

INSERT INTO `lang_resources` (`lang_type`, `lang_base`, `lang_code`, `lang_value`)
SELECT 'zh-CN', 'game_config', 'allow_quest_client_write', '是否允许管理端把任务 WZ 写入客户端（开发态；生产请保持 false）'
WHERE NOT EXISTS (SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'zh-CN' AND `lang_code` = 'allow_quest_client_write');

INSERT INTO `lang_resources` (`lang_type`, `lang_base`, `lang_code`, `lang_value`)
SELECT 'en-US', 'game_config', 'allow_quest_client_write', 'Allow admin to write quest WZ into the client (dev only; keep false in production)'
WHERE NOT EXISTS (SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'en-US' AND `lang_code` = 'allow_quest_client_write');

INSERT INTO `lang_resources` (`lang_type`, `lang_base`, `lang_code`, `lang_value`)
SELECT 'zh-CN', 'game_config', 'quest_client_en_path', '客户端 EN 根目录（可选；空则尝试 Data 同级 EN）'
WHERE NOT EXISTS (SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'zh-CN' AND `lang_code` = 'quest_client_en_path');

INSERT INTO `lang_resources` (`lang_type`, `lang_base`, `lang_code`, `lang_value`)
SELECT 'en-US', 'game_config', 'quest_client_en_path', 'Client EN root (optional; empty = sibling EN of Data)'
WHERE NOT EXISTS (SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'en-US' AND `lang_code` = 'quest_client_en_path');

INSERT INTO `lang_resources` (`lang_type`, `lang_base`, `lang_code`, `lang_value`)
SELECT 'zh-CN', 'game_config', 'quest_xml_img_patcher_path', 'xml-img-patcher.exe 绝对路径（可选）'
WHERE NOT EXISTS (SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'zh-CN' AND `lang_code` = 'quest_xml_img_patcher_path');

INSERT INTO `lang_resources` (`lang_type`, `lang_base`, `lang_code`, `lang_value`)
SELECT 'en-US', 'game_config', 'quest_xml_img_patcher_path', 'Absolute path to xml-img-patcher.exe (optional)'
WHERE NOT EXISTS (SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'en-US' AND `lang_code` = 'quest_xml_img_patcher_path');
