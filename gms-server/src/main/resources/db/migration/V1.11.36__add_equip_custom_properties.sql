-- 为装备表新增自定义扩展属性字段，用于存储非标准的额外属性（JSON字符串）
ALTER TABLE inventoryequipment
    ADD COLUMN custom_properties LONGTEXT NULL COMMENT '装备自定义扩展属性（JSON字符串）';
