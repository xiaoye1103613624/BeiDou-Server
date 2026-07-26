package org.gms.potential;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Hyper 上限 / 成功率表与卷轴 String 文案对齐的冒烟测试。
 */
class PotentialHyperConfigTest {

    @Test
    void maxEnhanceIsTen() {
        assertEquals(10, PotentialHyperConfig.MAX_ENHANCE);
        assertEquals(10, PotentialHyperConfig.getHyperMaxEnhance(2049300));
        assertEquals(2, PotentialHyperConfig.getHyperMaxEnhance(2049309));
        assertEquals(5, PotentialHyperConfig.getHyperMaxEnhance(2049310));
    }

    @Test
    void t5RatesMatchScrollDesc() {
        // 2049300 T5：1星100% … 10星55%
        assertEquals(100, PotentialHyperConfig.getHyperSuccessRate(2049300, 0));
        assertEquals(95, PotentialHyperConfig.getHyperSuccessRate(2049300, 1));
        assertEquals(55, PotentialHyperConfig.getHyperSuccessRate(2049300, 9));
    }

    @Test
    void artifactRatesDeclineToTen() {
        assertEquals(100, PotentialHyperConfig.getHyperSuccessRate(2049306, 0));
        assertEquals(10, PotentialHyperConfig.getHyperSuccessRate(2049306, 9));
    }

    @Test
    void classicASScrollRatesMatch095Names() {
        assertEquals(15, PotentialHyperConfig.successForPotentialScroll(2049409));
        assertEquals(30, PotentialHyperConfig.successForPotentialScroll(2049410));
        assertEquals(50, PotentialHyperConfig.successForPotentialScroll(2049411));
        assertEquals(100, PotentialHyperConfig.successForPotentialScroll(2049408));
        assertEquals(80, PotentialHyperConfig.successForPotentialScroll(2049412));
        assertEquals(50, PotentialHyperConfig.successForPotentialScroll(2049413));
        assertEquals(15, PotentialHyperConfig.successForPotentialScroll(2049753));
        assertEquals(10, PotentialHyperConfig.successForPotentialScroll(2049754));
        assertTrue(PotentialHyperConfig.isClassicEpicPotentialScroll(2049409));
        assertTrue(PotentialHyperConfig.isClassicUniquePotentialScroll(2049413));
        assertTrue(PotentialHyperConfig.isClassicUniquePotentialScroll(2049753));
        assertEquals(100, PotentialHyperConfig.curseForPotentialScroll(2049412));
        assertEquals(60, PotentialHyperConfig.curseForPotentialScroll(2049753));
        assertEquals(0, PotentialHyperConfig.curseForPotentialScroll(2049409));
    }

    @Test
    void ratesStayInPercentRange() {
        for (int id = 2049300; id <= 2049307; id++) {
            for (int star = 0; star < 10; star++) {
                int r = PotentialHyperConfig.getHyperSuccessRate(id, star);
                assertTrue(r >= 1 && r <= 100, "scroll=" + id + " star=" + star + " rate=" + r);
            }
        }
    }

    @Test
    void uniqueRejectedByMainCubeAcceptedBySuperAndPremium() {
        assertFalse(PotentialHyperConfig.allowsMainCubeGrade(4));
        assertTrue(PotentialHyperConfig.allowsPremiumCubeGrade(4));
        assertFalse(PotentialHyperConfig.allowsPremiumCubeGrade(5));
        assertTrue(PotentialHyperConfig.allowsSuperCubeGrade(4));
        assertTrue(PotentialHyperConfig.isMainCube(5062000));
        assertTrue(PotentialHyperConfig.isMainCube(2049910)); // alias
        assertTrue(PotentialHyperConfig.isPremiumCube(5062001));
        assertTrue(PotentialHyperConfig.isPremiumCube(5062100));
        assertTrue(PotentialHyperConfig.isSuperCube(5062002));
        assertTrue(PotentialHyperConfig.isSuperCube(2049916)); // alias
        assertFalse(PotentialHyperConfig.isSuperCube(5062000));
        assertTrue(PotentialHyperConfig.isCashCube(5062000));
        assertTrue(PotentialHyperConfig.isCubeOrGradeOrSocketScroll(5062002));
        assertTrue(PotentialHyperConfig.CLEAR_ON_TRADE);
    }

    @Test
    void hyperEnhanceTable095Aligned() {
        // ★5 四维累计 = 2*5 = 10；★10 = 10+3*5 = 25
        assertEquals(10, HyperEnhanceTable.cumulativeAllStat(5));
        assertEquals(25, HyperEnhanceTable.cumulativeAllStat(10));
        // 武器双攻 ★5 = 3*5 = 15；防具 0
        assertEquals(15, HyperEnhanceTable.cumulativeAtk(5, 1302000));
        assertEquals(0, HyperEnhanceTable.cumulativeAtk(5, 1040000));
        assertEquals(3, PotentialHyperConfig.MAX_SOCKET_SLOTS);
    }

