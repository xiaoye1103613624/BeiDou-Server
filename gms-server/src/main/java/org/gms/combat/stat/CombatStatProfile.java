package org.gms.combat.stat;

import org.gms.server.life.MonsterStats;

import java.util.Collections;
import java.util.List;

public final class CombatStatProfile {
    public static final CombatStatProfile EMPTY = new CombatStatProfile(0, 0, 0, List.of(), 0, 0, 0, 0, 1.0, 0, 0);

    public final int damR;
    public final int bossDamR;
    public final int normalDamR;
    public final List<Integer> finalDamageSources;
    public final int ignorePDR;
    public final int ignoreMDR;
    public final int critRate;
    public final int critDam;
    public final double finalDamageMultiplier;
    /** 潜能攻击%（incPADr 合计）；叠在客户端已算伤害之上 */
    public final int padR;
    /** 潜能魔法%（incMADr 合计） */
    public final int madR;

    public CombatStatProfile(int damR, int bossDamR, int normalDamR, List<Integer> finalDamageSources,
                             int ignorePDR, int ignoreMDR, int critRate, int critDam, double finalDamageMultiplier) {
        this(damR, bossDamR, normalDamR, finalDamageSources, ignorePDR, ignoreMDR, critRate, critDam,
                finalDamageMultiplier, 0, 0);
    }

    public CombatStatProfile(int damR, int bossDamR, int normalDamR, List<Integer> finalDamageSources,
                             int ignorePDR, int ignoreMDR, int critRate, int critDam, double finalDamageMultiplier,
                             int padR, int madR) {
        this.damR = damR;
        this.bossDamR = bossDamR;
        this.normalDamR = normalDamR;
        this.finalDamageSources = finalDamageSources == null ? List.of() : List.copyOf(finalDamageSources);
        this.ignorePDR = ignorePDR;
        this.ignoreMDR = ignoreMDR;
        this.critRate = critRate;
        this.critDam = critDam;
        this.finalDamageMultiplier = finalDamageMultiplier;
        this.padR = padR;
        this.madR = madR;
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

    /** 物理/魔法攻击力%（潜能 incPADr / incMADr），客户端伤害未含此项。 */
    public double attackPercentMultiplier(boolean magicAttack) {
        int r = magicAttack ? madR : padR;
        if (r == 0) {
            return 1.0;
        }
        return 1.0 + r / 100.0;
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
