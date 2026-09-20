package org.gms.combat.provider;

import org.gms.client.Character;
import org.gms.client.inventory.Equip;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.combat.stat.CombatStatModifier;
import org.gms.potential.PotentialHyperService;

import java.util.ArrayList;
import java.util.List;

/**
 * 潜能战斗属性 Provider：聚合角色已穿戴装备的暴击/爆伤/伤害%/首领/无视等战斗类潜能。
 * <p>
 * 平坦/百分比属性由 {@link PotentialHyperService#computeBonus} 经 {@code Character#recalcEquipStats}
 * 进入面板，这里只补「战斗侧」属性，避免重复。
 */
public final class PotentialStatProvider {
    private PotentialStatProvider() {}

    public static List<CombatStatModifier> provide(Character chr) {
        List<CombatStatModifier> out = new ArrayList<>();
        if (chr == null) {
            return out;
        }
        int charLevel = chr.getLevel();
        for (Item item : chr.getInventory(InventoryType.EQUIPPED)) {
            if (!(item instanceof Equip equip)) {
                continue;
            }
            out.addAll(PotentialHyperService.collectCombat(equip, charLevel));
        }
        return out;
    }
}
