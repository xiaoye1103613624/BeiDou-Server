package org.gms.combat.provider;

import org.gms.client.Character;
import org.gms.client.inventory.Equip;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.combat.stat.CombatStatModifier;
import org.gms.combat.stat.CombatStatSource;
import org.gms.combat.stat.CombatStatType;
import org.gms.potential.PotentialHyperService;

import java.util.ArrayList;
import java.util.List;

/**
 * 将装备主/附加潜能、灵魂、星岩中的战斗向选项接入 CombatProfile
 * （暴击率/暴击伤害、伤害%、无视防御、攻魔%等）。面板平坦属性仍由 {@code recalcEquipStats} 处理。
 */
public final class PotentialStatProvider {
    private PotentialStatProvider() {}

    public static List<CombatStatModifier> provide(Character chr) {
        List<CombatStatModifier> mods = new ArrayList<>();
        if (chr == null) {
            return mods;
        }
        for (Item item : chr.getInventory(InventoryType.EQUIPPED).list()) {
            if (!(item instanceof Equip equip)) {
                continue;
            }
            // 等级缩放选项需角色等级（与 recalcEquipStats 一致）
            PotentialHyperService.StatBonus b = PotentialHyperService.computeBonus(equip, chr.getLevel());
            String sid = "pot:" + equip.getItemId() + ":" + equip.getPosition();
            add(mods, CombatStatType.CRIT_RATE, b.critRate, sid);
            add(mods, CombatStatType.CRIT_DAM, b.critDam, sid);
            add(mods, CombatStatType.DAM_R, b.damR, sid);
            add(mods, CombatStatType.BOSS_DAM_R, b.bossDamR, sid);
            add(mods, CombatStatType.IGNORE_PDR, b.ignoreDef, sid);
            add(mods, CombatStatType.IGNORE_MDR, b.ignoreDef, sid);
            add(mods, CombatStatType.PAD_R, b.padR, sid);
            add(mods, CombatStatType.MAD_R, b.madR, sid);
        }
        return mods;
    }

    private static void add(List<CombatStatModifier> mods, CombatStatType type, int value, String sourceId) {
        if (value != 0) {
            mods.add(new CombatStatModifier(type, value, CombatStatSource.EQUIP, sourceId));
        }
    }
}
