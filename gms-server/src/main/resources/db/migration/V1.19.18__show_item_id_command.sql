-- GM-only client item name id suffix toggle
INSERT INTO command_info (syntax, level, enabled, clazz, default_level)
SELECT 'showid', 2, 1, 'ShowIdCommand', 2
WHERE NOT EXISTS (SELECT 1 FROM command_info WHERE clazz = 'ShowIdCommand' AND syntax = 'showid');
