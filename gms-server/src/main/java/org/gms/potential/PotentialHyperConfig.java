package org.gms.potential;

/**
 * 潜能 / Hyper / 灵魂 / 星岩 全局配置。
 * <p>
 * 卷轴、魔方、放大镜、灵魂宝珠、星岩等道具 ID 在此集中登记，供
 * {@link PotentialHyperService} 与 GM 命令复用。数值为 095 对齐的保守设定。
 */
public final class PotentialHyperConfig {
    private PotentialHyperConfig() {}

    /** 交易收到带潜能/灵韵装备时是否清空（防通胀；灵韵另有独立开关）。 */
    public static final boolean CLEAR_ON_TRADE = false;

    /** Hyper 强化最大星级。 */
    public static final int MAX_ENHANCE = 25;

    /** Hyper 强化卷（成功 +1 星，失败不变）。 */
    public static final int HYPER_SCROLL_ID = 2049300;
    public static final int HYPER_SCROLL_100_ID = 2049301;

    /** 普通 / 高级魔方：重 roll 主潜能。 */
    public static final int CUBE_NORMAL_ID = 2049100;
    public static final int CUBE_ADVANCED_ID = 2049101;

    /** 放大镜：揭示隐藏的附加潜能（bonusPotential*）。 */
    public static final int MAGNIFIER_ID = 2460000;

    /** 灵魂宝珠（赋予灵魂技）。 */
    public static final int SOUL_ENCHANTER_ID = 2070000;
    public static final int SOUL_SHIELD_ID = 2070001;

    /** 星岩（sockets）。 */
    public static final int STAR_STONE_ID = 4000000;

    /** GM 直接赋予潜能时使用的默认最低需求等级（仅用于过滤词条池）。 */
    public static final int DEFAULT_POTENTIAL_REQ_LEVEL = 70;

    public static boolean isHyperScroll(int itemId) {
        return itemId == HYPER_SCROLL_ID || itemId == HYPER_SCROLL_100_ID;
    }

    public static boolean isCube(int itemId) {
        return itemId == CUBE_NORMAL_ID || itemId == CUBE_ADVANCED_ID;
    }

    public static boolean isMagnifier(int itemId) {
        return itemId == MAGNIFIER_ID;
    }
}
