-- 赞助奖励：技能组（跨职业偷学等）
-- type=skill_group 时：pick_mode=ONE|MULTI|ALL；qty 对 MULTI 表示可选数量，ONE 固定为1，ALL 忽略
ALTER TABLE `xy_sponsor_reward`
    ADD COLUMN `pick_mode` VARCHAR(8) NULL
        COMMENT 'skill_group 选取模式：ONE多选一 / MULTI多选多 / ALL全发；其它类型为NULL'
        AFTER `stats_json`;

-- 技能组可选技能明细
CREATE TABLE IF NOT EXISTS `xy_sponsor_skill_option` (
    `id`           INT          NOT NULL AUTO_INCREMENT COMMENT '选项ID',
    `reward_id`    INT          NOT NULL COMMENT '所属奖励行ID（type=skill_group）',
    `skill_id`     INT          NOT NULL COMMENT '技能ID',
    `skill_level`  INT          NOT NULL DEFAULT 0 COMMENT '技能等级：0=发放时按技能最大等级',
    `default_key`  INT          NOT NULL DEFAULT 0 COMMENT '默认快捷键 keycode；0=自动找空闲偏好键',
    `sort_order`   INT          NOT NULL DEFAULT 0 COMMENT '排序，越小越靠前',
    `create_time`  DATETIME     NOT NULL COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_reward_id` (`reward_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赞助技能组可选技能';

-- 技能发放审计（便于排查；重登持久化依赖角色 skills 表）
CREATE TABLE IF NOT EXISTS `xy_sponsor_skill_grant` (
    `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `character_id` INT          NOT NULL COMMENT '角色ID',
    `config_id`    INT          NOT NULL COMMENT '档位配置ID',
    `reward_id`    INT          NOT NULL COMMENT '奖励行ID',
    `skill_id`     INT          NOT NULL COMMENT '发放的技能ID',
    `skill_level`  INT          NOT NULL COMMENT '实际发放等级',
    `bound_key`    INT          NOT NULL DEFAULT 0 COMMENT '绑定的快捷键（0=未绑定）',
    `grant_time`   DATETIME     NOT NULL COMMENT '发放时间',
    PRIMARY KEY (`id`),
    KEY `idx_char_config` (`character_id`, `config_id`),
    KEY `idx_char_skill` (`character_id`, `skill_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='赞助技能发放记录';
