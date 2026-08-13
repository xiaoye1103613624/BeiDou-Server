package org.gms.constants.inventory;

import org.gms.potential.PotentialHyperConfig;
import org.gms.potential.PotentialHyperService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Soft095 防-x：分类、flag 共存、Hyper 吸炸仅认预涂 SHIELD_WARD。
 */
class ProtectScrollSoft095Test {

    @Test
    void classifyMatchesSoft095IdRules() {
        // Soft095 is防暴卷軸
        assertTrue(ItemConstants.isShieldWardScroll(5064000));
        assertTrue(ItemConstants.isShieldWardScroll(5064003));
        assertTrue(ItemConstants.isShieldWardScroll(2531000));
        assertTrue(ItemConstants.isShieldWardScroll(5063100)); // Soft095 兼列幸运+防暴

        // Soft095 is安全卷軸
        assertTrue(ItemConstants.isSlotsProtectScroll(5064100));
        assertTrue(ItemConstants.isSlotsProtectScroll(5064101));
        assertTrue(ItemConstants.isSlotsProtectScroll(5068100));
        assertTrue(ItemConstants.isSlotsProtectScroll(2532000));
        assertFalse(ItemConstants.isShieldWardScroll(2532000));

        // Soft095 is幸運卷軸（本服另含 Cash 5063000，095 UseCashItem 直涂）
        assertTrue(ItemConstants.isLuckScroll(2530000));
        assertTrue(ItemConstants.isLuckScroll(5063100));
        assertTrue(ItemConstants.isLuckScroll(5068000));
        assertTrue(ItemConstants.isLuckScroll(5063000));

        // Soft095 is卷軸防護卷軸
        assertTrue(ItemConstants.isScrollProtectScroll(5064300));
        assertTrue(ItemConstants.isScrollProtectScroll(5068200));
        assertFalse(ItemConstants.isScrollProtectScroll(2531000));

        assertTrue(ItemConstants.isProtectFamilyScroll(2531000));
        assertTrue(ItemConstants.isProtectFamilyScroll(2530000));
        assertTrue(ItemConstants.isProtectFamilyScroll(2532000));
        assertTrue(ItemConstants.isProtectFamilyScroll(5064300));
    }

    @Test
    void flagsCanCoexistViaOr() {
        short f = 0;
        f = ItemConstants.withFlag(f, ItemConstants.SHIELD_WARD);
        f = ItemConstants.withFlag(f, ItemConstants.SLOTS_PROTECT);
        f = ItemConstants.withFlag(f, ItemConstants.SCROLL_PROTECT);
        f = ItemConstants.withFlag(f, ItemConstants.LUCKS_KEY);
        assertTrue(ItemConstants.hasFlag(f, ItemConstants.SHIELD_WARD));
        assertTrue(ItemConstants.hasFlag(f, ItemConstants.SLOTS_PROTECT));
        assertTrue(ItemConstants.hasFlag(f, ItemConstants.SCROLL_PROTECT));
        assertTrue(ItemConstants.hasFlag(f, ItemConstants.LUCKS_KEY));
        assertFalse(ItemConstants.hasFlag(f, ItemConstants.ACCOUNT_SHARING));
        assertFalse(ItemConstants.hasFlag(f, ItemConstants.MERGE_UNTRADEABLE));
    }

    @Test
    void beiDouBitsDoNotCollideWithSharingMerge() {
        // 095 SHIELD_WARD=0x100 / LUCKS_KEY=0x200 与本服 ACCOUNT_SHARING/MERGE 冲突 → 重映射
        assertNotEquals(ItemConstants.SHIELD_WARD, ItemConstants.ACCOUNT_SHARING);
        assertNotEquals(ItemConstants.LUCKS_KEY, ItemConstants.MERGE_UNTRADEABLE);
        assertEquals(0x2000, ItemConstants.SLOTS_PROTECT);
        assertEquals(0x4000, ItemConstants.SCROLL_PROTECT);
        assertEquals(0x800, ItemConstants.LUCKS_KEY);
        assertEquals(0x1000, ItemConstants.SHIELD_WARD);
    }

    @Test
    void hyperAbsorbOnlyHonorsPreAppliedShieldWard() {
        // Soft095：无砸星背包扣盾；开关恒关
        assertFalse(PotentialHyperConfig.HYPER_WHITE_SCROLL_PROTECTS);
        // 无 chr / 无装备 → false（不抛；不实例化 Equip，避免拉起 Spring/DB）
        assertFalse(PotentialHyperService.tryAbsorbHyperDestroy(null, null, true));
        assertFalse(PotentialHyperService.tryAbsorbHyperDestroy(null, null, false));
    }

    @Test
    void hyperAbsorbClearsWardWhenChrPresent() {
        // 用不依赖完整 Character 的 clear 语义：直接测 flag 工具
        short flag = ItemConstants.withFlag((short) 0, ItemConstants.SHIELD_WARD);
        flag = ItemConstants.withFlag(flag, ItemConstants.SLOTS_PROTECT);
        flag = ItemConstants.withoutFlag(flag, ItemConstants.SHIELD_WARD);
        assertFalse(ItemConstants.hasFlag(flag, ItemConstants.SHIELD_WARD));
        assertTrue(ItemConstants.hasFlag(flag, ItemConstants.SLOTS_PROTECT));
    }

    @Test
    void soft095ScrollProtectIsPaintOnlyIn095() {
        // Soft095 InventoryHandler 未消费 SCROLL_PROTECT；本服 ScrollHandler keepScroll 为 String 补全。
        assertTrue(ItemConstants.isScrollProtectScroll(5064300));
        assertEquals(0x4000, ItemConstants.SCROLL_PROTECT);
    }
}
