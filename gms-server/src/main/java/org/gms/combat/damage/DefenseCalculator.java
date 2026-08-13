package org.gms.combat.damage;

import org.gms.combat.stat.CombatStatProfile;
import org.gms.server.life.MonsterStats;

/**
 * 怪物防御率（PDR/MDR）与无视防御的换算。
 * <p>
 * 官方语义：effectiveRate = max(0, mobRate - ignore)，伤害再乘 (100 - effective) / 100。
 * 当客户端已按完整 mobRate 减伤时，用 {@link #ignoreDefenseCorrection} 把多减的部分补回。
 */
public final class DefenseCalculator {
    private DefenseCalculator() {}

    public static int effectiveDefenseRate(int mobRate, int ignoreRate) {
        return Math.max(0, mobRate - Math.max(0, ignoreRate));
    }

    public static double defenseMultiplier(int mobRate, int ignoreRate) {
        int effective = effectiveDefenseRate(mobRate, ignoreRate);
        if (effective <= 0) {
            return 1.0;
        }
        if (effective >= 100) {
            return 0.0;
        }
        return (100.0 - effective) / 100.0;
    }

    /**
     * 客户端已按完整 PDR/MDR 减伤时，对服务端下发的套装无视%做修正倍率。
     */
    public static double ignoreDefenseCorrection(int mobRate, int ignoreRate) {
        if (mobRate <= 0 || ignoreRate <= 0) {
            return 1.0;
        }
        double withIgnore = defenseMultiplier(mobRate, ignoreRate);
        double withoutIgnore = defenseMultiplier(mobRate, 0);
        if (withoutIgnore <= 0) {
            return 1.0;
        }
        return withIgnore / withoutIgnore;
    }

    public static double physicalCorrection(CombatStatProfile profile, MonsterStats mob) {
        if (profile == null || mob == null) {
            return 1.0;
        }
        return ignoreDefenseCorrection(mob.getPdr(), profile.ignorePDR);
    }

    public static double magicCorrection(CombatStatProfile profile, MonsterStats mob) {
        if (profile == null || mob == null) {
            return 1.0;
        }
        return ignoreDefenseCorrection(mob.getMdr(), profile.ignoreMDR);
    }
}
