package org.gms.combat.power;

/**
 * 装备评分（养成向，与战力公式独立）。
 * <pre>
 * score =
 *   (STR+DEX+INT+LUK) × 10
 * + WATK×50 + MATK×50
 * + HP/5 + MP/5
 * + WDEF×2 + MDEF×2
 * + ACC×3 + AVOID×3
 * + SPEED×5 + JUMP×5
 * + 砸卷等级(level)×60
 * + 金锤子(vicious)×80
 * + 装备升级等级(itemLevel)×40
 * + 武器部位额外 ×1.25
 * + 套服(上衣位且 isOverall 由调用方加 overallBonus) 可选
 * </pre>
 * 不含临时 Buff；潜能/星之力接入后可在此追加权重。
 */
public final class EquipScoreCalculator {
    private EquipScoreCalculator() {}

    public static long score(int str, int dex, int inte, int luk,
                             int hp, int mp, int watk, int matk,
                             int wdef, int mdef, int acc, int avoid,
                             int speed, int jump,
                             int scrollLevel, int vicious, int itemLevel,
                             boolean weapon) {
        long s = 0L;
        s += (long) (str + dex + inte + luk) * 10L;
        s += (long) watk * 50L + (long) matk * 50L;
        s += hp / 5L + mp / 5L;
        s += (long) wdef * 2L + (long) mdef * 2L;
        s += (long) acc * 3L + (long) avoid * 3L;
        s += (long) speed * 5L + (long) jump * 5L;
        s += (long) Math.max(0, scrollLevel) * 60L;
        s += (long) Math.max(0, vicious) * 80L;
        s += (long) Math.max(0, itemLevel) * 40L;
        if (weapon) {
            s = Math.round(s * 1.25);
        }
        return Math.max(0L, s);
    }
}
