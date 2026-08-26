package org.gms.reincarnation;

import org.gms.client.Character;
import org.gms.client.Skill;
import org.gms.client.SkillFactory;
import org.gms.constants.game.GameConstants;
import org.gms.constants.skills.Evan;
import org.gms.constants.skills.Legend;
import org.gms.constants.skills.Noblesse;

/**
 * 轮回专用技能 ID（后缀 1021），与各职业线新手技能树对齐。
 * 不使用英雄之回声（1005），避免客户端 PQ/回声 WZ 冲突导致闪退。
 */
public final class ReincarnationSkills {
    public static final int SKILL = 1021;
    public static final int SKILL_CYGNUS = 10001021;
    public static final int SKILL_LEGEND = 20001021;
    public static final int SKILL_EVAN = 20011021;

    public static final int[] ALL_SKILL_IDS = {
            SKILL, SKILL_CYGNUS, SKILL_LEGEND, SKILL_EVAN
    };

    private ReincarnationSkills() {}

    public static boolean isReincarnationSkill(int skillId) {
        for (int id : ALL_SKILL_IDS) {
            if (skillId == id) {
                return true;
            }
        }
        return false;
    }

    public static int skillIdFor(Character chr) {
        if (chr == null) {
            return SKILL;
        }
        int jobId = chr.getJob().getId();
        if (GameConstants.isCygnus(jobId)) {
            return SKILL_CYGNUS;
        }
        if (GameConstants.isAran(jobId)) {
            return SKILL_LEGEND;
        }
        if (jobId == 2001 || (jobId >= 2200 && jobId <= 2218)) {
            if (SkillFactory.getSkill(SKILL_EVAN) != null) {
                return SKILL_EVAN;
            }
        }
        return SKILL;
    }

    public static Skill skillFor(Character chr) {
        return SkillFactory.getSkill(skillIdFor(chr));
    }
}
