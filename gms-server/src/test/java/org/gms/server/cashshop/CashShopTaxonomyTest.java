package org.gms.server.cashshop;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** 100xxxx 帽子 vs 170xxxx 现金武器，禁止收进同一 kCats 桶。 */
class CashShopTaxonomyTest {

    @Test
    void capPrefixIsOnly100() {
        assertEquals(CashShopTaxonomy.CAP, CashShopTaxonomy.forItemId(1000000));
        assertEquals(CashShopTaxonomy.CAP, CashShopTaxonomy.forItemId(1002357));
        assertEquals("帽子", CashShopTaxonomy.forItemId(1000000).name());
        assertEquals(2, CashShopTaxonomy.forItemId(1000000).legacyTab());
        assertEquals(0, CashShopTaxonomy.forItemId(1000000).legacyCategory());
    }

    @Test
    void cashWeapon170IsWeaponNotCap() {
        assertEquals(170, CashShopTaxonomy.itemTypePrefix(1703629));
        assertTrue(CashShopTaxonomy.isWeaponItemType(170));
        assertFalse(CashShopTaxonomy.isWeaponItemType(100));
        assertEquals(CashShopTaxonomy.WEAPON, CashShopTaxonomy.forItemId(1703629));
        assertEquals(CashShopTaxonomy.WEAPON, CashShopTaxonomy.forItemId(1703633));
        assertEquals("武器", CashShopTaxonomy.forItemId(1703629).name());
        assertEquals(2, CashShopTaxonomy.forItemId(1703629).legacyTab());
        assertEquals(8, CashShopTaxonomy.forItemId(1703629).legacyCategory());
    }

    @Test
    void percentTenMustNotCollapse170Or110IntoCap() {
        // 错误写法 type%10==0 会把 170 武器、110 披风都收进帽子
        assertEquals(CashShopTaxonomy.WEAPON, CashShopTaxonomy.forItemType(170));
        assertEquals(CashShopTaxonomy.CAPE, CashShopTaxonomy.forItemType(110));
        assertEquals(CashShopTaxonomy.CAP, CashShopTaxonomy.forItemType(100));
    }

    @Test
    void otherWeaponPrefixes() {
        assertEquals(CashShopTaxonomy.WEAPON, CashShopTaxonomy.forItemId(1092010));
        assertEquals(CashShopTaxonomy.WEAPON, CashShopTaxonomy.forItemId(1302000));
        assertEquals(CashShopTaxonomy.WEAPON, CashShopTaxonomy.forItemId(1492000));
        assertEquals(CashShopTaxonomy.WEAPON, CashShopTaxonomy.forItemId(1212000));
        assertEquals(CashShopTaxonomy.FACE, CashShopTaxonomy.forItemId(1010000));
        assertEquals(CashShopTaxonomy.EYE, CashShopTaxonomy.forItemId(1020000));
    }

    @Test
    void remappableHatCategoryEvenWithoutClientSyncRemark() {
        assertTrue(CashShopTaxonomy.isRemappableAutoCategory(2, 0, "帽子", null));
        assertTrue(CashShopTaxonomy.isRemappableAutoCategory(1, 0, "帽子", null));
        assertTrue(CashShopTaxonomy.isKCatsDisplayName("帽子"));
        assertTrue(CashShopTaxonomy.isKCatsDisplayName("武器"));
    }
}
