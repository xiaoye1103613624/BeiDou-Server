package org.gms.spirit;

import org.gms.constants.skills.*;

/**
 * 灵韵池子里的技能 ID 多为英雄/飞侠等固定 ID；穿戴时映射到角色本职同名技，
 * 否则客户端技能窗对跨职技不予展示/更新。
 */
public final class SpiritSkillRemap {
    private SpiritSkillRemap() {}

    private static final int[] MAPLE_WARRIOR_IDS = {
            Hero.MAPLE_WARRIOR,
            Paladin.MAPLE_WARRIOR,
            DarkKnight.MAPLE_WARRIOR,
            FPArchMage.MAPLE_WARRIOR,
            ILArchMage.MAPLE_WARRIOR,
            Bishop.MAPLE_WARRIOR,
            Bowmaster.MAPLE_WARRIOR,
            Marksman.MAPLE_WARRIOR,
            NightLord.MAPLE_WARRIOR,
            Shadower.MAPLE_WARRIOR,
            Buccaneer.MAPLE_WARRIOR,
            Corsair.MAPLE_WARRIOR,
            Aran.MAPLE_WARRIOR
    };

    private static final int[] HEROS_WILL_IDS = {
            Hero.HEROS_WILL,
            Paladin.HEROS_WILL,
            DarkKnight.HEROS_WILL,
            FPArchMage.HEROS_WILL,
            ILArchMage.HEROS_WILL,
            Bishop.HEROS_WILL,
            Bowmaster.HEROS_WILL,
            Marksman.HEROS_WILL,
            NightLord.HEROS_WILL,
            Shadower.HEROS_WILL,
            5121008, // 冲锋队长 · 勇士的意志
            5221010, // 船长 · 勇士的意志（Corsair.java 常量名冲突）
            Aran.HEROS_WILL
    };

    /** 穿戴生效用：按职业映射到本职对应技；无映射则原样返回。 */
    public static int forJob(int skillId, int jobId) {
        if (skillId <= 0) {
            return skillId;
        }
        if (contains(MAPLE_WARRIOR_IDS, skillId)) {
            return mapleWarriorOf(jobId);
        }
        if (contains(HEROS_WILL_IDS, skillId) || skillId == Hero.HEROS_WILL) {
            return herosWillOf(jobId);
        }
        // 轻功：飞侠系用本职轻功，其它职业仍授原 ID（可能仅数据层有效）
        if (skillId == 4101004 || skillId == 4201003 || skillId == 4001003 || skillId == 14101004) {
            return hasteOf(jobId, skillId);
        }
        return skillId;
    }

    private static boolean contains(int[] arr, int id) {
        for (int v : arr) {
            if (v == id) {
                return true;
            }
        }
        return false;
    }

    private static int mapleWarriorOf(int jobId) {
        int job = jobId;
        if (job >= 2110 && job <= 2112) {
            return Aran.MAPLE_WARRIOR;
        }
        if (job >= 2210 && job <= 2218) {
            return Evan.MAPLE_WARRIOR;
        }
        return switch (job / 100) {
            case 11 -> Hero.MAPLE_WARRIOR;
            case 12 -> Paladin.MAPLE_WARRIOR;
            case 13 -> DarkKnight.MAPLE_WARRIOR;
            case 21 -> FPArchMage.MAPLE_WARRIOR;
            case 22 -> ILArchMage.MAPLE_WARRIOR;
            case 23 -> Bishop.MAPLE_WARRIOR;
            case 31 -> Bowmaster.MAPLE_WARRIOR;
            case 32 -> Marksman.MAPLE_WARRIOR;
            case 41 -> NightLord.MAPLE_WARRIOR;
            case 42 -> Shadower.MAPLE_WARRIOR;
            case 51 -> Buccaneer.MAPLE_WARRIOR;
            case 52 -> Corsair.MAPLE_WARRIOR;
            default -> Hero.MAPLE_WARRIOR;
        };
    }

    private static int herosWillOf(int jobId) {
        int job = jobId;
        if (job >= 2110 && job <= 2112) {
            return Aran.HEROS_WILL;
        }
        if (job >= 2210 && job <= 2218) {
            return Evan.HEROS_WILL;
        }
        return switch (job / 100) {
            case 11 -> Hero.HEROS_WILL;
            case 12 -> Paladin.HEROS_WILL;
            case 13 -> DarkKnight.HEROS_WILL;
            case 21 -> FPArchMage.HEROS_WILL;
            case 22 -> ILArchMage.HEROS_WILL;
            case 23 -> Bishop.HEROS_WILL;
            case 31 -> Bowmaster.HEROS_WILL;
            case 32 -> Marksman.HEROS_WILL;
            case 41 -> NightLord.HEROS_WILL;
            case 42 -> Shadower.HEROS_WILL;
            case 51 -> 5121008;
            case 52 -> 5221010;
            default -> Hero.HEROS_WILL;
        };
    }

    private static int hasteOf(int jobId, int fallback) {
        int branch = jobId / 100;
        if (branch == 41 || (jobId >= 410 && jobId < 420)) {
            return 4101004;
        }
        if (branch == 42 || (jobId >= 420 && jobId < 430)) {
            return 4201003; // 侠盗轻功（若无此技则回退）
        }
        if (jobId >= 1400 && jobId < 1500) {
            return 14101004;
        }
        if (jobId >= 400 && jobId < 410) {
            return 4001003;
        }
        return fallback;
    }
}
