-- 公会(家族)贡献系统：角色个人累计贡献字段 + 贡献/双爆相关配置项
-- 公会等级由 guilds.GP 字段(已存在，即"总贡献")按 GuildLevelConfig 换算得出，不新增公会等级表/字段

ALTER TABLE `characters`
    ADD COLUMN `guild_contribution` INT(11) NOT NULL DEFAULT '0' COMMENT '个人累计公会贡献(决定所属公会GP/等级，永久累加不重置)';

INSERT INTO game_config (config_type, config_sub_type, config_clazz, config_code, config_value, config_desc, update_time)
VALUES
    ('server', 'Game Mechanics', 'java.lang.Integer', 'guild_contribution_per_level_up', '100', '角色升级时连带获得的公会贡献(同时计入个人累计贡献与公会GP)', NOW()),
    ('server', 'Game Mechanics', 'java.lang.Integer', 'guild_donate_meso_per_contribution', '10000', '主动捐献时每多少金币换1点公会贡献', NOW()),
    ('server', 'Game Mechanics', 'java.lang.Integer', 'guild_double_buff_meso_cost', '5000000', '会长开启公会双爆所需金币', NOW()),
    ('server', 'Game Mechanics', 'java.lang.Integer', 'guild_double_buff_duration', '60', '公会双爆持续分钟数', NOW())
ON DUPLICATE KEY UPDATE config_desc = VALUES(config_desc);
