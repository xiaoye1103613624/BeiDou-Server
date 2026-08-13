package org.gms.server.setitem;

import org.gms.combat.stat.CombatStatType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 验证套装 Option 回退与 combat key 映射（无 WZ Data mock 时用 SetBonus 手工）。
 */
class SetBonusCombatKeyTest {

    @Test
    void genesisOption60024AddsBossDamage() {
        SetBonus bonus = new SetBonus(2);
        // 模拟 SetTiersV2Parser.applyKnownOptionFallback
        bonus.putCombatStat(CombatStatType.BOSS_DAM_R.getKey(), 10);
        assertEquals(10, bonus.getCombatStat(CombatStatType.BOSS_DAM_R));
        assertEquals(0, bonus.getCombatStat(CombatStatType.NORMAL_DAM_R));
    }

    @Test
    void nbdRMustNotMergeIntoBdR() {
        SetBonus bonus = new SetBonus(3);
        bonus.putCombatStat(CombatStatType.NORMAL_DAM_R.getKey(), 6);
        bonus.putCombatStat(CombatStatType.BOSS_DAM_R.getKey(), 10);
        assertEquals(6, bonus.getCombatStat(CombatStatType.NORMAL_DAM_R));
        assertEquals(10, bonus.getCombatStat(CombatStatType.BOSS_DAM_R));
    }

    @Test
    void cumulativeSumsBossDamageAcrossTiers() {
        SetBonus t2 = new SetBonus(2);
        t2.putCombatStat(CombatStatType.BOSS_DAM_R.getKey(), 10);
        SetBonus t3 = new SetBonus(3);
        t3.putCombatStat(CombatStatType.BOSS_DAM_R.getKey(), 10);
        SetBonus t4 = new SetBonus(4);
        t4.putCombatStat(CombatStatType.BOSS_DAM_R.getKey(), 10);

        SetBonus total = new SetBonus();
        total.merge(t2);
        total.merge(t3);
        total.merge(t4);
        assertEquals(30, total.getCombatStat(CombatStatType.BOSS_DAM_R));
    }
}
