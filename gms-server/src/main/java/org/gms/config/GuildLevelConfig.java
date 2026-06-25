package org.gms.config;

/**
 * 公会(家族)等级加成配置表
 * 等级由公会累计GP(总贡献)决定，数值先写死在此类中，后续可考虑迁移到后台配置
 */
public final class GuildLevelConfig {
    private GuildLevelConfig() {
    }

    /** 各等级所需的累计GP阈值，下标0对应1级 */
    private static final long[] LEVEL_GP = {
            0, 500, 1500, 3500, 7000, 12000, 19000, 28000, 40000, 55000,
            75000, 100000, 130000, 165000, 205000, 250000, 300000, 360000, 430000, 500000
    };
    /** 每级四维加成(STR/DEX/INT/LUK同值) */
    private static final int[] STAT_BONUS = {
            0, 2, 4, 6, 8, 10, 12, 14, 16, 18,
            20, 22, 24, 26, 28, 30, 32, 34, 36, 40
    };
    /** 每级双攻加成(物攻/魔攻同值) */
    private static final int[] ATK_BONUS = {
            0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
            10, 11, 12, 13, 14, 15, 16, 17, 18, 20
    };

    public static int getMaxLevel() {
        return LEVEL_GP.length;
    }

    /**
     * 根据累计GP求出当前公会等级
     */
    public static int getLevelForContribution(long totalGp) {
        for (int level = LEVEL_GP.length; level >= 1; level--) {
            if (totalGp >= LEVEL_GP[level - 1]) {
                return level;
            }
        }
        return 1;
    }

    public static int getStatBonus(int level) {
        if (level <= 0) {
            return 0;
        }
        int index = Math.min(level, STAT_BONUS.length) - 1;
        return STAT_BONUS[index];
    }

    public static int getAtkBonus(int level) {
        if (level <= 0) {
            return 0;
        }
        int index = Math.min(level, ATK_BONUS.length) - 1;
        return ATK_BONUS[index];
    }
}
