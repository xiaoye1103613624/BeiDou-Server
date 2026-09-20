package org.gms.talent;

import org.gms.server.life.Monster;

/**
 * 天赋配置（学习成功率、兑换比例、成长曲线、效果参数）。
 */
public final class TalentConfig {
    private TalentConfig() {}

    public static final int MAX_TALENT_LEVEL = 10;

    // —— 阶段累计（达到后解锁下一阶的额外成功率加成）——
    public static final int[] ULTIMATE_STEP_THRESHOLDS = {0, 5, 8, 10};
    public static final int[] ULTIMATE_RATES = {50, 65, 85, 100};

    // —— 兑换消耗（NPC 3030000 收集，按阶层门槛）——
    public static final int[] EXCHANGE_COST_LEVELS = {5, 10, 20, 30};
    public static final int[] EXCHANGE_REWARD = {1, 2, 3, 4};

    // —— 效果参数 ——
    public static final double HP_RECOVERY_PER_LEVEL = 0.01;
    public static final double HP_ON_REVIVE_PER_LEVEL = 0.05;
    public static final double MP_ON_REVIVE_PER_LEVEL = 0.03;
    public static final double CRIT_PER_LEVEL = 0.01;
    public static final double DAMAGE_PER_LEVEL = 0.01;
    public static final double BOSS_PER_LEVEL = 0.01;

    public static final double FIELD_DROP_PER_LEVEL = 0.03;
    public static final double FIELD_MESO_PER_LEVEL = 0.03;
    public static final double FIELD_ELITE_RATE_PER_LEVEL = 0.05;
    public static final double FIELD_MESO_GAIN_PER_LEVEL = 0.03;
    public static final double FIELD_ELITE_MESO_PER_LEVEL = 0.05;

    public static final double OVERWORK_BONUS_PER_LEVEL = 0.03;
    public static final double GHOST_SKILL_PER_LEVEL = 0.05;

    public static final int STANCE_THORN_BASE = 200;
    public static final double STANCE_THORN_PER_LEVEL = 0.05;
    public static final double STANCE_SPLASH_PER_LEVEL = 0.05;

    public static final double EXPERTISE_CAST_PER_LEVEL = 0.02;
    public static final double EXPERTISE_COOLDOWN_PER_LEVEL = 0.02;

    public static final double SPIDERWEB_RESIST_PER_LEVEL = 0.01;
    public static final double CURSE_RESIST_PER_LEVEL = 0.02;
    public static final double ZOMBIE_RESIST_PER_LEVEL = 0.02;
    public static final double POISON_RESIST_PER_LEVEL = 0.02;
    public static final double SEAL_RESIST_PER_LEVEL = 0.03;
    public static final double DARKNESS_RESIST_PER_LEVEL = 0.03;

    public static int ultimateSuccessRate(int currentLevel) {
        for (int i = ULTIMATE_STEP_THRESHOLDS.length - 1; i >= 0; i--) {
            if (currentLevel >= ULTIMATE_STEP_THRESHOLDS[i]) {
                return ULTIMATE_RATES[i];
            }
        }
        return ULTIMATE_RATES[0];
    }

    public static int exchangeRewardForLevel(int level) {
        for (int i = EXCHANGE_COST_LEVELS.length - 1; i >= 0; i--) {
            if (level >= EXCHANGE_COST_LEVELS[i]) {
                return EXCHANGE_REWARD[i];
            }
        }
        return 0;
    }

    /** 是否为「领域精英」强化条目对应的怪物（按 ID 区间粗略判定）。 */
    public static boolean isFieldEliteId(Monster monster) {
        if (monster == null) {
            return false;
        }
        int id = monster.getId();
        return id >= 9000000 && id < 9500000;
    }
}
