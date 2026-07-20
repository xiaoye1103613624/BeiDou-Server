package org.gms.combat.provider;

import org.gms.client.Character;
import org.gms.client.inventory.Equip;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.combat.stat.CombatStatJson;
import org.gms.combat.stat.CombatStatModifier;
import org.gms.combat.stat.CombatStatSource;
import org.gms.constants.inventory.ItemConstants;
import org.gms.dao.entity.EquipEnhanceRuleDO;
import org.gms.server.combat.CombatSourceManager;

import java.util.ArrayList;
import java.util.List;

public final class EnhanceStatProvider {
    private EnhanceStatProvider() {}

    public static List<CombatStatModifier> provide(Character chr) {
        List<CombatStatModifier> mods = new ArrayList<>();
        if (chr == null) {
            return mods;
        }
        CombatSourceManager.loadOrSeed();
        for (Item item : chr.getInventory(InventoryType.EQUIPPED).list()) {
            if (!(item instanceof Equip equip)) {
                continue;
            }
            int level = equip.getLevel() & 0xFF;
            if (level <= 0) {
                continue;
            }
            String type = classifyEquip(equip.getItemId());
            for (EquipEnhanceRuleDO rule : CombatSourceManager.listEnhanceRules()) {
                if (rule.getEnabled() != null && rule.getEnabled() == 0) {
                    continue;
                }
                int min = rule.getMinLevel() == null ? 0 : rule.getMinLevel();
                int max = rule.getMaxLevel() == null ? 99 : rule.getMaxLevel();
                if (level < min || level > max) {
                    continue;
                }
                if (!matchType(rule.getEquipType(), type)) {
                    continue;
                }
                mods.addAll(CombatStatJson.fromEnhanceStatsJson(
                        rule.getStatsJson(), level, CombatStatSource.ENHANCE,
                        "enhance:" + rule.getId() + ":" + equip.getItemId()));
            }
        }
        return mods;
    }

    private static boolean matchType(String ruleType, String equipType) {
        if (ruleType == null || ruleType.isBlank() || "ALL".equalsIgnoreCase(ruleType)) {
            return true;
        }
        return ruleType.equalsIgnoreCase(equipType);
    }

    private static String classifyEquip(int itemId) {
        if (ItemConstants.isWeapon(itemId)) {
            return "WEAPON";
        }
        // 饰品粗分：戒指/吊坠/腰带等
        int prefix = itemId / 10000;
        // 115 肩饰等同饰品增强规则
        if (prefix == 111 || prefix == 112 || prefix == 113 || prefix == 114 || prefix == 115 || prefix == 103) {
            return "ACCESSORY";
        }
        return "ARMOR";
    }
}
