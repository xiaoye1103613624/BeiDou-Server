-- V1.11.8: 萧曳北斗自定义表统一 xy_ 前缀
-- 迁移链：V1.11.4/V1.11.5 创建旧表名 -> 本脚本重命名为 xy_*（新库与已有库均适用）

-- beautystorage -> xy_beautystorage
SET @tbl := (SELECT COUNT(*) FROM information_schema.tables
             WHERE table_schema = DATABASE() AND table_name = 'beautystorage');
SET @sql := IF(@tbl > 0, 'RENAME TABLE beautystorage TO xy_beautystorage', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- beautyunlock -> xy_beautyunlock
SET @tbl := (SELECT COUNT(*) FROM information_schema.tables
             WHERE table_schema = DATABASE() AND table_name = 'beautyunlock');
SET @sql := IF(@tbl > 0, 'RENAME TABLE beautyunlock TO xy_beautyunlock', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- damageskin_catalog -> xy_damageskin_catalog
SET @tbl := (SELECT COUNT(*) FROM information_schema.tables
             WHERE table_schema = DATABASE() AND table_name = 'damageskin_catalog');
SET @sql := IF(@tbl > 0, 'RENAME TABLE damageskin_catalog TO xy_damageskin_catalog', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- damageskin_inventory -> xy_damageskin_inventory
SET @tbl := (SELECT COUNT(*) FROM information_schema.tables
             WHERE table_schema = DATABASE() AND table_name = 'damageskin_inventory');
SET @sql := IF(@tbl > 0, 'RENAME TABLE damageskin_inventory TO xy_damageskin_inventory', 'SELECT 1');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
