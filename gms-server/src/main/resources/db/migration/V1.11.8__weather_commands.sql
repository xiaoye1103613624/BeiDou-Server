-- Weather / day-night GM commands
INSERT INTO command_info (syntax, level, enabled, clazz, default_level)
SELECT 'weather', 2, 1, 'WeatherCommand', 2
WHERE NOT EXISTS (SELECT 1 FROM command_info WHERE clazz = 'WeatherCommand' AND syntax = 'weather');

INSERT INTO command_info (syntax, level, enabled, clazz, default_level)
SELECT 'night', 3, 1, 'NightCommand', 3
WHERE NOT EXISTS (SELECT 1 FROM command_info WHERE clazz = 'NightCommand' AND syntax = 'night');

INSERT INTO command_info (syntax, level, enabled, clazz, default_level)
SELECT 'lamp', 3, 1, 'LampCommand', 3
WHERE NOT EXISTS (SELECT 1 FROM command_info WHERE clazz = 'LampCommand' AND syntax = 'lamp');
