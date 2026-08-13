-- Teach/set single skill: !skill <id> [level]
INSERT INTO command_info (syntax, level, enabled, clazz, default_level)
SELECT 'skill', 2, 1, 'SkillCommand', 2
WHERE NOT EXISTS (SELECT 1 FROM command_info WHERE clazz = 'SkillCommand' AND syntax = 'skill');
