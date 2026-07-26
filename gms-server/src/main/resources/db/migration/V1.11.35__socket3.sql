-- Phase10：星岩槽3（封包尺寸 0x13C → 0x140）
ALTER TABLE inventoryequipment
    ADD COLUMN socket3 INT NOT NULL DEFAULT 0 COMMENT '星岩槽3 ItemOptionId' AFTER socket2;
