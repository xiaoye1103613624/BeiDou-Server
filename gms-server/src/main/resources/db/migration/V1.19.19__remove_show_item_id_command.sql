-- Revert showid GM command (show item id feature removed).
DELETE FROM command_info WHERE clazz = 'ShowIdCommand' AND syntax = 'showid';