    @Test
    void legendaryAllowedByBothCubes() {
        assertTrue(PotentialHyperConfig.allowsMainCubeGrade(5));
        assertTrue(PotentialHyperConfig.allowsSuperCubeGrade(5));
        assertTrue(PotentialHyperConfig.allowsMainCubeGrade(3));
        assertTrue(PotentialHyperConfig.allowsSuperCubeGrade(2));
        assertFalse(PotentialHyperConfig.allowsMainCubeGrade(0));
        assertFalse(PotentialHyperConfig.allowsSuperCubeGrade(0));
    }

    @Test
    void magnifyLevelBandsMatch095() {
        assertTrue(PotentialHyperConfig.isMagnifyingGlass(2460000));
        assertTrue(PotentialHyperConfig.isMagnifyingGlass(2460003));
        assertFalse(PotentialHyperConfig.isMagnifyingGlass(2460004));
        assertTrue(PotentialHyperConfig.isPotentialFamilyScroll(2460001));

        // reqLevel/10 bands
        assertTrue(PotentialHyperConfig.magnifyFitsEquipLevel(2460000, 30));
        assertTrue(PotentialHyperConfig.magnifyFitsEquipLevel(2460000, 39)); // band 3
        assertFalse(PotentialHyperConfig.magnifyFitsEquipLevel(2460000, 40)); // band 4
        assertTrue(PotentialHyperConfig.magnifyFitsEquipLevel(2460001, 70));
        assertFalse(PotentialHyperConfig.magnifyFitsEquipLevel(2460001, 80));
        assertTrue(PotentialHyperConfig.magnifyFitsEquipLevel(2460002, 120));
        assertFalse(PotentialHyperConfig.magnifyFitsEquipLevel(2460002, 130));
        assertTrue(PotentialHyperConfig.magnifyFitsEquipLevel(2460003, 200));
        // tip REQ.LV=0 的时装/移植装：初级镜应可用
        assertTrue(PotentialHyperConfig.magnifyFitsEquipLevel(2460000, 0));
        assertTrue(PotentialHyperConfig.magnifyFitsEquipLevel(2460001, 0));
    }

    @Test
    void hiddenStateHelpers() {
        assertTrue(PotentialRules095.isMainHidden(3, 0, 0, 0));
        assertTrue(PotentialRules095.isMainHidden(2, 0, -1, 0));
        assertFalse(PotentialRules095.isMainHidden(2, 10001, 10002, 0));
        assertFalse(PotentialRules095.isMainHidden(0, 0, 0, 0));
        assertEquals(2, PotentialRules095.pendingLineCount(0));
        assertEquals(3, PotentialRules095.pendingLineCount(-1));
        assertTrue(PotentialRules095.hasMainPotential(2, 0));
        assertTrue(PotentialRules095.isMainRevealed(10001));
    }

    @Test
    void equipOptionLevelMatches095ReqDiv10() {
        assertEquals(1, PotentialRules095.equipOptionLevel(0));
        assertEquals(1, PotentialRules095.equipOptionLevel(9));
        assertEquals(1, PotentialRules095.equipOptionLevel(10));
        assertEquals(3, PotentialRules095.equipOptionLevel(30));
        assertEquals(3, PotentialRules095.equipOptionLevel(39)); // 095 req/10=3（旧 (req+9)/10 会得到 4）
        assertEquals(10, PotentialRules095.equipOptionLevel(100));
        assertEquals(20, PotentialRules095.equipOptionLevel(200));
        assertEquals(20, PotentialRules095.equipOptionLevel(999));
    }

    @Test
    void miracleDemotesLegendaryAndRejectsUnique() {
        assertFalse(PotentialHyperConfig.allowsMainCubeGrade(4));
        assertTrue(PotentialHyperConfig.allowsMainCubeGrade(5));
        // 传说用奇迹：095 -8→-7，本服压回独特
        assertEquals(4, PotentialRules095.renewGradeMiracle(5));
        assertEquals(4, PotentialRules095.renewGradeMiracle(4));
        assertTrue(PotentialHyperConfig.CUBE_RESET_TO_HIDDEN);
    }

    @Test
    void renewGradeSuperCapsAtLegendary() {
        assertEquals(5, PotentialRules095.renewGradeSuper(5));
        assertEquals(8, PotentialHyperConfig.SUPER_CUBE_UPGRADE_CHANCE);
        // unique→legendary possible (result must stay 4 or 5)
        for (int i = 0; i < 200; i++) {
            int g = PotentialRules095.renewGradeSuper(4);
            assertTrue(g == 4 || g == 5, "super renew unique got " + g);
        }
        for (int i = 0; i < 200; i++) {
            int g = PotentialRules095.renewGradeSuper(3);
            assertTrue(g == 3 || g == 4, "super renew epic got " + g);
        }
    }

