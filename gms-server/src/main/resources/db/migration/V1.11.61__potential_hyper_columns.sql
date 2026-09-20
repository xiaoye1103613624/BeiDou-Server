-- 潜能 / Hyper 强化：装备附加字段（enhance、主/附加潜能、灵魂宝珠、星岩槽）
-- 幂等：列已存在时跳过，避免重复执行报错。
-- 注意：这些字段只落 DB + 通过 USER_INFO_EX 下发，不写进 addItemInfo 装备包尾（会破坏 ijl15 Decode）。

SET @db := DATABASE();

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='inventoryequipment' AND COLUMN_NAME='enhance');
SET @sql := IF(@exists=0, 'ALTER TABLE inventoryequipment ADD COLUMN enhance TINYINT NOT NULL DEFAULT 0 COMMENT ''Hyper强化星级'' AFTER reforgeLock', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='inventoryequipment' AND COLUMN_NAME='potentialGrade');
SET @sql := IF(@exists=0, 'ALTER TABLE inventoryequipment ADD COLUMN potentialGrade TINYINT NOT NULL DEFAULT 0 COMMENT ''主潜能品阶1~5'' AFTER enhance', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='inventoryequipment' AND COLUMN_NAME='potential1');
SET @sql := IF(@exists=0, 'ALTER TABLE inventoryequipment ADD COLUMN potential1 INT NOT NULL DEFAULT 0 COMMENT ''主潜能词条1'' AFTER potentialGrade', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='inventoryequipment' AND COLUMN_NAME='potential2');
SET @sql := IF(@exists=0, 'ALTER TABLE inventoryequipment ADD COLUMN potential2 INT NOT NULL DEFAULT 0 COMMENT ''主潜能词条2'' AFTER potential1', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='inventoryequipment' AND COLUMN_NAME='potential3');
SET @sql := IF(@exists=0, 'ALTER TABLE inventoryequipment ADD COLUMN potential3 INT NOT NULL DEFAULT 0 COMMENT ''主潜能词条3'' AFTER potential2', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='inventoryequipment' AND COLUMN_NAME='bonusPotentialGrade');
SET @sql := IF(@exists=0, 'ALTER TABLE inventoryequipment ADD COLUMN bonusPotentialGrade TINYINT NOT NULL DEFAULT 0 COMMENT ''附加潜能品阶'' AFTER potential3', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='inventoryequipment' AND COLUMN_NAME='bonusPotential1');
SET @sql := IF(@exists=0, 'ALTER TABLE inventoryequipment ADD COLUMN bonusPotential1 INT NOT NULL DEFAULT 0 COMMENT ''附加潜能词条1'' AFTER bonusPotentialGrade', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='inventoryequipment' AND COLUMN_NAME='bonusPotential2');
SET @sql := IF(@exists=0, 'ALTER TABLE inventoryequipment ADD COLUMN bonusPotential2 INT NOT NULL DEFAULT 0 COMMENT ''附加潜能词条2'' AFTER bonusPotential1', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='inventoryequipment' AND COLUMN_NAME='bonusPotential3');
SET @sql := IF(@exists=0, 'ALTER TABLE inventoryequipment ADD COLUMN bonusPotential3 INT NOT NULL DEFAULT 0 COMMENT ''附加潜能词条3'' AFTER bonusPotential2', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='inventoryequipment' AND COLUMN_NAME='soulId');
SET @sql := IF(@exists=0, 'ALTER TABLE inventoryequipment ADD COLUMN soulId INT NOT NULL DEFAULT 0 COMMENT ''灵魂宝珠怪物ID'' AFTER bonusPotential3', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='inventoryequipment' AND COLUMN_NAME='soulOption');
SET @sql := IF(@exists=0, 'ALTER TABLE inventoryequipment ADD COLUMN soulOption INT NOT NULL DEFAULT 0 COMMENT ''灵魂宝珠选项'' AFTER soulId', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='inventoryequipment' AND COLUMN_NAME='socket1');
SET @sql := IF(@exists=0, 'ALTER TABLE inventoryequipment ADD COLUMN socket1 INT NOT NULL DEFAULT 0 COMMENT ''星岩槽1'' AFTER soulOption', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='inventoryequipment' AND COLUMN_NAME='socket2');
SET @sql := IF(@exists=0, 'ALTER TABLE inventoryequipment ADD COLUMN socket2 INT NOT NULL DEFAULT 0 COMMENT ''星岩槽2'' AFTER socket1', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='inventoryequipment' AND COLUMN_NAME='socket3');
SET @sql := IF(@exists=0, 'ALTER TABLE inventoryequipment ADD COLUMN socket3 INT NOT NULL DEFAULT 0 COMMENT ''星岩槽3'' AFTER socket2', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;
