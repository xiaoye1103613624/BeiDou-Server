package org.gms.potential;

import org.gms.constants.inventory.ItemConstants;

/**
 * Hyper ★ 属性表（对齐 095 {@code scrollEnhance} 期望均值，虚拟加算不写回装备）。
 * 上限 {@link PotentialHyperConfig#MAX_ENHANCE}=25。
 */
public final class HyperEnhanceTable {
    private HyperEnhanceTable() {}

    /** ★1~5：+2；★6+：+3（贴近 095 非极真期望）。下标=星数，0 不用。 */
    private static int statOnStar(int star) {
        if (star <= 0) {
            return 0;
        }
        return star <= 5 ? 2 : 3;
    }

    /** 武器双攻：★1~5 +3；★6~9 +4；★10+ +5。 */
    private static int weaponAtkOnStar(int star) {
        if (star <= 0) {
            return 0;
        }
        if (star <= 5) {
            return 3;
        }
        if (star <= 9) {
            return 4;
        }
        return 5;
    }

    public static int cumulativeAllStat(int stars) {
        int n = clampStars(stars);
        int sum = 0;
        for (int s = 1; s <= n; s++) {
            sum += statOnStar(s);
        }
        return sum;
    }

    public static int cumulativeAtk(int stars, int itemId) {
        int n = clampStars(stars);
        if (!ItemConstants.isWeapon(itemId)) {
            return 0;
        }
        int sum = 0;
        for (int s = 1; s <= n; s++) {
            sum += weaponAtkOnStar(s);
        }
        return sum;
    }

    public static int allStatOnStar(int starReached) {
        return statOnStar(clampStars(starReached));
    }

    public static int atkOnStar(int starReached, boolean weapon) {
        if (!weapon) {
            return 0;
        }
        return weaponAtkOnStar(clampStars(starReached));
    }

    private static int clampStars(int stars) {
        if (stars <= 0) {
            return 0;
        }
        return Math.min(PotentialHyperConfig.MAX_ENHANCE, stars);
    }
}
