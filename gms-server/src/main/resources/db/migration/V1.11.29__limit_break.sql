ALTER TABLE `characters`
    ADD COLUMN `limitBreak` BIGINT NOT NULL DEFAULT 199999 COMMENT '角色伤害上限(破功值)，创建默认199999，与客户端config无关' AFTER `activeDamageSkin`;
