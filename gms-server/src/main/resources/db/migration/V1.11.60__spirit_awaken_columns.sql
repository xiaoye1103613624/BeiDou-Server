-- 灵韵觉醒：武器附加技能字段（equipSkillId / equipSkillLevel / equipSkillExpire）
-- 幂等：列已存在时跳过，避免重复执行报错。

SET @db := DATABASE();

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='inventoryequipment' AND COLUMN_NAME='equipSkillId');
SET @sql := IF(@exists=0, 'ALTER TABLE inventoryequipment ADD COLUMN equipSkillId INT NOT NULL DEFAULT 0 COMMENT ''灵韵技能ID'' AFTER anvilItemId', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='inventoryequipment' AND COLUMN_NAME='equipSkillLevel');
SET @sql := IF(@exists=0, 'ALTER TABLE inventoryequipment ADD COLUMN equipSkillLevel INT NOT NULL DEFAULT 0 COMMENT ''灵韵技能等级'' AFTER equipSkillId', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

SET @exists := (SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=@db AND TABLE_NAME='inventoryequipment' AND COLUMN_NAME='equipSkillExpire');
SET @sql := IF(@exists=0, 'ALTER TABLE inventoryequipment ADD COLUMN equipSkillExpire BIGINT NOT NULL DEFAULT 0 COMMENT ''灵韵过期时间，0=永久'' AFTER equipSkillLevel', 'SELECT 1');
PREPARE stmt FROM @sql; EXECUTE stmt; DEALLOCATE PREPARE stmt;

-- 匠人街材料商人：灵韵结晶 4021017（测试/投放通道）
INSERT INTO shopitems (shopid, itemid, price, pitch, position)
SELECT 9031007, 4021017, 2000000, 0,
       COALESCE((SELECT MAX(position) FROM shopitems WHERE shopid = 9031007), 0) + 1
FROM DUAL
WHERE NOT EXISTS (SELECT 1 FROM shopitems WHERE shopid = 9031007 AND itemid = 4021017);
