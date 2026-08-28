-- V1.11.14: 套装配置表（WZ 花名册 + DB 覆盖）
CREATE TABLE IF NOT EXISTS `xy_set_item` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `set_id` INT NOT NULL,
    `set_name` VARCHAR(255) DEFAULT NULL,
    `complete_count` INT NOT NULL DEFAULT 0,
    `item_ids` VARCHAR(1024) DEFAULT NULL,
    `enabled` INT NOT NULL DEFAULT 1,
    `sort_order` INT NOT NULL DEFAULT 0,
    `remark` VARCHAR(255) DEFAULT NULL,
    `tiers_json` LONGTEXT DEFAULT NULL,
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_xy_set_item_set_id` (`set_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
