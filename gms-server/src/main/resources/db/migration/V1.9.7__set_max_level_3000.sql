-- ============================================================
-- 等级上限硬性 3000（2026-11-11）
-- 表：game_config —— 角色等级上限参数（建表见 V1.7.0__create_game_config.sql）
-- 业务背景：等级上限从 999 提升到 3000。Character.getMaxClassLevel() 已代码钳制
--           MAX_LEVEL_CAP=3000（即使本行误配更大值也不超 3000），防止按现有
--           1.06 递增曲线时累计经验在 ~3135 级溢出 int64（9.2e18）。
-- 影响：全服角色等级上限变为 3000；GM 命令 !level、升级经验判定、远征入场、
--       家族谱系等级均随之上移。客户端 ExpTable 已支持 3000 级动态经验曲线。
-- 幂等：先 INSERT 兜底（若 V1.9.5 未执行导致 max_level 缺失），再 UPDATE 将
--       既有的 999 改为 3000；重复执行无副作用。
-- 回滚：UPDATE game_config SET config_value='999'
--             WHERE config_type='server' AND config_code='max_level';
-- ============================================================

-- 兜底插入（若 V1.9.5 未执行导致 max_level 缺失）
INSERT INTO `game_config`
    (`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`, `update_time`)
SELECT 'server', 'Core', 'java.lang.Integer', 'max_level', '3000',
       '角色等级上限，硬性 3000（代码钳制上限，配置不得超 3000，防止经验累计溢出 int64）',
       NOW()
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `game_config` WHERE `config_type` = 'server' AND `config_code` = 'max_level'
);

-- 将既有 max_level（如 V1.9.5 写入的 999）更新为 3000
UPDATE `game_config`
SET `config_value` = '3000',
    `config_desc` = '角色等级上限，硬性 3000（代码钳制上限，配置不得超 3000，防止经验累计溢出 int64）',
    `update_time` = NOW()
WHERE `config_type` = 'server' AND `config_code` = 'max_level';
