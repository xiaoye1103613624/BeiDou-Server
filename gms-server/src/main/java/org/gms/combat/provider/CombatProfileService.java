package org.gms.combat.provider;

import org.gms.client.Character;
import org.gms.combat.stat.CombatStatModifier;
import org.gms.combat.stat.CombatStatProfile;
import org.gms.combat.stat.CombatStatResolver;
import org.gms.server.setitem.SetBonus;

import java.util.ArrayList;
import java.util.List;

/** 汇总多来源 Modifier → Profile。 */
public final class CombatProfileService {
    private static final CombatStatResolver RESOLVER = new CombatStatResolver();

    private CombatProfileService() {}

    public static List<CombatStatModifier> collect(Character chr) {
        return collect(chr, null);
    }

    /**
     * @param precomputedSetBonus 若非 null，则复用，避免再次扫描装备套装。
     */
    public static List<CombatStatModifier> collect(Character chr, SetBonus precomputedSetBonus) {
        List<CombatStatModifier> mods = new ArrayList<>();
        if (precomputedSetBonus != null) {
            SetBonusStatProvider.appendFromSetBonus(mods, precomputedSetBonus, "set:total");
        } else {
            mods.addAll(SetBonusStatProvider.provide(chr));
        }
        mods.addAll(EnhanceStatProvider.provide(chr));
        mods.addAll(PotentialStatProvider.provide(chr));
        mods.addAll(CarryItemStatProvider.provide(chr));
        mods.addAll(TalentStatProvider.provide(chr));
        mods.addAll(BuffStatProvider.provide(chr));
        return mods;
    }

    public static CombatStatProfile resolve(Character chr) {
        return RESOLVER.resolve(collect(chr, null));
    }

    public static CombatStatProfile resolve(Character chr, SetBonus precomputedSetBonus) {
        return RESOLVER.resolve(collect(chr, precomputedSetBonus));
    }
}