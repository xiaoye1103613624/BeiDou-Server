-- 窗口现金商城（可管理分类/商品）+ 可配置客户端 Data 根目录

CREATE TABLE IF NOT EXISTS `xy_cashshop_category`
(
    `id`              INT          NOT NULL AUTO_INCREMENT COMMENT '分类ID',
    `name`            VARCHAR(64)  NOT NULL COMMENT '显示名',
    `parent_id`       INT          NULL     DEFAULT NULL COMMENT '父分类，NULL=顶栏',
    `sort`            INT          NOT NULL DEFAULT 0 COMMENT '排序，越小越靠前',
    `enabled`         TINYINT      NOT NULL DEFAULT 1 COMMENT '是否启用 1/0',
    `click_type`      VARCHAR(32)  NOT NULL DEFAULT 'SHOW_ITEMS' COMMENT 'SHOW_ITEMS|OPEN_WINDOW|SEND_PACKET|…',
    `click_param`     VARCHAR(255) NULL     DEFAULT NULL COMMENT '窗口ID/协议载荷等',
    `gate_item_id`    INT          NULL     DEFAULT NULL COMMENT '持有该道具才可见，NULL=不限',
    `is_hot`          TINYINT      NOT NULL DEFAULT 0 COMMENT '热卖榜类分类 1/0',
    `legacy_tab`      INT          NULL     DEFAULT NULL COMMENT '兼容现客户端 tab',
    `legacy_category` INT          NULL     DEFAULT NULL COMMENT '兼容现客户端 category',
    `remark`          VARCHAR(255) NULL     DEFAULT NULL,
    `updated_at`      TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    KEY `idx_parent_sort` (`parent_id`, `sort`),
    KEY `idx_legacy` (`legacy_tab`, `legacy_category`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='窗口商城分类';

CREATE TABLE IF NOT EXISTS `xy_cashshop_item`
(
    `item_id`    INT          NOT NULL COMMENT '物品ID（主键）',
    `price`      INT          NOT NULL DEFAULT 0 COMMENT '点券价格',
    `count`      INT          NOT NULL DEFAULT 1 COMMENT '数量',
    `period`     INT          NOT NULL DEFAULT 0 COMMENT '有效期(天)，0=永久',
    `gender`     INT          NOT NULL DEFAULT 0 COMMENT '性别 0双/1男/2女',
    `name`       VARCHAR(64)  NOT NULL DEFAULT '' COMMENT '展示名（可覆盖 String.wz）',
    `icon_url`   VARCHAR(512) NULL     DEFAULT NULL COMMENT 'Web管理缩略图，游戏内仍读WZ',
    `enabled`    TINYINT      NOT NULL DEFAULT 1 COMMENT '是否启用',
    `remark`     VARCHAR(255) NULL     DEFAULT NULL,
    `updated_at` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`item_id`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='窗口商城商品主数据';

CREATE TABLE IF NOT EXISTS `xy_cashshop_category_item`
(
    `id`          INT      NOT NULL AUTO_INCREMENT,
    `category_id` INT      NOT NULL COMMENT '分类ID',
    `item_id`     INT      NOT NULL COMMENT '物品ID',
    `sort`        INT      NOT NULL DEFAULT 0 COMMENT '分类内排序',
    `enabled`     TINYINT  NOT NULL DEFAULT 1 COMMENT '该分类下是否显示',
    `updated_at`  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_cat_item` (`category_id`, `item_id`),
    KEY `idx_item` (`item_id`),
    CONSTRAINT `fk_xy_cs_ci_cat` FOREIGN KEY (`category_id`) REFERENCES `xy_cashshop_category` (`id`) ON DELETE CASCADE,
    CONSTRAINT `fk_xy_cs_ci_item` FOREIGN KEY (`item_id`) REFERENCES `xy_cashshop_item` (`item_id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4 COMMENT ='窗口商城分类-商品关联（热卖可多挂）';

-- 客户端 Data 根目录：空=不做客户端资源校验；可随时在管理端/参数页改，勿写死路径
INSERT INTO `game_config`(`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`,
                          `update_time`)
SELECT 'server',
       'GM',
       'java.lang.String',
       'window_cashshop_client_data_path',
       '',
       '窗口商城校验用的客户端 Data 根目录（绝对路径，可换客户端时修改；空=跳过客户端存在性校验）',
       NOW()
WHERE NOT EXISTS (SELECT 1 FROM `game_config` WHERE `config_code` = 'window_cashshop_client_data_path');

INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'zh-CN',
       'game_config',
       'window_cashshop_client_data_path',
       '窗口商城校验用的客户端 Data 根目录（绝对路径；空=跳过客户端校验）',
       NULL
WHERE NOT EXISTS (SELECT 1
                  FROM `lang_resources`
                  WHERE `lang_type` = 'zh-CN'
                    AND `lang_code` = 'window_cashshop_client_data_path');

INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'en-US',
       'game_config',
       'window_cashshop_client_data_path',
       'Client Data root for window cash-shop asset checks (absolute path; empty skips)',
       NULL
WHERE NOT EXISTS (SELECT 1
                  FROM `lang_resources`
                  WHERE `lang_type` = 'en-US'
                    AND `lang_code` = 'window_cashshop_client_data_path');
