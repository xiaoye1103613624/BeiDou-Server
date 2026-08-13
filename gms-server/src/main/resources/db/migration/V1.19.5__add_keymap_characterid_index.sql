-- 修复角色保存时 keymap 表 DELETE/SELECT WHERE characterid = ?
-- 幂等：索引已存在则跳过。
-- 注：S8 独有索引；版本顺延至 1.19.5（不在中间插号）。
SET @exist := (
    SELECT COUNT(1) FROM information_schema.statistics
    WHERE table_schema = DATABASE()
      AND table_name = 'keymap'
      AND index_name = 'idx_characterid_key'
);
SET @sqlstmt := IF(
    @exist = 0,
    'ALTER TABLE `keymap` ADD UNIQUE INDEX `idx_characterid_key` (`characterid`, `key`)',
    'SELECT ''idx_characterid_key already exists'''
);
PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
