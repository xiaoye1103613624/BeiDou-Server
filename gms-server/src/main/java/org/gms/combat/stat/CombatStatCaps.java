package org.gms.combat.stat;

import java.util.EnumMap;
import java.util.Map;

public final class CombatStatCaps {
    private final Map<CombatStatType, Integer> caps;

    private CombatStatCaps(Map<CombatStatType, Integer> caps) {
        this.caps = caps;
    }

    public static CombatStatCaps defaults() {
        Map<CombatStatType, Integer> map = new EnumMap<>(CombatStatType.class);
        for (CombatStatType type : CombatStatType.values()) {
            if (type.getDefaultCap() > 0) {
                map.put(type, type.getDefaultCap());
            }
        }
        return new CombatStatCaps(map);
    }

    public int capOf(CombatStatType type) {
        return caps.getOrDefault(type, Integer.MAX_VALUE);
    }
}
