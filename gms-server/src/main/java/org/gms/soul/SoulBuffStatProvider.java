package org.gms.soul;

import org.gms.client.Character;
import org.gms.combat.stat.CombatStatModifier;
import org.gms.combat.stat.CombatStatSource;
import org.gms.combat.stat.CombatStatType;

import java.util.ArrayList;
import java.util.List;

/** 武公等灵魂技能的临时最终伤害 Buff → CombatProfile。 */
public final class SoulBuffStatProvider {
    private SoulBuffStatProvider() {}

    public static List<CombatStatModifier> provide(Character chr) {
        List<CombatStatModifier> mods = new ArrayList<>();
        if (chr == null) {
            return mods;
        }
        int fd = SoulWeaponService.getActiveFinalDamR(chr.getId());
        if (fd > 0) {
            mods.add(new CombatStatModifier(CombatStatType.FINAL_DAM_R, fd,
                    CombatStatSource.SKILL_BUFF, "soul:buff"));
        }
        return mods;
    }
}
