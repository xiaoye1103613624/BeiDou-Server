package org.gms.combat.stat;

public record CombatStatModifier(
        CombatStatType type,
        int value,
        CombatStatSource source,
        String sourceId,
        int priority
) {
    public CombatStatModifier(CombatStatType type, int value, CombatStatSource source, String sourceId) {
        this(type, value, source, sourceId, 0);
    }
}
