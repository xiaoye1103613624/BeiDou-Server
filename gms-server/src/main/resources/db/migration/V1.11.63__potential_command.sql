-- 潜能 / Hyper GM 指令注册
INSERT INTO command_info (syntax, level, enabled, clazz, default_level)
SELECT 'potential', 4, 1, 'PotentialCommand', 4
WHERE NOT EXISTS (SELECT 1 FROM command_info WHERE clazz = 'PotentialCommand' AND syntax = 'potential');
