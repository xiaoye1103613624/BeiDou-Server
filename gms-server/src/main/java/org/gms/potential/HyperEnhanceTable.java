package org.gms.potential;

/**
 * Hyper 强化星级加成表。
 * <p>
 * 每条 Hyper 星为装备追加基础属性（不影响经典强化/潜能），在 {@code computeBonus} 中按
 * {@code equip.getEnhance()} 查表并入面板。线性增长，上限 {@link PotentialHyperConfig#MAX_ENHANCE}。
 */
public final class HyperEnhanceTable {
    private HyperEnhanceTable() {}

    /** 单星加成。 */
    public record HyperBonus(int str, int dex, int inte, int luk, int hp, int mp,
                             int watk, int matk, int wdef, int mdef, int acc, int avoid) {}

    public static HyperBonus bonusForStar(int star) {
        int s = Math.max(0, Math.min(PotentialHyperConfig.MAX_ENHANCE, star));
        if (s <= 0) {
            return new HyperBonus(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        }
        // 095 对齐：星级越高边际略增（用 star 而非纯线性放大手感）
        int pri = s * 2 + s / 5;          // str/dex/int/luk
        int att = s * 3 + s / 4;          // watk/matk
        int vit = s * 30 + s * s / 2;     // hp/mp
        int def = s * 2;                  // wdef/mdef
        int acc = s;                      // acc/avoid
        return new HyperBonus(pri, pri, pri, pri, vit, vit, att, att, def, def, acc, acc);
    }
}
