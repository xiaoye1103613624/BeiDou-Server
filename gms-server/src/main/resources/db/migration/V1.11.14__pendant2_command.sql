-- 第二吊坠 −51：玩家可查询/卸下（客户端无 BP51 UI）
INSERT INTO command_info (syntax, level, enabled, clazz, default_level)
SELECT 'pendant2', 0, 1, 'Pendant2Command', 0
WHERE NOT EXISTS (SELECT 1 FROM command_info WHERE clazz = 'Pendant2Command' AND syntax = 'pendant2');

INSERT INTO command_info (syntax, level, enabled, clazz, default_level)
SELECT '第二坠', 0, 1, 'Pendant2Command', 0
WHERE NOT EXISTS (SELECT 1 FROM command_info WHERE clazz = 'Pendant2Command' AND syntax = '第二坠');
