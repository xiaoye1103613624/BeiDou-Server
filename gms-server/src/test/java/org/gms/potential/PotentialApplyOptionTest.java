package org.gms.potential;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/** 验证等级缩放 / 战斗向键写入 StatBonus（不依赖 WZ 加载）。 */
class PotentialApplyOptionTest {

    @Test
    void lvScalingUsesCharLevelDiv9() throws Exception {
        PotentialHyperService.StatBonus b = new PotentialHyperService.StatBonus();
        Map<String, Integer> stats = new HashMap<>();
        stats.put("incSTRlv", 2);
        stats.put("incMHPlv", 10);
        stats.put("incPAD", 5);
        invokeApply(b, stats, 90); // 90/9=10 → STR+20 HP+100
        assertEquals(20, b.str);
        assertEquals(100, b.hp);
        assertEquals(5, b.watk);
    }

    @Test
    void lvScalingSkippedWhenLevelZero() throws Exception {
        PotentialHyperService.StatBonus b = new PotentialHyperService.StatBonus();
        Map<String, Integer> stats = new HashMap<>();
        stats.put("incSTRlv", 2);
        invokeApply(b, stats, 0);
        assertEquals(0, b.str);
    }

    @Test
    void combatKeysMapped() throws Exception {
        PotentialHyperService.StatBonus b = new PotentialHyperService.StatBonus();
        Map<String, Integer> stats = new HashMap<>();
        stats.put("incCr", 8);
        stats.put("incCriticaldamage", 15);
        stats.put("incDAMr", 20);
        stats.put("boss", 1);
        stats.put("ignoreTargetDEF", 30);
        stats.put("incPADr", 12);
        invokeApply(b, stats, 0);
        assertEquals(8, b.critRate);
        assertEquals(15, b.critDam);
        assertEquals(0, b.damR);
        assertEquals(20, b.bossDamR);
        assertEquals(30, b.ignoreDef);
        assertEquals(12, b.padR);
    }

    @SuppressWarnings("unchecked")
    private static void invokeApply(PotentialHyperService.StatBonus b, Map<String, Integer> stats, int lv)
            throws Exception {
        Method m = PotentialHyperService.class.getDeclaredMethod(
                "applyOption", PotentialHyperService.StatBonus.class, Map.class, int.class);
        m.setAccessible(true);
        m.invoke(null, b, stats, lv);
    }
}
