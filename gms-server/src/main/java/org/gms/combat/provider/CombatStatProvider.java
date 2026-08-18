package org.gms.combat.provider;

import org.gms.client.Character;
import org.gms.combat.stat.CombatStatModifier;

import java.util.List;

/** 战斗属性来源：只产出 Modifier，不直接改伤害公式。 */
public interface CombatStatProvider {
    List<CombatStatModifier> provide(Character chr);
}
