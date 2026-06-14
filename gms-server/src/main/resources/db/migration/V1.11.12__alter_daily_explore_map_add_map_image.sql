-- 每日探索地图池：增加地图图片字段（base64存储，用于Web端展示）
ALTER TABLE `xy_daily_explore_map`
    ADD COLUMN `map_image` LONGTEXT COMMENT '地图渲染图片（base64 data URL，从maplestory.io爬取缓存）' AFTER `description`;
