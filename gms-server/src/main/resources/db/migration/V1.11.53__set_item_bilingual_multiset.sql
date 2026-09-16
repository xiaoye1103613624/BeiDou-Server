-- 套装双语名称 + 装备清单扩容（支持一装多套场景下更长 item_ids）
ALTER TABLE `xy_set_item`
    ADD COLUMN `set_name_zh` VARCHAR(255) DEFAULT NULL COMMENT '套装中文名' AFTER `set_name`,
    ADD COLUMN `set_name_en` VARCHAR(255) DEFAULT NULL COMMENT '套装英文名' AFTER `set_name_zh`,
    MODIFY COLUMN `item_ids` TEXT DEFAULT NULL;

-- 回填：含中日韩统一表意文字的视为中文名，否则视为英文名
UPDATE `xy_set_item`
SET `set_name_zh` = `set_name`
WHERE `set_name` IS NOT NULL
  AND `set_name` <> ''
  AND `set_name` REGEXP '[一-龥]';

UPDATE `xy_set_item`
SET `set_name_en` = `set_name`
WHERE `set_name` IS NOT NULL
  AND `set_name` <> ''
  AND (`set_name_zh` IS NULL OR `set_name_zh` = '');

-- 展示主名：优先中文，否则英文（兼容旧 set_name 读路径）
UPDATE `xy_set_item`
SET `set_name` = COALESCE(NULLIF(`set_name_zh`, ''), NULLIF(`set_name_en`, ''), `set_name`);
