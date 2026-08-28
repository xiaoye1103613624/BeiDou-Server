ALTER TABLE `characters`
    ADD COLUMN `activeDamageSkin` INT NOT NULL DEFAULT 0 COMMENT '当前装备的伤害皮肤ID，0=默认';

CREATE TABLE IF NOT EXISTS `xy_damageskin_catalog`
(
    `skinId`     INT    NOT NULL COMMENT '皮肤ID',
    `priceMesos` BIGINT NOT NULL DEFAULT 10000000 COMMENT '商店价格（金币）',
    PRIMARY KEY (`skinId`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='伤害皮肤商店目录';

CREATE TABLE IF NOT EXISTS `xy_damageskin_inventory`
(
    `id`          INT       NOT NULL AUTO_INCREMENT COMMENT '主键',
    `characterId` INT       NOT NULL COMMENT '角色ID',
    `skinId`      INT       NOT NULL COMMENT '皮肤ID',
    `acquiredAt`  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '获得时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uq_char_skin` (`characterId`, `skinId`),
    KEY `idx_char` (`characterId`),
    CONSTRAINT `fk_damageskin_char`
        FOREIGN KEY (`characterId`) REFERENCES `characters` (`id`)
            ON DELETE CASCADE
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT ='角色已拥有的伤害皮肤';
