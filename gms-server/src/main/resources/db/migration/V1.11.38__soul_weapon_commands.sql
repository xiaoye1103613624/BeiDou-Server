-- Phase A：灵魂武器命令（早期宝珠系统）
INSERT INTO command_info (syntax, level, enabled, clazz, default_level)
SELECT 'soul', 3, 1, 'SoulCommand', 3
WHERE NOT EXISTS (SELECT 1 FROM command_info WHERE clazz = 'SoulCommand' AND syntax = 'soul');

INSERT INTO command_info (syntax, level, enabled, clazz, default_level)
SELECT '灵魂', 3, 1, 'SoulCommand', 3
WHERE NOT EXISTS (SELECT 1 FROM command_info WHERE clazz = 'SoulCommand' AND syntax = '灵魂');

INSERT INTO command_info (syntax, level, enabled, clazz, default_level)
SELECT 'soulskill', 0, 1, 'SoulSkillCommand', 0
WHERE NOT EXISTS (SELECT 1 FROM command_info WHERE clazz = 'SoulSkillCommand' AND syntax = 'soulskill');

INSERT INTO command_info (syntax, level, enabled, clazz, default_level)
SELECT '灵魂技能', 0, 1, 'SoulSkillCommand', 0
WHERE NOT EXISTS (SELECT 1 FROM command_info WHERE clazz = 'SoulSkillCommand' AND syntax = '灵魂技能');
