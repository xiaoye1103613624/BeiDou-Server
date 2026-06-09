package org.gms.client;

import org.gms.util.I18nUtil;

/**
 * 家族权利枚举
 * 定义家族可用的各种特权Buff，如家族召唤、掉落加成、经验加成等
 * 每种特权有使用限制和声望消耗
 */
public enum FamilyEntitlement {
    /** 家族团聚 */
    FAMILY_REUINION(1, 300, I18nUtil.getMessage("FamilyEntitlement.message1"), I18nUtil.getMessage("FamilyEntitlement.message2")),
    /** 召唤家族成员 */
    SUMMON_FAMILY(1, 500, I18nUtil.getMessage("FamilyEntitlement.message3"), I18nUtil.getMessage("FamilyEntitlement.message4")),
    /** 自身掉落1.5倍 */
    SELF_DROP_1_5(1, 700, I18nUtil.getMessage("FamilyEntitlement.message5"), I18nUtil.getMessage("FamilyEntitlement.message6")),
    /** 自身经验1.5倍 */
    SELF_EXP_1_5(1, 800, I18nUtil.getMessage("FamilyEntitlement.message7"), I18nUtil.getMessage("FamilyEntitlement.message8")),
    /** 家族羁绊 */
    FAMILY_BONDING(1, 1000, I18nUtil.getMessage("FamilyEntitlement.message9"), I18nUtil.getMessage("FamilyEntitlement.message10")),
    /** 自身掉落2倍 */
    SELF_DROP_2(1, 1200, I18nUtil.getMessage("FamilyEntitlement.message11"), I18nUtil.getMessage("FamilyEntitlement.message12")),
    /** 自身经验2倍 */
    SELF_EXP_2(1, 1500, I18nUtil.getMessage("FamilyEntitlement.message13"), I18nUtil.getMessage("FamilyEntitlement.message14")),
    /** 自身掉落2倍（30分钟） */
    SELF_DROP_2_30MIN(1, 2000, I18nUtil.getMessage("FamilyEntitlement.message15"), I18nUtil.getMessage("FamilyEntitlement.message16")),
    /** 自身经验2倍（30分钟） */
    SELF_EXP_2_30MIN(1, 2500, I18nUtil.getMessage("FamilyEntitlement.message17"), I18nUtil.getMessage("FamilyEntitlement.message18")),
    /** 队伍掉落2倍（30分钟） */
    PARTY_DROP_2_30MIN(1, 4000, I18nUtil.getMessage("FamilyEntitlement.message19"), I18nUtil.getMessage("FamilyEntitlement.message20")),
    /** 队伍经验2倍（30分钟） */
    PARTY_EXP_2_30MIN(1, 5000, I18nUtil.getMessage("FamilyEntitlement.message21"), I18nUtil.getMessage("FamilyEntitlement.message22"));

    /** 使用次数限制 */
    private final int usageLimit;
    /** 声望消耗 */
    private final int repCost;
    /** 特权名称 */
    private final String name;
    /** 特权描述 */
    private final String description;

    FamilyEntitlement(int usageLimit, int repCost, String name, String description) {
        this.usageLimit = usageLimit;
        this.repCost = repCost;
        this.name = name;
        this.description = description;
    }

    public int getUsageLimit() {
        return usageLimit;
    }

    public int getRepCost() {
        return repCost;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}