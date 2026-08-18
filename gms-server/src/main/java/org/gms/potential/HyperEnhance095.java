package org.gms.potential;

import org.gms.server.ItemInformationProvider;

import java.util.Map;

/**
 * 095 {@code MapleItemInformationProvider.scrollEnhance} / {@code GameConstants.getEnchantSstarts}
 * 规则抽取。本服 Hyper 结算走此逻辑；属性仍用 {@link HyperEnhanceTable} 虚拟加算（有意不同）。
 */
public final class HyperEnhance095 {
    private HyperEnhance095() {}

    /** 095 全局绝对上限（高等级装备段）。 */
    public static final int ABSOLUTE_MAX_STARS = 25;

    /**
     * 按装备需求等级取星上限（对齐 095 {@code getEnchantSstarts}，非极真）。
     * 本服暂无 superior 装备标记时按非极真表。
     */
    public static int enchantStarCap(int itemReqLevel, boolean superior) {
        if (itemReqLevel < 0) {
            itemReqLevel = 0;
        }
        if (itemReqLevel <= 94) {
            return superior ? 3 : 5;
        }
        if (itemReqLevel <= 107) {
            return superior ? 5 : 8;
        }
        if (itemReqLevel <= 117) {
            return superior ? 8 : 10;
        }
        if (itemReqLevel <= 127) {
            return superior ? 10 : 15;
        }
        if (itemReqLevel <= 137) {
            return superior ? 12 : 20;
        }
        // >137 / >147
        return superior ? 15 : 25;
    }

    private static Map<String, Integer> safeScrollStats(int scrollId) {
        try {
            return ItemInformationProvider.getInstance().getEquipStats(scrollId);
        } catch (Throwable t) {
            return null;
        }
    }

    /** WZ {@code info/forceUpgrade}；缺省对 20493 族为 1。 */
    public static int forceUpgrade(int scrollId) {
        if (scrollId / 100 != 20493) {
            return 0;
        }
        Map<String, Integer> stats = safeScrollStats(scrollId);
        if (stats == null) {
            return 1;
        }
        Integer fu = stats.get("forceUpgrade");
        return fu == null || fu <= 0 ? 1 : fu;
    }

    /**
     * 单次判定成功率。{@code 2049302} 恒 100；
     * {@code forceUpgrade==1 && success缺/0} → {@code max(base - enhance*10, 5)}，
     * base：2049301/07=80，其余 100；有 WZ success 的 N 星卷用固定值。
     */
    public static int successRate(int scrollId, int currentEnhance) {
        if (scrollId == 2049302) {
            return 100;
        }
        Map<String, Integer> stats = safeScrollStats(scrollId);
        int succ = 0;
        if (stats != null && stats.containsKey("success")) {
            Integer s = stats.get("success");
            succ = s == null ? 0 : s;
        }
        int fu = forceUpgrade(scrollId);
        if (fu == 1 && succ == 0) {
            int base = (scrollId == 2049301 || scrollId == 2049307) ? 80 : 100;
            return Math.max(base - Math.max(0, currentEnhance) * 10, 5);
        }
        if (succ <= 0) {
            return Math.max(100 - Math.max(0, currentEnhance) * 10, 5);
        }
        return Math.max(1, Math.min(100, succ));
    }

    /**
     * 失败炸装率。2049302=0；WZ {@code noCursed}&gt;0 → 0；
     * 有 {@code cursed} 用其值；缺省 100（095）。
     */
    public static int curseRate(int scrollId) {
        if (!PotentialHyperConfig.HYPER_DESTROY_ON_FAIL) {
            return 0;
        }
        if (scrollId == 2049302) {
            return 0;
        }
        Map<String, Integer> stats = safeScrollStats(scrollId);
        if (stats != null) {
            Integer noCursed = stats.get("noCursed");
            if (noCursed != null && noCursed > 0) {
                return 0;
            }
            if (stats.containsKey("cursed")) {
                Integer c = stats.get("cursed");
                return c == null ? PotentialHyperConfig.DEFAULT_HYPER_CURSED : Math.max(0, Math.min(100, c));
            }
        }
        return switch (scrollId) {
            case 2049314, 2049315, 2049316, 2049317, 2049318 -> 0;
            default -> PotentialHyperConfig.DEFAULT_HYPER_CURSED;
        };
    }

    public static int equipStarCap(int equipItemId) {
        int req = 0;
        try {
            Integer r = ItemInformationProvider.getInstance().getEquipLevelReq(equipItemId);
            if (r != null) {
                req = r;
            }
        } catch (Throwable ignored) {
            req = 0;
        }
        return Math.min(ABSOLUTE_MAX_STARS, enchantStarCap(req, false));
    }
}