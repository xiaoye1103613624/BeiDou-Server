package org.gms.potential;

import org.gms.client.inventory.Equip;

/** S9 stub: potential/Hyper/soul/socket combat bonus not fully ported. */
public final class PotentialHyperService {
    private PotentialHyperService() {}

    public static final class StatBonus {
        public int str, dex, inte, luk, hp, mp, watk, matk, wdef, mdef, acc, avoid, speed, jump;
        public int strR, dexR, intR, lukR, hpR, mpR, padR, madR;
        public int dropProp, mesoProp, damReflect, damReflectProp, allSkill, cooltimeReduce, mpconReduce;
    }

    public static StatBonus computeBonus(Equip equip) {
        return new StatBonus();
    }

    public static StatBonus computeBonus(Equip equip, int charLevel) {
        return computeBonus(equip);
    }

    public static void clearOnTradeIfEnabled(Equip equip) {
        // no-op stub
    }
}
