-- 火花/涅槃：exGradeOption 编码（对齐 265）；属性虚拟加算，不进 GW 0x140 尾。
SET @exist := (
    SELECT COUNT(1) FROM information_schema.columns
    WHERE table_schema = DATABASE()
      AND table_name = 'inventoryequipment'
      AND column_name = 'exGradeOption'
);
SET @sqlstmt := IF(
    @exist = 0,
    'ALTER TABLE `inventoryequipment` ADD COLUMN `exGradeOption` BIGINT NOT NULL DEFAULT 0 COMMENT ''火花exGradeOption(265兼容编码)'' AFTER `chaosJump`',
    'SELECT ''inventoryequipment.exGradeOption already exists'''
);
PREPARE stmt FROM @sqlstmt;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;
