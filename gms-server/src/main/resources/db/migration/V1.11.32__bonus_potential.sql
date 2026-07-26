-- Phase3：附加潜能（bonus potential）装备扩展字段
ALTER TABLE inventoryequipment
    ADD COLUMN bonusPotential1 INT NOT NULL DEFAULT 0 COMMENT '附加潜能选项1 ItemOptionId' AFTER enhance,
    ADD COLUMN bonusPotential2 INT NOT NULL DEFAULT 0 COMMENT '附加潜能选项2' AFTER bonusPotential1,
    ADD COLUMN bonusPotential3 INT NOT NULL DEFAULT 0 COMMENT '附加潜能选项3' AFTER bonusPotential2,
    ADD COLUMN bonusPotentialGrade TINYINT NOT NULL DEFAULT 0 COMMENT '附加潜能品阶 0无 1普通 2稀有 3史诗 4独特 5传说' AFTER bonusPotential3;
