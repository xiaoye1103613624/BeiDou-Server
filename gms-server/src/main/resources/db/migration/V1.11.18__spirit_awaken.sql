-- 灵韵觉醒：武器附加技能字段
ALTER TABLE inventoryequipment
    ADD COLUMN equipSkillId INT NOT NULL DEFAULT 0 COMMENT '灵韵技能ID' AFTER anvilItemId,
    ADD COLUMN equipSkillLevel INT NOT NULL DEFAULT 0 COMMENT '灵韵技能等级' AFTER equipSkillId,
    ADD COLUMN equipSkillExpire BIGINT NOT NULL DEFAULT 0 COMMENT '灵韵过期时间，0=永久' AFTER equipSkillLevel;

-- 匠人街材料商人：灵韵结晶（测试/投放通道）
INSERT INTO shopitems (shopid, itemid, price, pitch, position)
SELECT 9031007, 4021017, 2000000, 0, COALESCE(MAX(position), 0) + 1
FROM shopitems WHERE shopid = 9031007
AND NOT EXISTS (
    SELECT 1 FROM shopitems WHERE shopid = 9031007 AND itemid = 4021017
);
