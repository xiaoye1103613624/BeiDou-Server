package org.gms.combat.stat;

import lombok.Getter;

@Getter
public enum CombatStatType {
    DAM_R("damR", StackRule.ADDITIVE, "伤害%", 0),
    BOSS_DAM_R("bdR", StackRule.ADDITIVE, "Boss伤害%", 0),
    NORMAL_DAM_R("nbdR", StackRule.ADDITIVE, "对普通怪伤害%", 0),
    FINAL_DAM_R("fdR", StackRule.MULTIPLICATIVE, "最终伤害%", 0),
    IGNORE_PDR("ignoreMobpdpR", StackRule.ADDITIVE_CAP, "无视物理防御%", 100),
    IGNORE_MDR("ignoreMobmdR", StackRule.ADDITIVE_CAP, "无视魔法防御%", 100),
    CRIT_RATE("cr", StackRule.ADDITIVE_CAP, "暴击率%", 100),
    CRIT_DAM("cd", StackRule.ADDITIVE, "暴击伤害%", 0);

    public enum StackRule {
        ADDITIVE,
        MULTIPLICATIVE,
        ADDITIVE_CAP
    }

    private final String key;
    private final StackRule stackRule;
    private final String label;
    private final int defaultCap;

    CombatStatType(String key, StackRule stackRule, String label, int defaultCap) {
        this.key = key;
        this.stackRule = stackRule;
        this.label = label;
        this.defaultCap = defaultCap;
    }

    public static CombatStatType fromKey(String key) {
        if (key == null) {
            return null;
        }
        for (CombatStatType t : values()) {
            if (t.key.equals(key)) {
                return t;
            }
        }
        return null;
    }
}
