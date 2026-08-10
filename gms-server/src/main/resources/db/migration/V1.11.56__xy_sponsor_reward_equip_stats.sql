-- 赞助奖励：装备属性模式（默认模板 / 自定义），兼容已有行（默认 default）
ALTER TABLE `xy_sponsor_reward`
    ADD COLUMN `stat_mode`  VARCHAR(16) NOT NULL DEFAULT 'default'
        COMMENT '装备属性模式：default=WZ模板 / custom=自定义（非装备忽略）' AFTER `qty`,
    ADD COLUMN `stats_json` TEXT NULL
        COMMENT '自定义装备属性JSON，字段对齐 gainEquip：str/dex/int/luk/hp/mp/pAtk/mAtk/pDef/mDef/acc/avoid/hands/speed/jump/upgradeSlot'
        AFTER `stat_mode`;
