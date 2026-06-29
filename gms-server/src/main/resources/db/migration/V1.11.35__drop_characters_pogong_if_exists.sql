-- 删除characters表中手动添加的pogong字段（实际破功系统使用extra_damage_cap字段，pogong为冗余字段）
-- 使用存储过程安全删除：列存在则删除，不存在则跳过
DROP PROCEDURE IF EXISTS drop_pogong_if_exists;
DELIMITER $$
CREATE PROCEDURE drop_pogong_if_exists()
BEGIN
    DECLARE col_count INT DEFAULT 0;
    SELECT COUNT(*) INTO col_count
    FROM information_schema.COLUMNS
    WHERE TABLE_SCHEMA = DATABASE()
      AND TABLE_NAME = 'characters'
      AND COLUMN_NAME = 'pogong';
    IF col_count > 0 THEN
        ALTER TABLE `characters` DROP COLUMN `pogong`;
    END IF;
END$$
DELIMITER ;
CALL drop_pogong_if_exists();
DROP PROCEDURE IF EXISTS drop_pogong_if_exists;
