-- ============================================================
-- 注意：项目根目录 migrate_max_level_999.sql 保留作手动执行备份；
--       两者只需其一（INSERT ... NOT EXISTS 幂等，重复执行无副作用）。
-- 表：game_config —— 游戏运营参数表（建表见 V1.7.0__create_game_config.sql）
-- 业务背景：等级上限从 255 提升到 999 后，Character.getMaxClassLevel() 通过
--           GameConfig.getServerInt("max_level") 读取上限；未配置时回退 255，
--           导致 !level / 升级判定 / NPC 脚本仍被 255 卡住。
-- 影响：写入后全服角色等级上限变为 999；GM 命令 !level、升级经验判定、
--       远征入场等级、家族谱系等级均随之上移。不改任何既有配置行。
-- 幂等：仅当 config_type='server' 且 config_code='max_level' 不存在时插入。
-- 备份：执行前请备份 game_config（SELECT * 或 mysqldump）。
-- 回滚：DELETE FROM game_config WHERE config_type='server' AND config_code='max_level';
--       （删行后回退到代码内 255 默认值；Flyway 历史记录需另行处理）
-- 说明：本脚本为 DML（INSERT ... SELECT），列均有建表注释；此处补业务说明。
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
