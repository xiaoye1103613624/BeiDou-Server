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
    ADD COLUMN `facetintbright` TINYINT NOT NULL DEFAULT 0,
    ADD COLUMN `skintinthue` SMALLINT NOT NULL DEFAULT 0,
    ADD COLUMN `skintintchroma` TINYINT NOT NULL DEFAULT 0,
    ADD COLUMN `skintintbright` TINYINT NOT NULL DEFAULT 0;

ALTER TABLE `inventoryitems`
    ADD COLUMN `efftinthue` SMALLINT NOT NULL DEFAULT 0 COMMENT '现金特效染色色相',
    ADD COLUMN `efftintchroma` TINYINT NOT NULL DEFAULT 0 COMMENT '现金特效染色饱和度',
    ADD COLUMN `efftintbright` TINYINT NOT NULL DEFAULT 0 COMMENT '现金特效染色明度';

CREATE TABLE `skilltints` (
    `characterid` INT NOT NULL COMMENT '角色ID',
    `skillid` INT NOT NULL COMMENT '技能ID',
    `tinthue` SMALLINT NOT NULL DEFAULT 0 COMMENT '技能染色色相',
    `tintchroma` TINYINT NOT NULL DEFAULT 0 COMMENT '技能染色饱和度',
    `tintbright` TINYINT NOT NULL DEFAULT 0 COMMENT '技能染色明度',
    PRIMARY KEY (`characterid`, `skillid`),
    CONSTRAINT `fk_skilltints_character` FOREIGN KEY (`characterid`) REFERENCES `characters` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) COMMENT='七彩棱镜技能染色';
