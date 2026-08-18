package org.gms.combat;

import org.gms.combat.damage.DamageCalculator;
import org.gms.combat.damage.DefenseCalculator;
import org.gms.combat.stat.CombatStatModifier;
import org.gms.combat.stat.CombatStatProfile;
import org.gms.combat.stat.CombatStatResolver;
import org.gms.combat.stat.CombatStatSource;
import org.gms.combat.stat.CombatStatType;
import org.gms.server.life.MonsterStats;
import org.gms.server.setitem.SetBonus;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** 对照伤害文档官方数值示例。 */
class CombatStatFormulaTest {

    @Test
    void additivePoolDilutes() {
        CombatStatResolver resolver = new CombatStatResolver();
        CombatStatProfile p = resolver.resolve(List.of(
                mod(CombatStatType.BOSS_DAM_R, 50, "a"),
                mod(CombatStatType.DAM_R, 10, "b")
        ));
        MonsterStats boss = bossWithPdr(0);
        double mul = p.damagePercentMultiplier(boss);
        assertEquals(1.60, mul, 0.0001);
        assertEquals(6.67, (160.0 / 150.0 - 1.0) * 100.0, 0.01);
    }

    @Test
    void finalDamageMultipliesIndependently() {
        CombatStatResolver resolver = new CombatStatResolver();
        CombatStatProfile p = resolver.resolve(List.of(
                mod(CombatStatType.FINAL_DAM_R, 20, "a"),
                mod(CombatStatType.FINAL_DAM_R, 15, "b")
        ));
        assertEquals(1.2 * 1.15, p.finalDamageMultiplier, 0.0001);
        assertEquals(38.0, (1.2 * 1.15 - 1.0) * 100.0, 0.01);
    }

    @Test
    void ignorePdrReducesEffectiveDefense() {
        assertEquals(10, DefenseCalculator.effectiveDefenseRate(40, 30));
        assertEquals(0.90, DefenseCalculator.defenseMultiplier(40, 30), 0.0001);
    }

    @Test
    void ignoreCapAt100() {
        CombatStatResolver resolver = new CombatStatResolver();
        CombatStatProfile p = resolver.resolve(List.of(
                mod(CombatStatType.IGNORE_PDR, 60, "a"),
                mod(CombatStatType.IGNORE_PDR, 60, "b")
        ));
        assertEquals(100, p.ignorePDR);
    }

    @Test
    void applyCombatProfileStacksDamAndFd() {
        CombatStatProfile p = new CombatStatResolver().resolve(List.of(
                mod(CombatStatType.DAM_R, 50, "dam"),
                mod(CombatStatType.FINAL_DAM_R, 10, "fd")
        ));
        long out = DamageCalculator.applyCombatProfile(1000, p, null, false);
        assertEquals(1650L, out);
    }

    @Test
    void setBonusCumulativeFdRNotDoubled() {
        SetBonus t2 = new SetBonus(2);
        t2.putCombatStat(CombatStatType.FINAL_DAM_R.getKey(), 10);
        SetBonus t5 = new SetBonus(5);
        t5.putCombatStat(CombatStatType.FINAL_DAM_R.getKey(), 15);

        SetBonus total = new SetBonus();
        total.merge(copyLikeManager(t2));
        total.merge(copyLikeManager(t5));

        List<Integer> sources = total.collectFinalDamageSources();
        assertEquals(2, sources.size());
        assertTrue(sources.contains(10));
        assertTrue(sources.contains(15));
        assertEquals(1.10 * 1.15, CombatStatProfile.computeFinalDamageMultiplier(sources), 0.0001);
    }

    @Test
    void convertFixedDefToRate() {
        assertEquals(0, DefenseCalculator.convertFixedDefenseToRate(0, 1000));
        assertEquals(50, DefenseCalculator.convertFixedDefenseToRate(1000, 1000));
        assertTrue(DefenseCalculator.convertFixedDefenseToRate(9000, 1000) <= 90);
    }

    private static CombatStatModifier mod(CombatStatType type, int value, String id) {
        return new CombatStatModifier(type, value, CombatStatSource.SET_BONUS, id);
    }

    private static MonsterStats bossWithPdr(int pdr) {
        MonsterStats s = new MonsterStats();
        s.setBoss(true);
        s.setPdr(pdr);
        return s;
    }

    /** 模拟 SetItemManager.copyBonus：先物化 sources 再 merge。 */
    private static SetBonus copyLikeManager(SetBonus src) {
        SetBonus copy = new SetBonus(src.requiredCount);
        copy.combatStats.putAll(src.combatStats);
        copy.finalDamagePercent = src.finalDamagePercent;
        copy.finalDamageSources.addAll(src.finalDamageSources);
        if (copy.finalDamageSources.isEmpty()) {
            copy.finalDamageSources.addAll(src.collectFinalDamageSources());
        }
        return copy;
    }
}
