-- 为characters表新增money(赞助/元宝)预留字段，用于后续VIP权益、充值兑换等业务扩展
-- 注：当前版本暂不实现充值/赞助校验逻辑，本字段仅作为数据结构预留，默认值0
ALTER TABLE `characters`
    ADD COLUMN `money` INT(11) NOT NULL DEFAULT 0 COMMENT '赞助/元宝余额(预留字段，用于后续VIP及充值业务扩展)' AFTER `meso`;
