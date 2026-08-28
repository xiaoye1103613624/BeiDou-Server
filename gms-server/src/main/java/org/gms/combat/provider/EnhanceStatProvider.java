package org.gms.combat.provider;

import org.gms.client.Character;
import org.gms.combat.stat.CombatStatModifier;

import java.util.ArrayList;
import java.util.List;

/** 装备强化战斗属性（S9 未移植 CombatSourceManager 时占位）。 */
public final class EnhanceStatProvider {
    private EnhanceStatProvider() {}

    public static List<CombatStatModifier> provide(Character chr) {
        return new ArrayList<>();
    }
}
