package org.gms.combat.provider;

import org.gms.client.Character;
import org.gms.combat.stat.CombatStatModifier;

import java.util.ArrayList;
import java.util.List;

/**
 * 天赋战斗属性源占位。
 * <p>
 * 物理/魔法基础、野外精英增伤等与目标或攻击类型相关，
 * 统一在 {@link org.gms.talent.TalentEffects} 中按次命中结算。
 */
public final class TalentStatProvider {
    private TalentStatProvider() {}

    public static List<CombatStatModifier> provide(Character chr) {
        return new ArrayList<>();
    }
}
