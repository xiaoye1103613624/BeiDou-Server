-- 头顶灯泡/引导迁侧边栏：配置开关 + 第 11 槽「任务提醒」

INSERT INTO `game_config` (`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`)
SELECT 'server', 'Client', 'java.lang.Boolean', 'replace_overhead_icons', 'true', 'replace_overhead_icons'
WHERE NOT EXISTS (SELECT 1 FROM `game_config` WHERE `config_code` = 'replace_overhead_icons');

INSERT INTO `lang_resources` (`lang_type`, `lang_base`, `lang_code`, `lang_value`)
SELECT 'zh-CN', 'game_config', 'replace_overhead_icons', '头顶入口迁侧边栏（关引导精灵 + 场上 NPC 任务灯泡；侧边栏任务提醒）'
WHERE NOT EXISTS (SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'zh-CN' AND `lang_code` = 'replace_overhead_icons');

INSERT INTO `lang_resources` (`lang_type`, `lang_base`, `lang_code`, `lang_value`)
SELECT 'en-US', 'game_config', 'replace_overhead_icons', 'Move overhead guide/quest icons to sidebar (disable guide spawn + field NPC QuestIcon)'
WHERE NOT EXISTS (SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'en-US' AND `lang_code` = 'replace_overhead_icons');

INSERT INTO `sidebar_tool_config`
(`tool_index`, `label`, `script_path`, `tip_title`, `tip_desc`, `enabled`)
VALUES
(10, '任务提醒', 'xy/portal/任务提醒', '任务提醒', '可接·进行中·可交付任务', 1)
ON DUPLICATE KEY UPDATE
    `label` = VALUES(`label`),
    `script_path` = VALUES(`script_path`),
    `tip_title` = VALUES(`tip_title`),
    `tip_desc` = VALUES(`tip_desc`),
    `enabled` = VALUES(`enabled`);
