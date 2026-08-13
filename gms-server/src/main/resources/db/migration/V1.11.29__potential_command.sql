-- Phase1：潜能/Hyper GM 指令（命令从 command_info 表加载，硬编码 registerLv* 已注释）
INSERT INTO command_info (syntax, level, enabled, clazz, default_level)
SELECT 'potential', 3, 1, 'PotentialCommand', 3
WHERE NOT EXISTS (SELECT 1 FROM command_info WHERE clazz = 'PotentialCommand' AND syntax = 'potential');

INSERT INTO command_info (syntax, level, enabled, clazz, default_level)
SELECT '潜能', 3, 1, 'PotentialCommand', 3
WHERE NOT EXISTS (SELECT 1 FROM command_info WHERE clazz = 'PotentialCommand' AND syntax = '潜能');
