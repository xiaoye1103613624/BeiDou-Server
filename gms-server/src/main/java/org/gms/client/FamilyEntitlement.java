package org.gms.client;

import org.gms.util.I18nUtil;

/**
 * 【枚举】FamilyEntitlement：家族特权（家族声望可兑换的增益效果）。
 * <p>消耗家族声望兑换各类增益，包括经验加成、掉宝率提升、家族召唤等</p>
 */
public enum FamilyEntitlement {
    FAMILY_REUINION(1, 300, I18nUtil.getMessage("FamilyEntitlement.message1"), I18nUtil.getMessage("FamilyEntitlement.message2")),     // 家族团聚
    SUMMON_FAMILY(1, 500, I18nUtil.getMessage("FamilyEntitlement.message3"), I18nUtil.getMessage("FamilyEntitlement.message4")),         // 召唤家族成员
    SELF_DROP_1_5(1, 700, I18nUtil.getMessage("FamilyEntitlement.message5"), I18nUtil.getMessage("FamilyEntitlement.message6")),         // 个人掉宝率1.5倍
    SELF_EXP_1_5(1, 800, I18nUtil.getMessage("FamilyEntitlement.message7"), I18nUtil.getMessage("FamilyEntitlement.message8")),         // 个人经验1.5倍
    FAMILY_BONDING(1, 1000, I18nUtil.getMessage("FamilyEntitlement.message9"), I18nUtil.getMessage("FamilyEntitlement.message10")),     // 家族羁绊
    SELF_DROP_2(1, 1200, I18nUtil.getMessage("FamilyEntitlement.message11"), I18nUtil.getMessage("FamilyEntitlement.message12")),        // 个人掉宝率2倍
    SELF_EXP_2(1, 1500, I18nUtil.getMessage("FamilyEntitlement.message13"), I18nUtil.getMessage("FamilyEntitlement.message14")),        // 个人经验2倍
    SELF_DROP_2_30MIN(1, 2000, I18nUtil.getMessage("FamilyEntitlement.message15"), I18nUtil.getMessage("FamilyEntitlement.message16")),   // 个人掉宝率2倍(30分钟)
    SELF_EXP_2_30MIN(1, 2500, I18nUtil.getMessage("FamilyEntitlement.message17"), I18nUtil.getMessage("FamilyEntitlement.message18")),   // 个人经验2倍(30分钟)
    PARTY_DROP_2_30MIN(1, 4000, I18nUtil.getMessage("FamilyEntitlement.message19"), I18nUtil.getMessage("FamilyEntitlement.message20")), // 队伍掉宝率2倍(30分钟)
    PARTY_EXP_2_30MIN(1, 5000, I18nUtil.getMessage("FamilyEntitlement.message21"), I18nUtil.getMessage("FamilyEntitlement.message22")); // 队伍经验2倍(30分钟)

    /** 使用次数限制 */
    private final int usageLimit;
    /** 声望消耗 */
    private final int repCost;
    /** 特权名称（国际化） */
    private final String name;
    /** 特权描述（国际化） */
    private final String description;

    /**
     * 构造家族特权
     * @param usageLimit 使用次数限制
     * @param repCost 声望消耗
     * @param name 特权名称
     * @param description 特权描述
     */
    FamilyEntitlement(int usageLimit, int repCost, String name, String description) {
        this.usageLimit = usageLimit;
        this.repCost = repCost;
        this.name = name;
        this.description = description;
    }

    /**
     * 获取使用次数限制
     * @return 使用次数
     */
    public int getUsageLimit() {
        return usageLimit;
    }

    /**
     * 获取声望消耗
     * @return 所需声望值
     */
    public int getRepCost() {
        return repCost;
    }

    /**
     * 获取特权名称
     * @return 特权名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取特权描述
     * @return 特权描述
     */
    public String getDescription() {
        return description;
    }
}