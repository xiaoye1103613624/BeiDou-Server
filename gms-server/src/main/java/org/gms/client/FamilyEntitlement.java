package org.gms.client;

import org.gms.util.I18nUtil;

/**
 * 【枚举】FamilyEntitlement，包 {@code org.gms.client}。
 * 家族特权（家族声望可兑换的增益效果）。
 *
 * <p>消耗家族声望可以兑换各类增益效果，包括经验加成、掉宝率提升、家族召唤等。
 * 每个特权都有使用次数限制和声望消耗值。</p>
 *
 * @see <a href="https://maplestory.fandom.com/wiki/Family">家族系统</a>
 */
public enum FamilyEntitlement {
    /** 家族团聚：消耗300声望，全家族成员获得经验加成 */
    FAMILY_REUINION(1, 300, I18nUtil.getMessage("FamilyEntitlement.message1"), I18nUtil.getMessage("FamilyEntitlement.message2")),
    /** 召唤家族成员：消耗500声望，可召唤离线家族成员到身边 */
    SUMMON_FAMILY(1, 500, I18nUtil.getMessage("FamilyEntitlement.message3"), I18nUtil.getMessage("FamilyEntitlement.message4")),
    /** 个人掉宝率1.5倍：消耗700声望，获得1.5倍掉宝率增益 */
    SELF_DROP_1_5(1, 700, I18nUtil.getMessage("FamilyEntitlement.message5"), I18nUtil.getMessage("FamilyEntitlement.message6")),
    /** 个人经验1.5倍：消耗800声望，获得1.5倍经验增益 */
    SELF_EXP_1_5(1, 800, I18nUtil.getMessage("FamilyEntitlement.message7"), I18nUtil.getMessage("FamilyEntitlement.message8")),
    /** 家族羁绊：消耗1000声望，增强家族成员间的联系效果 */
    FAMILY_BONDING(1, 1000, I18nUtil.getMessage("FamilyEntitlement.message9"), I18nUtil.getMessage("FamilyEntitlement.message10")),
    /** 个人掉宝率2倍：消耗1200声望，获得2倍掉宝率增益 */
    SELF_DROP_2(1, 1200, I18nUtil.getMessage("FamilyEntitlement.message11"), I18nUtil.getMessage("FamilyEntitlement.message12")),
    /** 个人经验2倍：消耗1500声望，获得2倍经验增益 */
    SELF_EXP_2(1, 1500, I18nUtil.getMessage("FamilyEntitlement.message13"), I18nUtil.getMessage("FamilyEntitlement.message14")),
    /** 个人掉宝率2倍(30分钟)：消耗2000声望，获得30分钟的2倍掉宝率增益 */
    SELF_DROP_2_30MIN(1, 2000, I18nUtil.getMessage("FamilyEntitlement.message15"), I18nUtil.getMessage("FamilyEntitlement.message16")),
    /** 个人经验2倍(30分钟)：消耗2500声望，获得30分钟的2倍经验增益 */
    SELF_EXP_2_30MIN(1, 2500, I18nUtil.getMessage("FamilyEntitlement.message17"), I18nUtil.getMessage("FamilyEntitlement.message18")),
    /** 队伍掉宝率2倍(30分钟)：消耗4000声望，队伍成员获得30分钟的2倍掉宝率增益 */
    PARTY_DROP_2_30MIN(1, 4000, I18nUtil.getMessage("FamilyEntitlement.message19"), I18nUtil.getMessage("FamilyEntitlement.message20")),
    /** 队伍经验2倍(30分钟)：消耗5000声望，队伍成员获得30分钟的2倍经验增益 */
    PARTY_EXP_2_30MIN(1, 5000, I18nUtil.getMessage("FamilyEntitlement.message21"), I18nUtil.getMessage("FamilyEntitlement.message22"));

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