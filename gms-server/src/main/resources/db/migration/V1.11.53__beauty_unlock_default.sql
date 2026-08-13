-- Beauty salon: unlock all slots that are still 0; ensure @beauty alias exists.

UPDATE xy_beautyunlock SET slots = 6 WHERE slots IS NULL OR slots <= 0;

INSERT INTO command_info (syntax, level, enabled, clazz, default_level)
SELECT 'beauty', 0, 1, 'BeautyCommand', 0
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM command_info WHERE clazz = 'BeautyCommand' AND syntax = 'beauty'
);
