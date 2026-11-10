-- ============================================================
-- 注意：本脚本已同步为 Flyway 迁移
--   V1.9.5__set_max_level_999.sql，随服务启动自动执行。
--   本文件保留作手动执行备份；两者只需其一（INSERT ... NOT EXISTS 幂等）。
-- 表：game_config —— 游戏运营参数表（建表见 V1.7.0__create_game_config.sql）
-- 业务背景：等级上限从 255 提升到 999 后，Character.getMaxClassLevel() 通过
--           GameConfig.getServerInt("max_level") 读取上限；未配置时回退 255，
--           !level / 升级判定 / 远征入场 / 家族谱系等级仍会被 255 卡住。
-- 影响：写入后全服角色等级上限变为 999；不改任何既有配置行，仅新增一行参数。
--       config_type='server'（getServerInt 只查 server 大类），
--       config_sub_type='Core'，config_clazz='java.lang.Integer'（getIntValue）。
-- 幂等：仅当 config_type='server' 且 config_code='max_level' 不存在时插入。
-- 备份：执行前请备份 game_config（SELECT * 或 mysqldump）。
-- 回滚：DELETE FROM game_config WHERE config_type='server' AND config_code='max_level';
--       （删行后回退到代码内 255 默认值；Flyway 历史记录需另行处理）
-- 前置：V1.9.3（characters.exp / gachaexp 扩为 BIGINT）已于 2026-11-06 只读
--       核查为 flyway_schema_history success=1、列类型 bigint，无需再执行。
-- 说明：本脚本为 DML（INSERT ... SELECT），game_config 各列在建表脚本中均有备注；
--       此处补业务背景、影响与回滚说明。
-- ============================================================

INSERT INTO `game_config`
    (`config_type`, `config_sub_type`, `config_clazz`, `config_code`, `config_value`, `config_desc`, `update_time`)
SELECT 'server', 'Core', 'java.lang.Integer', 'max_level', '999',
       '角色等级上限，999 级改造后由 Character.getMaxClassLevel() 读取；未配置时回退 255',
       NOW()
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM `game_config`
    WHERE `config_type` = 'server' AND `config_code` = 'max_level'
);

-- ---------- 执行后只读校验 ----------
SELECT COLUMN_NAME, COLUMN_TYPE FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = 'beidou' AND TABLE_NAME = 'characters'
  AND COLUMN_NAME IN ('exp', 'gachaexp');

SELECT config_type, config_sub_type, config_code, config_value, config_clazz
FROM game_config WHERE config_code = 'max_level';
