package org.gms.combat.damage;

import org.gms.combat.stat.CombatStatProfile;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PotentialAttackPercentTest {

    @Test
    void padRMultipliesPhysicalClientDamage() {
        CombatStatProfile profile = new CombatStatProfile(
                0, 0, 0, List.of(), 0, 0, 0, 0, 1.0, 20, 0);
        // Client base 1000 without %ATT; server should apply +20% PAD
        assertEquals(1200L, DamageCalculator.applyCombatProfile(1000, profile, null, false));
    }

    @Test
    void madRMultipliesMagicClientDamage() {
        CombatStatProfile profile = new CombatStatProfile(
                0, 0, 0, List.of(), 0, 0, 0, 0, 1.0, 0, 30);
        assertEquals(1300L, DamageCalculator.applyCombatProfile(1000, profile, null, true));
    }

    @Test
    void padRDoesNotAffectMagic() {
        CombatStatProfile profile = new CombatStatProfile(
                0, 0, 0, List.of(), 0, 0, 0, 0, 1.0, 50, 0);
        assertEquals(1000L, DamageCalculator.applyCombatProfile(1000, profile, null, true));
    }
}
