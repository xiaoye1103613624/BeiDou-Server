package org.gms.combat.provider;

import org.gms.client.Character;
import org.gms.combat.stat.CombatStatModifier;

import java.util.ArrayList;
import java.util.List;

/** 潜能战斗属性（S9 未移植 PotentialHyperService 时占位）。 */
public final class PotentialStatProvider {
    private PotentialStatProvider() {}

    public static List<CombatStatModifier> provide(Character chr) {
        return new ArrayList<>();
    }
}
