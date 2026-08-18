-- equipSkill columns (shop seed stripped for port)
ALTER TABLE inventoryequipment
    ADD COLUMN equipSkillId INT NOT NULL DEFAULT 0 COMMENT '灵韵技能ID' AFTER anvilItemId,
    ADD COLUMN equipSkillLevel INT NOT NULL DEFAULT 0 COMMENT '灵韵技能等级' AFTER equipSkillId,
    ADD COLUMN equipSkillExpire BIGINT NOT NULL DEFAULT 0 COMMENT '灵韵过期时间，0=永久' AFTER equipSkillLevel;
