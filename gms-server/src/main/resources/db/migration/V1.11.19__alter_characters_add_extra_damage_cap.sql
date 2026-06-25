-- 为characters表新增extra_damage_cap字段，存储突破石累加的角色额外伤害上限
-- 突破石(itemId 02614000-02614021)使用成功后按物品WZ配置的incALB值累加到该字段，叠加到伤害计算的理论最大值上
ALTER TABLE `characters`
    ADD COLUMN `extra_damage_cap` BIGINT(20) NOT NULL DEFAULT 0 COMMENT '突破石累加的额外伤害上限(叠加在理论最大伤害之上，不挂在装备上)' AFTER `money`;
