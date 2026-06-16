-- ==========================================
-- 独立掉落系统
-- 功能：远征BOSS死亡时，每个队员独立随机掉落一份物品（替代共享抢夺机制）
--       独立掉落物品初始120秒内仅掉落归属者可拾取
-- ==========================================

-- 1. 全局开关配置
INSERT INTO game_config (config_type,config_code, config_value, config_desc) VALUES
('server','independent_drop_enabled', '1', '全局独立掉落开关（0=关闭 1=开启），仅对配置表中启用的怪物生效');

-- 2. 独立掉落怪物配置表
CREATE TABLE IF NOT EXISTS xy_independent_drop_config (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    mob_id INT NOT NULL COMMENT 'BOSS怪物ID（对应 drop_data.dropperid）',
    mob_name VARCHAR(64) DEFAULT '' COMMENT '怪物名称（备注用）',
    enabled INT DEFAULT 1 COMMENT '是否启用独立掉落（0=禁用 1=启用）',
    UNIQUE INDEX idx_mob_id (mob_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='独立掉落怪物配置表（配置哪些怪物启用独立掉落模式）';

-- 3. 默认数据：扎昆、黑龙等远征BOSS
INSERT INTO xy_independent_drop_config (mob_id, mob_name, enabled) VALUES
(9600009, '大王蜈蚣', 1),
(8150000, '蝙蝠魔', 1),
(8830007, '巨魔蝙蝠怪', 1),
(8500002, '帕普拉图斯', 1),
(8510000, '皮亚奴斯(右)', 1),
(8520000, '皮亚奴斯(左)', 1),
(9420549, '愤怒的心疤狮王', 1),
(9420544, '愤怒的暴力熊', 1),
(9420522, '克雷塞尔', 1),
(9400409, '天皇蟾蜍', 1),
(9300215, '武公', 1),
(8810018, '暗黑龙王的灵魂', 1),
(8820001, '品克缤', 1),
(9400589, '绯红-纳格罗姆', 1),
(9400590, '绯红-玛格纳', 1),
(9400591, '绯红-猩红尼格', 1),
(9400592, '绯红-瑞力克', 1),
(9400593, '绯红-萨尔弗', 1),
(9600025, '妖僧', 1),
(9600026, '妖僧', 1),
(9700004, '半半', 1),
(9700002, '蛇王', 1),
(9700000, '希拉', 1),
(9700001, '希纳斯', 1),
(9700006, '阴阳师', 1),
(9700011, '三头犬', 1)
ON DUPLICATE KEY UPDATE mob_name = VALUES(mob_name);
