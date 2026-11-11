-- 金币（meso）上限由 int 放宽到 BIGINT（long，约 92 亿亿上限）
-- 涉及：角色余额(meso)、雇佣商人余额(MerchantMesos)、仓库余额(storages.meso)、快递余额(dueypackages.Mesos)
-- 背景：等级上限提升后玩家资产规模上涨，int 上限约 21.47 亿不足以承载，故统一改为 BIGINT。

ALTER TABLE characters MODIFY COLUMN meso BIGINT NOT NULL DEFAULT 0 COMMENT '角色当前金币余额（已放宽至 BIGINT，支持超过 21.47 亿）';
ALTER TABLE characters MODIFY COLUMN MerchantMesos BIGINT DEFAULT 0 COMMENT '雇佣商人/玩家商店累计结算给角色的金币余额（BIGINT）';
ALTER TABLE storages MODIFY COLUMN meso BIGINT NOT NULL DEFAULT 0 COMMENT '仓库中存放的金币余额（BIGINT）';
ALTER TABLE dueypackages MODIFY COLUMN Mesos BIGINT DEFAULT 0 COMMENT '快递包裹内随件寄送的金币金额（BIGINT）';
