package org.gms.flame;

import org.gms.constants.inventory.ItemConstants;

/**
 * 火花/涅槃物品与常量。物品 ID 对齐 String.wz / 小册子官名。
 */
public final class FlameConfig {
    private FlameConfig() {}

    /** 对齐 265 */
    public static final double[] WEAPON_FLAME_MULTIPLIER =
            {1.0, 2.2, 3.65, 5.35, 7.3, 8.8, 10.25};
    public static final double[] WEAPON_FLAME_MULTIPLIER_BOSS =
            {1.0, 1.0, 3.0, 4.4, 6.05, 8.0, 10.25};
    public static final short LEVEL_DIVIDER = 40;
    public static final short LEVEL_DIVIDER_EXTENDED = 20;

    // —— 常用可用火焰（跳过「已移除」类；以官名 ID 为准）——
    public static final int POWERFUL = 2048744;          // 强大的涅槃火焰
    public static final int POWERFUL_ALT = 2048750;      // 强大的涅槃火焰（别名）
    public static final int ETERNAL = 2048746;           // 永远的涅槃火焰
    public static final int ETERNAL_ALT = 2048721;       // 永远的涅槃火焰
    public static final int ETERNAL_ALT2 = 2048723;
    public static final int ETERNAL_ALT3 = 2048747;
    public static final int BLACK = 2048753;             // 黑暗涅槃火焰
    public static final int BLACK_ALT = 2048755;
    public static final int ABYSSAL = 2048757;           // 深渊/特殊永远（按 Eternal+ 处理，见 resolve）
    /** 等级限定普通涅槃（按 Powerful 简化） */
    public static final int GENERIC_110 = 2048700;
    public static final int GENERIC = 2048714;

    public static boolean isFlameItem(int itemId) {
        return resolveType(itemId) != null;
    }

    public static FlameType resolveType(int itemId) {
        return switch (itemId) {
            case POWERFUL, POWERFUL_ALT, 2048745, 2048759, GENERIC_110, GENERIC,
                 2048701, 2048702, 2048703, 2048704, 2048705, 2048709,
                 2048712, 2048713, 2048727, 2048728, 2048729, 2048730, 2048731,
                 2048743, 2048754, 2048756, 2048762, 2048763, 2048764, 2048765
                    -> FlameType.POWERFUL;
            case ETERNAL, ETERNAL_ALT, ETERNAL_ALT2, ETERNAL_ALT3, 2048749, 2048758
                    -> FlameType.ETERNAL;
            case BLACK, BLACK_ALT, 2048748, 2048751
                    -> FlameType.BLACK;
            case ABYSSAL
                    -> FlameType.ABYSSAL;
            default -> {
                if (itemId >= 2048700 && itemId < 2048800) {
                    // 未单列的 20487xx：默认强大档
                    yield FlameType.POWERFUL;
                }
                yield null;
            }
        };
    }

    /** 按装备大类前缀（对齐 265 canEquipHaveFlame）。 */
    public static boolean canEquipHaveFlame(int itemId) {
        int t = itemId / 10000;
        return ItemConstants.isWeapon(itemId)
                || t == 100 // hat
                || t == 101 || t == 102 // face / eye
                || t == 103 // earring
                || t == 104 || t == 105 // top / overall
                || t == 106 // bottom
                || t == 107 // shoes
                || t == 108 // glove
                || t == 110 // cape
                || t == 112 || t == 113 // pendant / belt
                || t == 115 // shoulder
                || t == 116; // pocket
    }
}
