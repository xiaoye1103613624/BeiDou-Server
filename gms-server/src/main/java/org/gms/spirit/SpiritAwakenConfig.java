package org.gms.spirit;

import java.util.List;

/**
 * 灵韵觉醒全局配置（成功率、消耗、选池比例、等级上限）。
 */
public final class SpiritAwakenConfig {
    private SpiritAwakenConfig() {}

    /** 成功概率（百分比）。 */
    public static final int SUCCESS_RATE = 70;

    /** 消耗材料：灵韵结晶（原炼金术士之石）。 */
    public static final int COST_ITEM_ID = 4021017;
    public static final int COST_ITEM_QTY = 2;
    public static final int COST_MESO = 5_000_000;

    /** 选池：通用 / 本职（百分比，之和应为 100）。 */
    public static final int COMMON_POOL_CHANCE = 40;
    public static final int JOB_POOL_CHANCE = 60;

    /** 通用池内 T0（火眼、稳如泰山）相对概率。 */
    public static final double COMMON_T0_RATE = 0.005;

    public static final int NORMAL_MAX_LEVEL = 3;
    public static final int T0_MAX_LEVEL = 5;

    public static final int MIN_WEAPON_REQ_LEVEL = 70;

    /** 交易收到带灵韵武器时清空灵韵（防通胀）。 */
    public static final boolean CLEAR_SPIRIT_ON_TRADE = true;

    /** 清除灵韵消耗（重置）。 */
    public static final int RESET_COST_ITEM_QTY = 1;
    public static final int RESET_COST_MESO = 1_000_000;

    /** T0 技能 ID。 */
    public static final int SKILL_SHARP_EYES = 3121002;
    public static final int SKILL_STANCE = 1121002;

    /** 影分身黑名单。 */
    public static final List<Integer> SHADOW_PARTNER_BLACKLIST = List.of(
            4111002,
            14111000
    );

    public static boolean isT0(int skillId) {
        return skillId == SKILL_SHARP_EYES || skillId == SKILL_STANCE;
    }

    public static int maxLevel(int skillId) {
        return isT0(skillId) ? T0_MAX_LEVEL : NORMAL_MAX_LEVEL;
    }

    public static boolean isBanned(int skillId) {
        return SHADOW_PARTNER_BLACKLIST.contains(skillId);
    }
}
