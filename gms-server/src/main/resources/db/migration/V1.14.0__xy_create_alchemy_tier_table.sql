-- 炼金师品级配置表：品级名称与"升级经验值"在管理后台可配置（避免硬编码）。
-- 规则：累计经验达到某品级的 exp_start（经验起点）即进入该品级，只增不减、升级不重置。
CREATE TABLE IF NOT EXISTS `xy_alchemy_tier` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(32) NOT NULL COMMENT '品级名称（如：入门、普通、职业、大师、宗师）',
    `exp_start` BIGINT NOT NULL DEFAULT 0 COMMENT '达到该品级所需的最低累计经验（经验阈值）',
    `is_max` TINYINT NOT NULL DEFAULT 0 COMMENT '是否为最高品级：0=否 1=是（最高品级无上限）',
    `sort_order` INT NOT NULL DEFAULT 0 COMMENT '品级显示顺序，越小品级越低',
    `enabled` TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用：0=禁用 1=启用',
    `create_time` DATETIME NOT NULL COMMENT '创建时间',
    `update_time` DATETIME NOT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='炼金师品级配置表（品级名称/经验阈值后台可配）';

-- 默认品级（与配方表 tier_required 下标一致：0=入门 1=普通 2=职业 3=大师 4=宗师）
INSERT INTO `xy_alchemy_tier` (`name`, `exp_start`, `is_max`, `sort_order`, `enabled`, `create_time`, `update_time`) VALUES
('入门', 0,     0, 0, 1, NOW(), NOW()),
('普通', 16000, 0, 1, 1, NOW(), NOW()),
('职业', 32000, 0, 2, 1, NOW(), NOW()),
('大师', 64000, 0, 3, 1, NOW(), NOW()),
('宗师', 128000, 1, 4, 1, NOW(), NOW());