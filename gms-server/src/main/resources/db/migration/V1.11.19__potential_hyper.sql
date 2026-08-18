-- 潜能 + Hyper 强化：装备扩展字段
ALTER TABLE inventoryequipment
    ADD COLUMN potential1 INT NOT NULL DEFAULT 0 COMMENT '潜能选项1 ItemOptionId' AFTER equipSkillExpire,
    ADD COLUMN potential2 INT NOT NULL DEFAULT 0 COMMENT '潜能选项2' AFTER potential1,
    ADD COLUMN potential3 INT NOT NULL DEFAULT 0 COMMENT '潜能选项3' AFTER potential2,
    ADD COLUMN potentialGrade TINYINT NOT NULL DEFAULT 0 COMMENT '潜能品阶 0无 1普通 2稀有 3史诗 4独特 5传说' AFTER potential3,
    ADD COLUMN enhance TINYINT NOT NULL DEFAULT 0 COMMENT 'Hyper强化星级' AFTER potentialGrade;
