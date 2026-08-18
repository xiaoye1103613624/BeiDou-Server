-- Coloring Prism weapontint columns (append; identity tint = all zeros).
ALTER TABLE `inventoryequipment`
    ADD COLUMN `tinthue` SMALLINT NOT NULL DEFAULT 0,
    ADD COLUMN `tintchroma` TINYINT NOT NULL DEFAULT 0,
    ADD COLUMN `tintbright` TINYINT NOT NULL DEFAULT 0,
    ADD COLUMN `tintfxhue` SMALLINT NOT NULL DEFAULT 0,
    ADD COLUMN `tintfxchroma` TINYINT NOT NULL DEFAULT 0,
    ADD COLUMN `tintfxbright` TINYINT NOT NULL DEFAULT 0;

ALTER TABLE `characters`
    ADD COLUMN `hairtinthue` SMALLINT NOT NULL DEFAULT 0,
    ADD COLUMN `hairtintchroma` TINYINT NOT NULL DEFAULT 0,
    ADD COLUMN `hairtintbright` TINYINT NOT NULL DEFAULT 0,
    ADD COLUMN `facetinthue` SMALLINT NOT NULL DEFAULT 0,
    ADD COLUMN `facetintchroma` TINYINT NOT NULL DEFAULT 0,
    ADD COLUMN `facetintbright` TINYINT NOT NULL DEFAULT 0;
