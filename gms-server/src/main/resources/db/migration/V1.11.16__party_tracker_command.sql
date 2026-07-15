INSERT INTO command_info (syntax, level, enabled, clazz, default_level)
SELECT 'partytracker', 0, 1, 'PartyTrackerCommand', 0
WHERE NOT EXISTS (SELECT 1 FROM command_info WHERE clazz = 'PartyTrackerCommand' AND syntax = 'partytracker');
