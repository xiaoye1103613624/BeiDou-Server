-- XY收集系统
CREATE TABLE IF NOT EXISTS `xy_collection_type`
(
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `type_name`     VARCHAR(128) NOT NULL COMMENT '类型名称',
    `description`   VARCHAR(500)          DEFAULT NULL COMMENT '类型描述',
    `sort_order`    INT          NOT NULL DEFAULT 0 COMMENT '排序',
    `enabled`       TINYINT(1)   NOT NULL DEFAULT 1 COMMENT '是否启用 0-否 1-是',
    `reward_type`   VARCHAR(32)           DEFAULT NULL COMMENT '类型完成奖励类型(CASH/MAPLE_POINT/MESO/AP)',
    `reward_amount` INT          NOT NULL DEFAULT 0 COMMENT '类型完成奖励数量',
    `create_time`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   TIMESTAMP    NULL     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='XY收集-类型配置';

CREATE TABLE IF NOT EXISTS `xy_collection_stage`
(
    `id`            BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `type_id`       BIGINT       NOT NULL COMMENT '类型ID',
    `stage_name`    VARCHAR(128) NOT NULL COMMENT '阶段名称',
    `sort_order`    INT          NOT NULL DEFAULT 0 COMMENT '排序',
    `reward_type`   VARCHAR(32)           DEFAULT NULL COMMENT '阶段奖励类型(CASH/MAPLE_POINT/MESO/AP)',
    `reward_amount` INT          NOT NULL DEFAULT 0 COMMENT '阶段奖励数量',
    `create_time`   TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`   TIMESTAMP    NULL     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_type_id` (`type_id`),
    CONSTRAINT `fk_stage_type` FOREIGN KEY (`type_id`) REFERENCES `xy_collection_type` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='XY收集-阶段配置';

CREATE TABLE IF NOT EXISTS `xy_collection_stage_item`
(
    `id`          BIGINT    NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `stage_id`    BIGINT    NOT NULL COMMENT '阶段ID',
    `item_id`     INT       NOT NULL COMMENT '物品ID',
    `quantity`    INT       NOT NULL DEFAULT 1 COMMENT '需求数量',
    `sort_order`  INT       NOT NULL DEFAULT 0 COMMENT '排序',
    `create_time` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` TIMESTAMP NULL     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    INDEX `idx_stage_id` (`stage_id`),
    CONSTRAINT `fk_item_stage` FOREIGN KEY (`stage_id`) REFERENCES `xy_collection_stage` (`id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='XY收集-阶段需求物品';

CREATE TABLE IF NOT EXISTS `xy_collection_progress`
(
    `id`               BIGINT    NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `character_id`     INT       NOT NULL COMMENT '角色ID',
    `type_id`          BIGINT    NOT NULL COMMENT '类型ID',
    `stage_id`         BIGINT    NOT NULL COMMENT '阶段ID',
    `item_id`          INT       NOT NULL COMMENT '物品ID',
    `collected_count`  INT       NOT NULL DEFAULT 0 COMMENT '已收集数量',
    `stage_completed`  TINYINT(1)         DEFAULT 0 COMMENT '阶段是否完成 0-否 1-是',
    `type_completed`   TINYINT(1)         DEFAULT 0 COMMENT '类型是否完成 0-否 1-是',
    `create_time`      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time`      TIMESTAMP NULL     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_char_stage_item` (`character_id`, `stage_id`, `item_id`),
    INDEX `idx_character_type` (`character_id`, `type_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci COMMENT ='XY收集-玩家收集进度';
