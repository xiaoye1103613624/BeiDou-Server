-- Phase9：星岩槽2（封包原 reserved@0x138，尺寸仍 0x13C）
ALTER TABLE inventoryequipment
    ADD COLUMN socket2 INT NOT NULL DEFAULT 0 COMMENT '星岩槽2 ItemOptionId' AFTER socket1;
