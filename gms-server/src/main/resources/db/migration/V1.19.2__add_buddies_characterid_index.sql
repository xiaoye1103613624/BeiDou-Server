-- 修复角色保存时 buddies 表 DELETE WHERE characterid = ?
-- 因 characterid 列无索引导致全表扫描加锁，多角色并发下线（尤其互为好友）时
-- 偶发 1213 Deadlock。补上索引以避免死锁。
-- 幂等：库若已手工/提前建过 idx_characterid 则跳过，避免 Flyway failed migration。
SET @exist := (
    SELECT COUNT(1)
    FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'buddies'
      AND index_name = 'idx_characterid'
);
SET @sqlstmt := IF(
    @exist = 0,
    'ALTER TABLE `buddies` ADD INDEX `idx_characterid` (`characterid`)',
    'SELECT ''idx_characterid already exists'''
);
PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
