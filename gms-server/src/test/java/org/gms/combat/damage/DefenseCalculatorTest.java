package org.gms.combat.damage;

import org.gms.combat.stat.CombatStatProfile;
import org.gms.server.life.MonsterStats;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DefenseCalculatorTest {

    @Test
    void effectivePdrMinusIgnore() {
        // T-07: ignorePDR 30, mob PDR 40 → effective 10
        assertEquals(10, DefenseCalculator.effectiveDefenseRate(40, 30));
        assertEquals(0.9, DefenseCalculator.defenseMultiplier(40, 30), 1e-9);
    }

    @Test
    void ignoreCorrectionBoostsClientDamage() {
        // Client used full 40% PDR; with ignore 30 effective is 10%.
        // Correction = 0.9 / 0.6 = 1.5
        assertEquals(1.5, DefenseCalculator.ignoreDefenseCorrection(40, 30), 1e-9);
    }

    @Test
    void applyCombatProfileIncludesIgnoreCorrection() {
        MonsterStats mob = new MonsterStats();
        mob.setPdr(40);
        CombatStatProfile profile = new CombatStatProfile(
                0, 0, 0, List.of(), 30, 0, 0, 0, 1.0);
        long out = DamageCalculator.applyCombatProfile(600, profile, mob, false);
        // 600 is after client 40% cut of 1000 raw; correction *1.5 → 900
        assertEquals(900L, out);
    }
}
