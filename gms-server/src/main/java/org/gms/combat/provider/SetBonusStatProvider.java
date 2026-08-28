package org.gms.combat.provider;

import org.gms.client.Character;
import org.gms.combat.stat.CombatStatModifier;
import org.gms.combat.stat.CombatStatProfile;
import org.gms.combat.stat.CombatStatSource;
import org.gms.combat.stat.CombatStatType;
import org.gms.server.setitem.SetBonus;
import org.gms.server.setitem.SetItemManager;

import java.util.ArrayList;
import java.util.List;

public final class SetBonusStatProvider {
    private SetBonusStatProvider() {}

    public static List<CombatStatModifier> provide(Character chr) {
        List<CombatStatModifier> mods = new ArrayList<>();
        if (chr == null) {
            return mods;
        }
        SetBonus bonus = SetItemManager.getTotalSetBonus(chr);
        appendFromSetBonus(mods, bonus, "set:total");
        return mods;
    }

    public static CombatStatProfile resolveFromCharacter(Character chr) {
        return CombatProfileService.resolve(chr);
    }

    public static void appendFromSetBonus(List<CombatStatModifier> mods, SetBonus bonus, String sourceId) {
        if (bonus == null) {
            return;
        }
        for (CombatStatType type : CombatStatType.values()) {
            int v = bonus.getCombatStat(type);
            if (v != 0 && type.getStackRule() != CombatStatType.StackRule.MULTIPLICATIVE) {
                mods.add(new CombatStatModifier(type, v, CombatStatSource.SET_BONUS, sourceId));
            }
        }
        for (int fd : bonus.finalDamageSources) {
            if (fd != 0) {
                mods.add(new CombatStatModifier(CombatStatType.FINAL_DAM_R, fd, CombatStatSource.SET_BONUS, sourceId));
            }
        }
        if (bonus.finalDamageSources.isEmpty()) {
            for (int fd : bonus.collectFinalDamageSources()) {
                if (fd != 0) {
                    mods.add(new CombatStatModifier(CombatStatType.FINAL_DAM_R, fd, CombatStatSource.SET_BONUS, sourceId));
                }
            }
        }
    }
}
