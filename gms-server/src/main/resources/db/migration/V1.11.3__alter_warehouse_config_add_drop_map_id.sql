-- 仓库配置表增加掉落地图ID（用于跑环VIP传送功能）
ALTER TABLE `xy_warehouse_config`
    ADD COLUMN `drop_map_id` INT NOT NULL DEFAULT 0 COMMENT '物品掉落地图ID（0=未知，用于脚本传送）'
    AFTER `item_name`;
