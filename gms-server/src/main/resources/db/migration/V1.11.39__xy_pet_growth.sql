-- 自定义宠物成长/进阶系统（配置驱动，Web 可改）
-- use_pet_growth_system=false 时完全走原版喂食，不影响客户端

INSERT INTO `game_config`(`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`, `update_time`)
SELECT 'server', 'Game Mechanics', 'java.lang.Boolean', 'use_pet_growth_system', 'true',
       '启用自定义宠物成长进阶（喂养经验满后进阶，召唤时叠加经验/爆率/金币倍率）', NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM `game_config` WHERE `config_code` = 'use_pet_growth_system'
);

INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'zh-CN', 'game_config', 'use_pet_growth_system', '启用自定义宠物成长进阶', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'zh-CN' AND `lang_code` = 'use_pet_growth_system'
);

INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'en-US', 'game_config', 'use_pet_growth_system', 'Enable custom pet growth / evolve', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'en-US' AND `lang_code` = 'use_pet_growth_system'
);

CREATE TABLE IF NOT EXISTS xy_pet_growth_stage (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    chain_code      VARCHAR(64)  NOT NULL COMMENT '进阶链编码，同链多阶段共用',
    stage           INT          NOT NULL DEFAULT 1 COMMENT '阶段序号，从1开始',
    name            VARCHAR(128) NOT NULL COMMENT '展示名',
    pet_id          INT          NOT NULL COMMENT '当前形态宠物物品ID(5000xxx)',
    next_pet_id     INT          NULL COMMENT '进阶目标宠物ID，空=终阶',
    need_exp        INT          NOT NULL DEFAULT 100 COMMENT '本阶段进阶所需成长经验',
    exp_per_feed    INT          NOT NULL DEFAULT 10 COMMENT '每次喂养增加成长经验',
    feed_item_ids   VARCHAR(255) NULL COMMENT '允许的喂养道具ID，逗号分隔；空=任意212宠物食品',
    exp_rate        DOUBLE       NOT NULL DEFAULT 1.0 COMMENT '召唤时经验倍率（乘算）',
    drop_rate       DOUBLE       NOT NULL DEFAULT 1.0 COMMENT '召唤时爆率倍率（乘算）',
    meso_rate       DOUBLE       NOT NULL DEFAULT 1.0 COMMENT '召唤时金币倍率（乘算）',
    sort_order      INT          NOT NULL DEFAULT 0,
    enabled         TINYINT      NOT NULL DEFAULT 1 COMMENT '0禁用 1启用',
    create_time     DATETIME     NULL DEFAULT CURRENT_TIMESTAMP,
    update_time     DATETIME     NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_pet_growth_pet_id (pet_id),
    KEY idx_pet_growth_chain (chain_code, stage)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物成长阶段配置';

CREATE TABLE IF NOT EXISTS xy_pet_growth_progress (
    petid       BIGINT NOT NULL COMMENT 'pets.petid',
    growth_exp  INT    NOT NULL DEFAULT 0,
    update_time DATETIME NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (petid)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宠物自定义成长经验进度';

-- 默认种子：现客户端已有外观的高版本三阶链（不含商城宠）
-- L1 倍率轻微，L2/L3 递增；喂养用任意宠物食品(212)
INSERT INTO xy_pet_growth_stage
(chain_code, stage, name, pet_id, next_pet_id, need_exp, exp_per_feed, feed_item_ids, exp_rate, drop_rate, meso_rate, sort_order, enabled)
VALUES
('hedgehog_pastel', 1, '粉嫩刺猬', 5000765, 5000766, 100, 10, NULL, 1.03, 1.02, 1.02, 10, 1),
('hedgehog_pastel', 2, '天蓝刺猬', 5000766, 5000767, 200, 10, NULL, 1.06, 1.04, 1.04, 11, 1),
('hedgehog_pastel', 3, '蛋黄刺猬', 5000767, NULL,     0,   10, NULL, 1.10, 1.06, 1.06, 12, 1),

('sisters_trio', 1, '珊珊', 5000772, 5000773, 100, 10, NULL, 1.03, 1.02, 1.02, 20, 1),
('sisters_trio', 2, '纳利', 5000773, 5000774, 200, 10, NULL, 1.06, 1.04, 1.04, 21, 1),
('sisters_trio', 3, '白露', 5000774, NULL,     0,   10, NULL, 1.10, 1.06, 1.06, 22, 1),

('mystic_cloud', 1, '神秘', 5000918, 5000919, 120, 10, NULL, 1.04, 1.02, 1.02, 30, 1),
('mystic_cloud', 2, '云云', 5000919, 5000920, 240, 10, NULL, 1.07, 1.04, 1.04, 31, 1),
('mystic_cloud', 3, '闪闪', 5000920, NULL,     0,   10, NULL, 1.12, 1.07, 1.07, 32, 1),

('peng_ping_feng', 1, '彭彭', 5000933, 5000934, 100, 10, NULL, 1.03, 1.02, 1.02, 40, 1),
('peng_ping_feng', 2, '平平', 5000934, 5000935, 200, 10, NULL, 1.06, 1.04, 1.04, 41, 1),
('peng_ping_feng', 3, '风风', 5000935, NULL,     0,   10, NULL, 1.10, 1.06, 1.06, 42, 1),

('serena_cosmos', 1, '赛瑞纳', 5000963, 5000964, 150, 10, NULL, 1.04, 1.03, 1.02, 50, 1),
('serena_cosmos', 2, '宇宙兔', 5000964, 5000965, 300, 10, NULL, 1.08, 1.05, 1.04, 51, 1),
('serena_cosmos', 3, '紫晶',   5000965, NULL,     0,   10, NULL, 1.12, 1.08, 1.06, 52, 1),

('bento_pets', 1, '玉子烧',   5002030, 5002031, 80,  10, NULL, 1.02, 1.02, 1.03, 60, 1),
('bento_pets', 2, '章鱼香肠', 5002031, 5002032, 160, 10, NULL, 1.04, 1.04, 1.05, 61, 1),
('bento_pets', 3, '饭团',     5002032, NULL,     0,   10, NULL, 1.06, 1.06, 1.08, 62, 1),

('small_trio', 1, '小乌帕', 5002048, 5002049, 100, 10, NULL, 1.03, 1.02, 1.02, 70, 1),
('small_trio', 2, '小镥',   5002049, 5002050, 200, 10, NULL, 1.06, 1.04, 1.04, 71, 1),
('small_trio', 3, '小葱',   5002050, NULL,     0,   10, NULL, 1.10, 1.06, 1.06, 72, 1),

('fruit_hedgehog', 1, '桃子刺猬', 5002057, 5002058, 100, 10, NULL, 1.03, 1.03, 1.02, 80, 1),
('fruit_hedgehog', 2, '蓝莓刺猬', 5002058, 5002059, 200, 10, NULL, 1.06, 1.05, 1.04, 81, 1),
('fruit_hedgehog', 3, '樱桃刺猬', 5002059, NULL,     0,   10, NULL, 1.10, 1.08, 1.06, 82, 1),

('tiny_cats', 1, '娇小白猫',   5002110, 5002111, 120, 10, NULL, 1.03, 1.02, 1.02, 90, 1),
('tiny_cats', 2, '娇小棕纹猫', 5002111, NULL,     0,   10, NULL, 1.08, 1.05, 1.05, 91, 1);
