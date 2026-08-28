package org.gms.server.equipgrowth;

import org.gms.client.Character;

import java.util.Collections;
import java.util.Map;

/** S9 stub: equip growth DB overlay not fully ported. */
public final class EquipGrowthConfigManager {
    private static final EquipGrowthConfigManager INSTANCE = new EquipGrowthConfigManager();

    private EquipGrowthConfigManager() {}

    public static EquipGrowthConfigManager get() {
        return INSTANCE;
    }

    public Map<Integer, Integer> sumEquippedSkillBonuses(Character chr) {
        return Collections.emptyMap();
    }
}
