-- 幂等补索引（库可能已提前建过）
SET @exist := (
    SELECT COUNT(1) FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'wishlists' AND index_name = 'idx_charid'
);
SET @sqlstmt := IF(@exist = 0,
    'ALTER TABLE `wishlists` ADD INDEX `idx_charid` (`charid`)',
    'SELECT ''wishlists.idx_charid already exists''');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exist := (
    SELECT COUNT(1) FROM information_schema.statistics
    WHERE table_schema = DATABASE() AND table_name = 'inventoryitems' AND index_name = 'idx_accountid'
);
SET @sqlstmt := IF(@exist = 0,
    'ALTER TABLE `inventoryitems` ADD INDEX `idx_accountid` (`accountid`)',
    'SELECT ''inventoryitems.idx_accountid already exists''');
PREPARE stmt FROM @sqlstmt; EXECUTE stmt; DEALLOCATE PREPARE stmt;
