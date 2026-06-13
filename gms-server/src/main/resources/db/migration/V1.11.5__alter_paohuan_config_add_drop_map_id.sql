-- 跑环物品池配置表增加掉落地图ID（用于VIP传送功能）
ALTER TABLE `xy_paohuan_config`
    ADD COLUMN `drop_map_id` INT NOT NULL DEFAULT 0 COMMENT '物品掉落地图ID（0=未知，用于VIP传送）'
    AFTER `quantity`;
