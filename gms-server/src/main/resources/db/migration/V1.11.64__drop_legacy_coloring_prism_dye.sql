-- Drop legacy HSL Coloring Prism store (EquipDye / opcodes 0x11D·0x184).
-- New weapontint protocol uses inventoryequipment / characters / inventoryitems columns
-- and the skilltints table; semantics are incompatible, so rows are not migrated.
DROP TABLE IF EXISTS `coloring_prism_dye`;
