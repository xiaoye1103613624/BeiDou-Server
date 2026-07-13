package org.gms.combat.stat;

import org.gms.server.life.MonsterStats;

import java.util.Collections;
import java.util.List;

public final class CombatStatProfile {
    public static final CombatStatProfile EMPTY = new CombatStatProfile(0, 0, 0, List.of(), 0, 0, 0, 0, 1.0);

    public final int damR;
    public final int bossDamR;
    public final int normalDamR;
    public final List<Integer> finalDamageSources;
    public final int ignorePDR;
    public final int ignoreMDR;
    public final int critRate;
    public final int critDam;
    public final double finalDamageMultiplier;

    public CombatStatProfile(int damR, int bossDamR, int normalDamR, List<Integer> finalDamageSources,
                             int ignorePDR, int ignoreMDR, int critRate, int critDam, double finalDamageMultiplier) {
        this.damR = damR;
        this.bossDamR = bossDamR;
        this.normalDamR = normalDamR;
        this.finalDamageSources = finalDamageSources == null ? List.of() : List.copyOf(finalDamageSources);
        this.ignorePDR = ignorePDR;
        this.ignoreMDR = ignoreMDR;
        this.critRate = critRate;
        this.critDam = critDam;
        this.finalDamageMultiplier = finalDamageMultiplier;
    }

    public int effectiveDamR(MonsterStats mob) {
        int total = damR;
        if (mob != null && mob.isBoss()) {
            total += bossDamR;
        } else {
            total += normalDamR;
        }
        return total;
    }

    public double damagePercentMultiplier(MonsterStats mob) {
        int total = effectiveDamR(mob);
        if (total == 0) {
            return 1.0;
        }
        return 1.0 + total / 100.0;
    }

    public static double computeFinalDamageMultiplier(List<Integer> sources) {
        double mul = 1.0;
        if (sources == null) {
            return mul;
        }
        for (int v : sources) {
            if (v != 0) {
                mul *= (1.0 + v / 100.0);
            }
        }
        return mul;
    }
}
