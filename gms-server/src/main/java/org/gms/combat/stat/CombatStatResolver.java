package org.gms.combat.stat;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class CombatStatResolver {
    private final CombatStatCaps caps;

    public CombatStatResolver() {
        this(CombatStatCaps.defaults());
    }

    public CombatStatResolver(CombatStatCaps caps) {
        this.caps = caps;
    }

    public CombatStatProfile resolve(List<CombatStatModifier> modifiers) {
        if (modifiers == null || modifiers.isEmpty()) {
            return CombatStatProfile.EMPTY;
        }

        Map<CombatStatType, Integer> additive = new EnumMap<>(CombatStatType.class);
        List<Integer> finalSources = new ArrayList<>();

        for (CombatStatModifier mod : modifiers) {
            if (mod == null || mod.type() == null || mod.value() == 0) {
                continue;
            }
            switch (mod.type().getStackRule()) {
                case MULTIPLICATIVE -> finalSources.add(mod.value());
                case ADDITIVE, ADDITIVE_CAP -> additive.merge(mod.type(), mod.value(), Integer::sum);
            }
        }

        int ignorePDR = clamp(additive.getOrDefault(CombatStatType.IGNORE_PDR, 0), CombatStatType.IGNORE_PDR);
        int ignoreMDR = clamp(additive.getOrDefault(CombatStatType.IGNORE_MDR, 0), CombatStatType.IGNORE_MDR);
        int critRate = clamp(additive.getOrDefault(CombatStatType.CRIT_RATE, 0), CombatStatType.CRIT_RATE);

        double finalMul = CombatStatProfile.computeFinalDamageMultiplier(finalSources);

        return new CombatStatProfile(
                additive.getOrDefault(CombatStatType.DAM_R, 0),
                additive.getOrDefault(CombatStatType.BOSS_DAM_R, 0),
                additive.getOrDefault(CombatStatType.NORMAL_DAM_R, 0),
                finalSources,
                ignorePDR,
                ignoreMDR,
                critRate,
                additive.getOrDefault(CombatStatType.CRIT_DAM, 0),
                finalMul,
                additive.getOrDefault(CombatStatType.PAD_R, 0),
                additive.getOrDefault(CombatStatType.MAD_R, 0)
        );
    }

    private int clamp(int value, CombatStatType type) {
        int cap = caps.capOf(type);
        return Math.max(0, Math.min(value, cap));
    }
}
