package org.gms.combat.stat;

import org.gms.config.GameConfig;

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

    /** 从 GameConfig 读 cap；Spring 未就绪或键缺失时回退 {@link #defaults()}。 */
    public static CombatStatCaps fromGameConfig() {
        CombatStatCaps base = defaults();
        try {
            overlay(base.caps, CombatStatType.IGNORE_PDR, "combat_ignore_pdr_cap");
            overlay(base.caps, CombatStatType.IGNORE_MDR, "combat_ignore_mdr_cap");
            overlay(base.caps, CombatStatType.CRIT_RATE, "combat_crit_rate_cap");
        } catch (Throwable ignored) {
            return defaults();
        }
        return base;
    }

    private static void overlay(Map<CombatStatType, Integer> caps, CombatStatType type, String key) {
        int v = GameConfig.getServerInt(key);
        if (v > 0) {
            caps.put(type, v);
        }
    }

    public int capOf(CombatStatType type) {
        try {
            int configured = switch (type) {
                case IGNORE_PDR -> GameConfig.getServerInt("combat_ignore_pdr_cap");
                case IGNORE_MDR -> GameConfig.getServerInt("combat_ignore_mdr_cap");
                case CRIT_RATE -> GameConfig.getServerInt("combat_crit_rate_cap");
                default -> 0;
            };
            if (configured > 0) {
                return configured;
            }
        } catch (Throwable ignored) {
            // 单测 / 启动早期无 Spring
        }
        return caps.getOrDefault(type, Integer.MAX_VALUE);
    }
}
