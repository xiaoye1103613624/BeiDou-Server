package org.gms.server.equipgrowth;

import org.gms.combat.stat.CombatStatJson;
import org.gms.combat.stat.CombatStatModifier;
import org.gms.combat.stat.CombatStatSource;
import org.gms.combat.stat.CombatStatType;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EquipGrowthTipManagerTest {

    @Test
    void hasGrowthDataOnlyWhenWzGrowthTree() {
        // 纯 Hyper / 砸卷 / itemLevel 无 WZ 树 → 不显示成长 tip
        assertFalse(EquipGrowthTipManager.hasGrowthData(3, 1, false));
        assertFalse(EquipGrowthTipManager.hasGrowthData(0, 4, false));
        assertFalse(EquipGrowthTipManager.hasGrowthData(0, 1, true));
        assertFalse(EquipGrowthTipManager.hasGrowthData(0, 1, false, false));
        // 黑龙传说指环等：WZ info/level 可成长
        assertTrue(EquipGrowthTipManager.hasGrowthData(0, 1, false, true));
        assertTrue(EquipGrowthTipManager.hasGrowthData(5, 2, true, true));
    }

    @Test
    void buildGrowthTextEmptyWithoutWzTree() {
        assertEquals("", EquipGrowthTipManager.buildGrowthText(1002000, 5, 1, 0, Map.of(), 0));
        assertEquals("", EquipGrowthTipManager.buildGrowthText(1002000, 0, 3, 0, Map.of(), 1));
        assertEquals("", EquipGrowthTipManager.buildGrowthText(1002000, 0, 1, 7,
                Map.of(CombatStatType.DAM_R, 7), 0));
    }

    @Test
    void buildGrowthTextOmitsHyperAndScrollCombat() {
        Map<CombatStatType, Integer> combat = Map.of(
                CombatStatType.DAM_R, 7,
                CombatStatType.FINAL_DAM_R, 14);
        String text = EquipGrowthTipManager.buildGrowthText(1113084, 5, 2, 7, combat, 6);
        assertTrue(text.contains("装备成长属性"));
        assertFalse(text.contains("Hyper"));
        assertFalse(text.contains("砸卷"));
        assertFalse(text.contains("伤害"));
        assertFalse(text.contains("全属性"));
    }

    @Test
    void buildGrowthTextHasTitleForWzGrowthEvenIfNodesUnread() {
        // 无 WZ 静态数据时节点为空，仍保留标题（客户端可本地补全分段）
        String text = EquipGrowthTipManager.buildGrowthText(1113084, 0, 2, 0, Map.of(), 6);
        assertTrue(text.contains("装备成长属性"));
        assertFalse(text.contains("成长等级"));
    }

    @Test
    void buildGrowthTextSegmentLabelsUseNodeIndex() {
        // 有可读节点时：节点1→「1级效果」、节点2→「2级效果」（黑龙 Lv2 验收）
        Map<String, Integer> n1 = EquipGrowthTipManager.levelNodeStatsMin(1113084, 1);
        Map<String, Integer> n2 = EquipGrowthTipManager.levelNodeStatsMin(1113084, 2);
        String text = EquipGrowthTipManager.buildGrowthText(1113084, 0, 2, 0, Map.of(), 6);
        assertTrue(text.contains("装备成长属性"));
        if (!n1.isEmpty()) {
            assertTrue(text.contains("1级效果"), text);
        }
        if (!n2.isEmpty()) {
            assertTrue(text.contains("2级效果"), text);
        }
    }

    @Test
    void aggregateEnhanceModsSumsPerLevelCombat() {
        String json = "{\"perLevel\":{\"damR\":1,\"fdR\":2}}";
        List<CombatStatModifier> mods = CombatStatJson.fromEnhanceStatsJson(
                json, 7, CombatStatSource.ENHANCE, "t");
        Map<CombatStatType, Integer> totals = EquipGrowthTipManager.aggregateEnhanceMods(mods);
        assertEquals(7, totals.get(CombatStatType.DAM_R));
        assertEquals(14, totals.get(CombatStatType.FINAL_DAM_R));
    }
}
