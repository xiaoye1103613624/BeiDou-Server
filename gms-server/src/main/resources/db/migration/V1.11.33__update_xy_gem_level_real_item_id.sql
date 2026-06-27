-- 填充宝石合成真实数据：1~10级装备宝石的物品ID/名称，并启用对应等级
-- 来源：WZ Etc.img 4440xxx ~ 4443xxx 分组中的"N级装备宝石"条目（仅取装备宝石，A/S级属性宝石不在合成链中）
UPDATE `xy_gem_level` SET `item_id` = 4441300, `item_name` = '1级装备宝石', `enabled` = 1 WHERE `gem_level` = 1;
UPDATE `xy_gem_level` SET `item_id` = 4443300, `item_name` = '2级装备宝石', `enabled` = 1 WHERE `gem_level` = 2;
UPDATE `xy_gem_level` SET `item_id` = 4442300, `item_name` = '3级装备宝石', `enabled` = 1 WHERE `gem_level` = 3;
UPDATE `xy_gem_level` SET `item_id` = 4440300, `item_name` = '4级装备宝石', `enabled` = 1 WHERE `gem_level` = 4;
UPDATE `xy_gem_level` SET `item_id` = 4441200, `item_name` = '5级装备宝石', `enabled` = 1 WHERE `gem_level` = 5;
UPDATE `xy_gem_level` SET `item_id` = 4443200, `item_name` = '6级装备宝石', `enabled` = 1 WHERE `gem_level` = 6;
UPDATE `xy_gem_level` SET `item_id` = 4442200, `item_name` = '7级装备宝石', `enabled` = 1 WHERE `gem_level` = 7;
UPDATE `xy_gem_level` SET `item_id` = 4440200, `item_name` = '8级装备宝石', `enabled` = 1 WHERE `gem_level` = 8;
UPDATE `xy_gem_level` SET `item_id` = 4441101, `item_name` = '9级装备宝石', `enabled` = 1 WHERE `gem_level` = 9;
UPDATE `xy_gem_level` SET `item_id` = 4443101, `item_name` = '10级装备宝石', `enabled` = 1 WHERE `gem_level` = 10;
