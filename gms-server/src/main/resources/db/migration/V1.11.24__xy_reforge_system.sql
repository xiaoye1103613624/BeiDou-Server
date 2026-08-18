-- 洗炼系统：装备词条鉴定/重洗（独立于潜能系统）
-- 设计文档：玩法/装备强化系统-脚本主导设计.md + 洗炼-词条配置示例.md
ALTER TABLE inventoryequipment
    ADD COLUMN reforge1 INT NOT NULL DEFAULT 0 COMMENT '洗炼词条1：高16位=affixCode编码 低16位=prefixLv(①~⑤)' AFTER socket3,
    ADD COLUMN reforge2 INT NOT NULL DEFAULT 0 COMMENT '洗炼词条2' AFTER reforge1,
    ADD COLUMN reforge3 INT NOT NULL DEFAULT 0 COMMENT '洗炼词条3' AFTER reforge2,
    ADD COLUMN reforgeLock TINYINT NOT NULL DEFAULT 0 COMMENT '洗炼锁定位掩码 bit0=slot0 bit1=slot1 bit2=slot2' AFTER reforge3;

-- 洗炼词条配置表（权威来源，脚本+Java同读此表）
CREATE TABLE IF NOT EXISTS `xy_reforge_affix` (
    `code` VARCHAR(16) NOT NULL COMMENT '词条代码：HP/DEF/WAR/MAG/BOW/THF/ALL/PAD/MAD/BRAVE/WISE/SWIFT/THORN/HOLY/XIAN/SHEN',
    `name_zh` VARCHAR(8) NOT NULL COMMENT '中文名：血/防/战/法/弓/侠/全/攻/魔/勇/慧/迅/刺/圣/仙/神',
    `max_prefix` TINYINT NOT NULL DEFAULT 5 COMMENT '最高前缀等级：血/防固定1，其余5',
    `base_json` JSON NOT NULL COMMENT '①级基值JSON：{"HP":100,"MP":100} 或 {"STR":2,"DEX":1} 等',
    `weight` INT NOT NULL DEFAULT 10 COMMENT '抽取权重(万分比相对权重)，血/防低权重降低常驻概率',
    `enabled` TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用：0=禁用 1=启用',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='洗炼词条配置表';

-- 插入16种词条配置（与设计文档一致）
INSERT INTO `xy_reforge_affix` (`code`, `name_zh`, `max_prefix`, `base_json`, `weight`) VALUES
('HP', '血', 1, '{"HP":100,"MP":100}', 8),
('DEF', '防', 1, '{"PDD":50,"MDD":50}', 8),
('WAR', '战', 5, '{"STR":2,"DEX":1}', 10),
('MAG', '法', 5, '{"INT":2,"LUK":1}', 10),
('BOW', '弓', 5, '{"STR":1,"DEX":2}', 10),
('THF', '侠', 5, '{"DEX":1,"LUK":2}', 10),
('ALL', '全', 5, '{"ALLSTAT":1}', 8),
('PAD', '攻', 5, '{"PAD":1}', 10),
('MAD', '魔', 5, '{"MAD":2}', 10),
('BRAVE', '勇', 5, '{"STR":2,"DEX":1,"PAD":1}', 6),
('WISE', '慧', 5, '{"INT":2,"LUK":1,"MAD":2}', 6),
('SWIFT', '迅', 5, '{"STR":1,"DEX":2,"PAD":1}', 6),
('THORN', '刺', 5, '{"DEX":1,"LUK":2,"PAD":1}', 6),
('HOLY', '圣', 5, '{"ALLSTAT":2,"HP":20,"MP":20,"PAD":1}', 4),
('XIAN', '仙', 5, '{"ALLSTAT":2,"HP":20,"MP":20,"MAD":2}', 4),
('SHEN', '神', 5, '{"ALLSTAT":3,"HP":30,"MP":30,"PAD":1,"MAD":2}', 3);