    @Test
    void gradeMapsTo095StateAndOptionBands() {
        assertEquals(5, PotentialRules095.gradeTo095State(2)); // 稀有
        assertEquals(6, PotentialRules095.gradeTo095State(3)); // 史诗
        assertEquals(7, PotentialRules095.gradeTo095State(4)); // 独特
        assertEquals(8, PotentialRules095.gradeTo095State(5)); // 传说

        // 史诗：主档 2xxxx，降档 1xxxx；20044/12003 合法
        int epic = 6;
        assertTrue(PotentialRules095.inPreferredBand(20044, epic));
        assertTrue(PotentialRules095.inSecondaryBand(12003, epic));
        assertFalse(PotentialRules095.inPreferredBand(12003, epic));
        assertFalse(PotentialRules095.inGradeBands(30001, epic)); // 独特不进史诗池
        assertFalse(PotentialRules095.inGradeBands(40001, epic));

        // 独特：主档 3xxxx，降档 2xxxx；传说 4xxxx 不进
        int unique = 7;
        assertTrue(PotentialRules095.inPreferredBand(30001, unique));
        assertTrue(PotentialRules095.inSecondaryBand(20044, unique));
        assertFalse(PotentialRules095.inGradeBands(40001, unique));

        // 传说：主档 4xxxx，降档 3xxxx；低阶/附加段不进
        int legend = 8;
        assertTrue(PotentialRules095.inPreferredBand(40001, legend));
        assertTrue(PotentialRules095.inSecondaryBand(30001, legend));
        assertFalse(PotentialRules095.inGradeBands(20044, legend));
        assertFalse(PotentialRules095.inGradeBands(10001, legend));
        assertFalse(PotentialRules095.inGradeBands(1, legend));
        assertFalse(PotentialRules095.inPreferredBand(60001, legend));
        assertFalse(PotentialRules095.inGradeBands(60002, legend));
    }

    @Test
    void bonusQualityTiersMatchMainPercentLadder() {
        java.util.Map<String, Integer> rare = java.util.Map.of(
                "incSTRr", 3, "incDEXr", 3, "incINTr", 3, "incLUKr", 3);
        java.util.Map<String, Integer> epic = java.util.Map.of(
                "incSTRr", 6, "incDEXr", 6, "incINTr", 6, "incLUKr", 6);
        java.util.Map<String, Integer> unique = java.util.Map.of("incSTRr", 9);
        java.util.Map<String, Integer> legend = java.util.Map.of(
                "boss", 1, "incDAMr", 30);
        java.util.Map<String, Integer> proc = java.util.Map.of(
                "prop", 20, "attackType", 1000, "level", 5);

        assertEquals(1, PotentialRules095.bonusQualityTier(rare));
        assertEquals(2, PotentialRules095.bonusQualityTier(epic));
        assertEquals(3, PotentialRules095.bonusQualityTier(unique));
        assertEquals(4, PotentialRules095.bonusQualityTier(legend));
        assertEquals(0, PotentialRules095.bonusQualityTier(proc));

        int legendState = 8;
        assertTrue(PotentialRules095.inBonusPreferredBand(4, legendState));
        assertTrue(PotentialRules095.inBonusSecondaryBand(3, legendState));
        assertFalse(PotentialRules095.inBonusGradeBands(2, legendState));
        assertFalse(PotentialRules095.inBonusGradeBands(1, legendState));

        int epicState = 6;
        assertTrue(PotentialRules095.inBonusPreferredBand(2, epicState));
        assertTrue(PotentialRules095.inBonusSecondaryBand(1, epicState));
        assertFalse(PotentialRules095.inBonusGradeBands(3, epicState));
        assertFalse(PotentialRules095.inBonusGradeBands(4, epicState));

        int rareState = 5;
        assertTrue(PotentialRules095.inBonusPreferredBand(1, rareState));
        assertFalse(PotentialRules095.inBonusSecondaryBand(1, rareState));
        assertEquals(0, PotentialRules095.bonusSecondaryTier(rareState));
    }

    @Test
    void ultimateWeirdBonusCubeIds() {
        assertTrue(PotentialHyperConfig.isUltimateCube(5062003));
        assertTrue(PotentialHyperConfig.isUltimateCube(2049917));
        assertTrue(PotentialHyperConfig.isWeirdCube(2710000));
        assertTrue(PotentialHyperConfig.isWeirdCube(5062004));
        assertTrue(PotentialHyperConfig.allowsWeirdCubeGrade(2));
        assertFalse(PotentialHyperConfig.allowsWeirdCubeGrade(4));
        assertTrue(PotentialHyperConfig.isBonusCube(5062500));
        assertTrue(PotentialHyperConfig.isCashCube(5062003));
        assertTrue(PotentialHyperConfig.isPotentialFamilyScroll(2049600));
    }

    @Test
    void chaosAndResetHelpers() {
        assertTrue(org.gms.constants.inventory.ItemConstants.isChaosScroll(2049119));
        assertTrue(org.gms.constants.inventory.ItemConstants.isPositiveChaosScroll(2049122));
        assertEquals(7, org.gms.constants.inventory.ItemConstants.chaosStatRange(2049119, 5));
        assertEquals(70, org.gms.constants.inventory.ItemConstants.resetScrollSuccess(2049600));
        assertEquals(100, org.gms.constants.inventory.ItemConstants.resetScrollSuccess(2049610));
    }
}
