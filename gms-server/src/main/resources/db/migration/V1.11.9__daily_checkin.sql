-- 每日签到：characters 表增加 streak 字段 + 注册 @签到 命令

ALTER TABLE `characters`
    ADD COLUMN `checkinDay` INT NOT NULL DEFAULT 0 COMMENT '本周期已领取天数(0..28)',
    ADD COLUMN `checkinClaimed` INT NOT NULL DEFAULT 0 COMMENT '本周期28天领取位图',
    ADD COLUMN `checkinLastClaim` BIGINT NOT NULL DEFAULT 0 COMMENT '上次领取时间戳(毫秒)';

INSERT INTO command_info (syntax, level, enabled, clazz, default_level)
SELECT '签到', 0, 1, 'CheckinCommand', 0
WHERE NOT EXISTS (SELECT 1 FROM command_info WHERE clazz = 'CheckinCommand' AND syntax = '签到');

INSERT INTO command_info (syntax, level, enabled, clazz, default_level)
SELECT 'daily', 0, 1, 'CheckinCommand', 0
WHERE NOT EXISTS (SELECT 1 FROM command_info WHERE clazz = 'CheckinCommand' AND syntax = 'daily');
