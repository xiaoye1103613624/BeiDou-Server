-- Phase4：灵魂宝珠 + 星岩（socket）装备扩展字段
ALTER TABLE inventoryequipment
    ADD COLUMN soulId INT NOT NULL DEFAULT 0 COMMENT '灵魂宝珠类型/卷ID' AFTER bonusPotentialGrade,
    ADD COLUMN soulOption INT NOT NULL DEFAULT 0 COMMENT '灵魂潜能 ItemOptionId' AFTER soulId,
    ADD COLUMN socket1 INT NOT NULL DEFAULT 0 COMMENT '星岩槽1 ItemOptionId' AFTER soulOption;
