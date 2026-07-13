package org.gms.server.setitem;

public record SetSkillMod(int skillId, int addAttackCount, int addLevel, String type) {
    public static SetSkillMod attackCount(int skillId, int add) {
        return new SetSkillMod(skillId, add, 0, "attackCount");
    }

    public static SetSkillMod level(int skillId, int add) {
        return new SetSkillMod(skillId, 0, add, "level");
    }
}
