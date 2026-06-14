-- 每日探索地图池：增加地图名称和描述字段
ALTER TABLE `xy_daily_explore_map`
    ADD COLUMN `map_name` VARCHAR(100) DEFAULT '' COMMENT '地图名称（服务端自动解析）' AFTER `map_id`,
    ADD COLUMN `description` VARCHAR(255) DEFAULT '' COMMENT '地图描述（配置者填写）' AFTER `map_name`;