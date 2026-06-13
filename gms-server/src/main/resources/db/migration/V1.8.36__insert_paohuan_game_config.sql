-- ================================================================
-- 每日跑环系统配置
-- 用途：玩家每日可完成多环任务，每环随机分配击杀目标，环数越高奖励越多
-- 参数说明：
--   paohuan_daily_limit  = 每日可完成跑环次数上限（默认20）
--   paohuan_exp_per_ring = 每环基础经验（EXP = 环数 × 此值）
--   paohuan_meso_per_ring = 每环基础金币（Meso = 环数 × 此值）
-- 关联表：xy_paohuan_config（跑环怪物/奖励配置表）
-- ================================================================
-- 每日跑环上限（角色每日可完成的跑环次数，默认20）
INSERT INTO `game_config`(`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`, `update_time`)
SELECT 'server', 'Game Mechanics', 'java.lang.Integer', 'paohuan_daily_limit', '20', '每日跑环上限次数', NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM `game_config` WHERE `config_code` = 'paohuan_daily_limit'
);

-- 每环基础经验（EXP = 环数 × 此值）
INSERT INTO `game_config`(`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`, `update_time`)
SELECT 'server', 'Game Mechanics', 'java.lang.Integer', 'paohuan_exp_per_ring', '10000', '每环基础经验奖励', NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM `game_config` WHERE `config_code` = 'paohuan_exp_per_ring'
);

-- 每环基础金币（Meso = 环数 × 此值）
INSERT INTO `game_config`(`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`, `update_time`)
SELECT 'server', 'Game Mechanics', 'java.lang.Integer', 'paohuan_meso_per_ring', '10000', '每环基础金币奖励', NOW()
WHERE NOT EXISTS (
    SELECT 1 FROM `game_config` WHERE `config_code` = 'paohuan_meso_per_ring'
);

-- 中文内容
INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'zh-CN', 'game_config', 'paohuan_daily_limit', '每日跑环上限次数', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'zh-CN' AND `lang_code` = 'paohuan_daily_limit'
);

INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'zh-CN', 'game_config', 'paohuan_exp_per_ring', '每环基础经验奖励', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'zh-CN' AND `lang_code` = 'paohuan_exp_per_ring'
);

INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'zh-CN', 'game_config', 'paohuan_meso_per_ring', '每环基础金币奖励', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'zh-CN' AND `lang_code` = 'paohuan_meso_per_ring'
);

-- 英文内容
INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'en-US', 'game_config', 'paohuan_daily_limit', 'Daily max loop count.', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'en-US' AND `lang_code` = 'paohuan_daily_limit'
);

INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'en-US', 'game_config', 'paohuan_exp_per_ring', 'Base EXP reward per ring.', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'en-US' AND `lang_code` = 'paohuan_exp_per_ring'
);

INSERT INTO `lang_resources`(`lang_type`, `lang_base`, `lang_code`, `lang_value`, `lang_extend`)
SELECT 'en-US', 'game_config', 'paohuan_meso_per_ring', 'Base meso reward per ring.', NULL
WHERE NOT EXISTS (
    SELECT 1 FROM `lang_resources` WHERE `lang_type` = 'en-US' AND `lang_code` = 'paohuan_meso_per_ring'
);
