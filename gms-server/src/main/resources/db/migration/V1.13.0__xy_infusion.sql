-- 装备注能系统：⚡ 注能等级 0~10（永久附加属性，与装备类型无关）
-- 设计：每级新增增量属性（累积叠加）；成功/失败均消耗材料；⚡数值=已达注能等级（复用星之力⚡字形）
ALTER TABLE inventoryequipment
    ADD COLUMN infusion TINYINT NOT NULL DEFAULT 0 COMMENT '注能等级 0~10（⚡）' AFTER reforgeLock;