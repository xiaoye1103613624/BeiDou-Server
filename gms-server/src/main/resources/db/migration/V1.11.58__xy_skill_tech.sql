-- 技改管理：突破效果等级上限，手动 SP 仍锁在 sp_max_level
CREATE TABLE IF NOT EXISTS `xy_skill_tech` (
    `id` BIGINT NOT NULL AUTO_INCREMENT,
    `skill_id` INT NOT NULL COMMENT '技能ID',
    `skill_name` VARCHAR(128) DEFAULT NULL COMMENT '技能名（展示用）',
    `sp_max_level` INT NOT NULL COMMENT '手动加点上限（原始最高等级）',
    `effect_max_level` INT NOT NULL COMMENT '效果最高等级（可高于 sp_max_level）',
    `levels_json` LONGTEXT DEFAULT NULL COMMENT '等级属性覆盖 JSON，如 {"31":{"damage":155,"mpCon":20}}',
    `enabled` INT NOT NULL DEFAULT 1 COMMENT '1启用 0禁用',
    `client_synced` INT NOT NULL DEFAULT 0 COMMENT '客户端 Skill.wz 是否已同步',
    `remark` VARCHAR(255) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_xy_skill_tech_skill_id` (`skill_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='技能技改配置';
