-- ============================================================
-- 表：characters —— 角色主表（建表见 V1.0.6__create_characters.sql）
-- 业务背景：等级上限提升到 999 后，ExpTable.getExpNeededForLevel 对 200 级
--           以上按 1.06 曲线扩表，999 级单级所需经验约 2.1e12，远超
--           INT(11) 的 21.4 亿上限；不扩宽会在 200+ 级截断经验、损坏存档。
-- 本次字段：
--   exp      角色当前等级已累积经验（Character.exp = AtomicLong），
--            升级判定与经验日志均按 long；Stat.EXP 封包改 8 字节 writeLong。
--   gachaexp 扭蛋/所罗门经验券累积的「待兑换经验池」，不是等级经验：
--            UseSolomonHandler 累加，UseGachaExpHandler 兑换后转入 exp；
--            Stat.GACHAEXP 封包仍维持 4 字节 writeInt。
-- 说明：BIGINT 属安全扩宽、不丢数据；本脚本只给本次涉及的两列补备注，
--       不重定义其他列。执行前请备份，大表 ALTER 建议低峰期进行。
-- ============================================================

ALTER TABLE `characters`
    MODIFY COLUMN `exp`      BIGINT(20) NOT NULL DEFAULT '0' COMMENT '角色当前等级已累积经验；等级上限 999 需 BIGINT，来源 Character.exp，封包 8 字节',
    MODIFY COLUMN `gachaexp` BIGINT(20) NOT NULL DEFAULT '0' COMMENT '扭蛋/经验券累积的待兑换经验池，兑换后转入 exp，非等级经验';

-- 表级备注（原建表脚本未写表注释；若线上已有自定义表注释会被覆盖）
ALTER TABLE `characters` COMMENT = '角色主表：账号下角色存档、属性、等级与经验；exp/gachaexp 为 999 级突破后的 BIGINT 经验字段';
