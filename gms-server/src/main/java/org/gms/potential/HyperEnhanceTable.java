package org.gms.potential;

import org.gms.constants.inventory.ItemConstants;

/**
 * Hyper ★ 属性表（对齐 095 {@code MapleItemInformationProvider.scrollEnhance} 的期望均值，
 * 本服仍用「虚拟加算」不写回装备本体）。
 * <p>
 * 095 非极真装备每次强化：已有四维约 +0~5（期望≈2~3）；武器物魔攻按当前攻分段 +3~5。
 * 无独立 WZ ItemEnhance 表（v083/095 早期 Hyper 卷时代），故以确定性表替代随机写盘。
 * 上限仍 {@link PotentialHyperConfig#MAX_ENHANCE}=10。
 */
public final class HyperEnhanceTable {
    private HyperEnhanceTable() {}

    /**
     * 冲到第 N 星时，本星增加的四维（下标 1..10；0 不用）。
     * ★1~5：+2；★6~10：+3（贴近 095 非极真 rand(0,5) 期望）。
     */
    private static final int[] STAT_ON_STAR = {0, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3};

    /**
     * 武器：冲到第 N 星时本星增加的双攻。
     * 对齐 095 watk&lt;150→+3 … 更高分段 +4/+5。
     */
    private static final int[] WEAPON_ATK_ON_STAR = {0, 3, 3, 3, 3, 3, 4, 4, 4, 5, 5};

    /** 防具/饰品：095 几乎不加攻（仅已有 watk 且是武器才加）。 */
    private static final int[] ARMOR_ATK_ON_STAR = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

    /** 累计到 {@code stars} 星的四维加成。 */
    public static int cumulativeAllStat(int stars) {
        int n = clampStars(stars);
        int sum = 0;
        for (int s = 1; s <= n; s++) {
            sum += STAT_ON_STAR[s];
        }
        return sum;
    }

    /** 累计到 {@code stars} 星的双攻加成（按是否武器分表）。 */
    public static int cumulativeAtk(int stars, int itemId) {
        int n = clampStars(stars);
        int[] table = ItemConstants.isWeapon(itemId) ? WEAPON_ATK_ON_STAR : ARMOR_ATK_ON_STAR;
        int sum = 0;
        for (int s = 1; s <= n; s++) {
            sum += table[s];
        }
        return sum;
    }

    /** tip / 调试：单星四维增量。 */
    public static int allStatOnStar(int starReached) {
        int s = clampStars(starReached);
        return s <= 0 ? 0 : STAT_ON_STAR[s];
    }

    /** tip / 调试：单星双攻增量。 */
    public static int atkOnStar(int starReached, boolean weapon) {
        int s = clampStars(starReached);
        if (s <= 0) {
            return 0;
        }
        return weapon ? WEAPON_ATK_ON_STAR[s] : ARMOR_ATK_ON_STAR[s];
    }

    private static int clampStars(int stars) {
        if (stars <= 0) {
            return 0;
        }
        return Math.min(PotentialHyperConfig.MAX_ENHANCE, stars);
    }
}
