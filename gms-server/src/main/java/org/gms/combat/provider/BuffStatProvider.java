package org.gms.combat.provider;

import org.gms.client.Character;
import org.gms.combat.stat.CombatStatModifier;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用技能 / 药水 Buff → CombatStat。
 * <p>
 * v83 的 WATK/MATK/SharpEyes 已在客户端伤害公式内，再映射会双重计算，故不接入。
 * 灵魂最终伤害等「客户端不含」的扩展由 {@link org.gms.soul.SoulBuffStatProvider} 提供。
 */
public final class BuffStatProvider {
    private BuffStatProvider() {}

    public static List<CombatStatModifier> provide(Character chr) {
        List<CombatStatModifier> mods = new ArrayList<>();
        if (chr == null) {
            return mods;
        }
        // 预留：高版本 indieDamR 等客户端未知 Buff 在此追加
        return mods;
    }
}
