package org.gms.combat.provider;

import org.gms.client.Character;
import org.gms.combat.stat.CombatStatModifier;

import java.util.ArrayList;
import java.util.List;

/** 携带物战斗属性（S9 未移植 CombatSourceManager 时占位）。 */
public final class CarryItemStatProvider {
    private CarryItemStatProvider() {}

    public static List<CombatStatModifier> provide(Character chr) {
        return new ArrayList<>();
    }
}
