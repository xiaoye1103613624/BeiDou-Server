package org.gms.combat.damage;

import org.gms.combat.stat.CombatStatProfile;
import org.gms.server.life.MonsterStats;
import org.gms.util.Randomizer;

public final class DamageCalculator {
    private DamageCalculator() {}

    /**
     * 默认路径：在客户端上报伤害上叠 damR/fdR，并对套装无视防御做修正。
     */
    public static long applyCombatProfile(long baseDamage, CombatStatProfile profile, MonsterStats mob,
                                          boolean magicAttack) {
        if (baseDamage <= 0 || profile == null || profile == CombatStatProfile.EMPTY) {
            return baseDamage;
        }
        double dmg = baseDamage;
        dmg *= profile.damagePercentMultiplier(mob);
        dmg *= profile.finalDamageMultiplier;
        if (magicAttack) {
            dmg *= DefenseCalculator.magicCorrection(profile, mob);
        } else {
            dmg *= DefenseCalculator.physicalCorrection(profile, mob);
        }
        return Math.max(1L, Math.round(dmg));
    }

    public static long applyCombatProfile(long baseDamage, CombatStatProfile profile, MonsterStats mob) {
        return applyCombatProfile(baseDamage, profile, mob, false);
    }

    /**
     * 服务端重算路径：damR × 完整防御减免 × fdR，并可按 Profile 暴击。
     */
    public static long applyServerDamage(long baseDamage, CombatStatProfile profile, MonsterStats mob,
                                         boolean magicAttack, boolean rollCrit) {
        if (baseDamage <= 0) {
            return baseDamage;
        }
        if (profile == null || profile == CombatStatProfile.EMPTY) {
            return baseDamage;
        }
        double dmg = baseDamage;
        dmg *= profile.damagePercentMultiplier(mob);
        if (mob != null) {
            if (magicAttack) {
                dmg *= DefenseCalculator.defenseMultiplier(mob.getMdr(), profile.ignoreMDR);
            } else {
                dmg *= DefenseCalculator.defenseMultiplier(mob.getPdr(), profile.ignorePDR);
            }
        }
        dmg *= profile.finalDamageMultiplier;
        if (rollCrit && shouldCrit(profile)) {
            dmg *= critMultiplier(profile);
        }
        return Math.max(1L, Math.round(dmg));
    }

    public static boolean shouldCrit(CombatStatProfile profile) {
        if (profile == null || profile.critRate <= 0) {
            return false;
        }
        return Randomizer.nextInt(100) < profile.critRate;
    }

    /** 基础暴击 1.5 + critDam% */
    public static double critMultiplier(CombatStatProfile profile) {
        int cd = profile == null ? 0 : profile.critDam;
        return 1.5 + Math.max(0, cd) / 100.0;
    }

    /** 反作弊上限用的暴击倍率（必暴时）。 */
    public static double maxCritMultiplier(CombatStatProfile profile) {
        return critMultiplier(profile);
    }

    public static long applyFinalDamageOnly(long damage, CombatStatProfile profile) {
        if (damage <= 0 || profile == null) {
            return damage;
        }
        return Math.max(1L, Math.round(damage * profile.finalDamageMultiplier));
    }

    public static String profileSummary(CombatStatProfile p) {
        if (p == null || p == CombatStatProfile.EMPTY) {
            return "profile=empty";
        }
        return "damR=" + p.damR + ",bdR=" + p.bossDamR + ",nbdR=" + p.normalDamR
                + ",fdMul=" + String.format("%.3f", p.finalDamageMultiplier)
                + ",ignPDR=" + p.ignorePDR + ",ignMDR=" + p.ignoreMDR
                + ",cr=" + p.critRate + ",cd=" + p.critDam;
    }
}
