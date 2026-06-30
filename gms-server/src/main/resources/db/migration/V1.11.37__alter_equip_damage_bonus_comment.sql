-- 更新装备伤害加成表的 fixed_damage 字段注释：从"固定伤害值"改为"额外攻击段数"
ALTER TABLE `xy_equip_damage_bonus_config`
    MODIFY COLUMN `fixed_damage` BIGINT NOT NULL DEFAULT 0 COMMENT '额外攻击段数（每段独立造成一次百分比加成后的单段伤害）';
