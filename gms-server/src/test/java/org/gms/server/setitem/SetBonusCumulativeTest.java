package org.gms.server.setitem;

import org.gms.combat.stat.CombatStatModifier;
import org.gms.combat.stat.CombatStatProfile;
import org.gms.combat.stat.CombatStatResolver;
import org.gms.combat.stat.CombatStatSource;
import org.gms.combat.stat.CombatStatType;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SetBonusCumulativeTest {

    @Test
    void cumulativeMergesAllQualifyingTiers() {
        SetDefinition def = new SetDefinition();
        def.enabled = true;

        SetBonus t2 = new SetBonus(2);
        t2.str = 3;
        SetBonus t3 = new SetBonus(3);
        t3.pad = 5;
        SetBonus t5 = new SetBonus(5);
        t5.putCombatStat(CombatStatType.FINAL_DAM_R.getKey(), 10);

        def.tiers.put(2, t2);
        def.tiers.put(3, t3);
        def.tiers.put(5, t5);

        SetBonus r3 = invokeCumulative(def, 3);
        assertEquals(3, r3.str);
        assertEquals(5, r3.pad);

        SetBonus r5 = invokeCumulative(def, 5);
        assertEquals(3, r5.str);
        assertEquals(5, r5.pad);
        assertEquals(1, r5.finalDamageSources.size());
        assertEquals(10, r5.finalDamageSources.get(0));
    }

    @Test
    void disabledTierIsSkipped() {
        SetDefinition def = new SetDefinition();
        SetBonus t2 = new SetBonus(2);
        t2.str = 2;
        SetBonus t3 = new SetBonus(3);
        t3.tierEnabled = false;
        t3.pad = 99;
        SetBonus t4 = new SetBonus(4);
        t4.pad = 4;
        def.tiers.put(2, t2);
        def.tiers.put(3, t3);
        def.tiers.put(4, t4);

        SetBonus r = invokeCumulative(def, 4);
        assertEquals(2, r.str);
        assertEquals(4, r.pad);
    }

    @Test
    void finalDamageMultiplies() {
        CombatStatResolver resolver = new CombatStatResolver();
        List<CombatStatModifier> mods = List.of(
                new CombatStatModifier(CombatStatType.FINAL_DAM_R, 10, CombatStatSource.SET_BONUS, "a"),
                new CombatStatModifier(CombatStatType.FINAL_DAM_R, 15, CombatStatSource.SET_BONUS, "b")
        );
        CombatStatProfile profile = resolver.resolve(mods);
        assertEquals(1.265, profile.finalDamageMultiplier, 0.001);
    }

    private static SetBonus invokeCumulative(SetDefinition def, int count) {
        SetBonus total = new SetBonus();
        boolean any = false;
        for (var entry : def.tiers.entrySet().stream().sorted(java.util.Map.Entry.comparingByKey()).toList()) {
            int need = entry.getKey();
            if (need > count) {
                continue;
            }
            SetBonus tier = entry.getValue();
            if (tier == null || !tier.tierEnabled) {
                continue;
            }
            total.merge(copy(tier));
            any = true;
        }
        return any ? total : null;
    }

    private static SetBonus copy(SetBonus src) {
        SetBonus c = new SetBonus(src.requiredCount);
        c.tierEnabled = src.tierEnabled;
        c.str = src.str;
        c.pad = src.pad;
        c.finalDamagePercent = src.finalDamagePercent;
        c.combatStats.putAll(src.combatStats);
        return c;
    }
}
