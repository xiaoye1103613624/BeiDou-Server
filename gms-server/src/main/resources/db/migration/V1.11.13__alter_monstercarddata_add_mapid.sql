-- ==========================================
-- monstercarddata - 增加地图ID字段，用于按地区分类展示卡片收集
-- ==========================================

-- 1. 新增mapid字段（先允许NULL，数据填充后再设为NOT NULL）
ALTER TABLE `monstercarddata`
    ADD COLUMN `mapid` INT(11) DEFAULT NULL COMMENT '卡片所属地区地图ID（用于按地区分类展示）' AFTER `mobid`;

-- 2. 根据卡片ID区间填充地图ID
-- 对应官方怪物图鉴9个分册，每个区间对应一个主要城镇/区域
UPDATE `monstercarddata`
SET `mapid` = CASE
    WHEN `cardid` >= 2380000 AND `cardid` < 2381000 THEN 1000000   -- 明珠港/新手区（彩虹岛）
    WHEN `cardid` >= 2381000 AND `cardid` < 2382000 THEN 100000000  -- 射手村（金银岛）
    WHEN `cardid` >= 2382000 AND `cardid` < 2383000 THEN 211000000  -- 冰峰雪域
    WHEN `cardid` >= 2383000 AND `cardid` < 2384000 THEN 220000000  -- 玩具城
    WHEN `cardid` >= 2384000 AND `cardid` < 2385000 THEN 240000000  -- 神木村
    WHEN `cardid` >= 2385000 AND `cardid` < 2386000 THEN 250000000  -- 武陵
    WHEN `cardid` >= 2386000 AND `cardid` < 2387000 THEN 260000000  -- 阿里安特（纳希沙漠）
    WHEN `cardid` >= 2387000 AND `cardid` < 2388000 THEN 270000100  -- 时间神殿
    WHEN `cardid` >= 2388000 AND `cardid` < 2389000 THEN 200000000  -- 天空之城（特殊/Boss卡片）
    ELSE 100000000  -- 兜底：默认归入射手村
END;

-- 3. 改为NOT NULL
ALTER TABLE `monstercarddata`
    MODIFY COLUMN `mapid` INT(11) NOT NULL COMMENT '卡片所属地区地图ID（用于按地区分类展示）';

-- 4. 添加索引加速按地区查询
ALTER TABLE `monstercarddata`
    ADD INDEX `idx_mapid` (`mapid`);
