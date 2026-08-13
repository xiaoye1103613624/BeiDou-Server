package org.gms.combat.provider;

import org.gms.client.Character;
import org.gms.client.inventory.InventoryType;
import org.gms.combat.stat.CombatStatJson;
import org.gms.combat.stat.CombatStatModifier;
import org.gms.combat.stat.CombatStatSource;
import org.gms.constants.inventory.ItemConstants;
import org.gms.dao.entity.CarryItemStatDO;
import org.gms.server.combat.CombatSourceManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 按配置表中的 itemId 索引查找背包，避免扫描全部格子。
 */
public final class CarryItemStatProvider {
    private CarryItemStatProvider() {}

    public static List<CombatStatModifier> provide(Character chr) {
        List<CombatStatModifier> mods = new ArrayList<>();
        if (chr == null) {
            return mods;
        }
        Map<Integer, CarryItemStatDO> carryMap = CombatSourceManager.carryMap();
        if (carryMap.isEmpty()) {
            return mods;
        }
        for (Map.Entry<Integer, CarryItemStatDO> e : carryMap.entrySet()) {
            CarryItemStatDO row = e.getValue();
            if (row == null || (row.getEnabled() != null && row.getEnabled() == 0)) {
                continue;
            }
            int itemId = e.getKey();
            boolean needEquip = row.getRequireEquipped() != null && row.getRequireEquipped() == 1;
            if (needEquip) {
                if (!chr.haveItemEquipped(itemId)) {
                    continue;
                }
            } else if (!ownsCarryItem(chr, itemId)) {
                continue;
            }
            mods.addAll(CombatStatJson.fromCarryStatsJson(
                    row.getStatsJson(), CombatStatSource.CARRY_ITEM, "item:" + itemId));
        }
        return mods;
    }

    /** 装备栏或对应背包栏任一持有即可。 */
    private static boolean ownsCarryItem(Character chr, int itemId) {
        if (chr.haveItemEquipped(itemId)) {
            return true;
        }
        InventoryType bagType = ItemConstants.getInventoryType(itemId);
        return chr.getInventory(bagType).findById(itemId) != null;
    }
}
