package org.gms.spirit;

import java.util.ArrayList;
import java.util.List;

/**
 * 灵韵觉醒技能池定义与职业映射。
 */
public final class SpiritAwakenPools {
    private SpiritAwakenPools() {}

    public record WeightedSkill(int skillId, int weight) {}

    public enum JobBranch {
        HERO,
        PALADIN,
        DARK_KNIGHT,
        FP,
        IL,
        BISHOP,
        BOWMASTER,
        MARKSMAN,
        NIGHT_LORD,
        SHADOWER,
        BUCCANEER,
        CORSAIR,
        UNKNOWN
    }

    /** 通用池非 T0 条目（T0 权重运行时按 COMMON_T0_RATE 注入）。 */
    public static final List<WeightedSkill> COMMON_BASE = List.of(
            new WeightedSkill(1121000, 80),   // 冒险岛勇士
            new WeightedSkill(2301004, 120),  // 祝福
            new WeightedSkill(2311003, 60),   // 神圣祈祷
            new WeightedSkill(1301007, 100),  // 神圣之躯
            new WeightedSkill(4101004, 140),  // 轻功（飞侠二转，非影分身）
            new WeightedSkill(1001003, 90),   // 剑气护体
            new WeightedSkill(2001003, 90),   // 魔法盾
            new WeightedSkill(1121011, 50)    // 勇士的意志
    );

    public static List<WeightedSkill> buildCommonPool() {
        int baseSum = 0;
        for (WeightedSkill s : COMMON_BASE) {
            baseSum += s.weight();
        }
        // 两个 T0 各占 COMMON_T0_RATE：t0W / (base + 2*t0W) = rate
        // => t0W = base * rate / (1 - 2*rate)
        double rate = SpiritAwakenConfig.COMMON_T0_RATE;
        double denom = 1.0 - 2.0 * rate;
        int t0Weight = Math.max(1, (int) Math.round(baseSum * rate / Math.max(0.001, denom)));
        List<WeightedSkill> pool = new ArrayList<>(COMMON_BASE.size() + 2);
        pool.addAll(COMMON_BASE);
        pool.add(new WeightedSkill(SpiritAwakenConfig.SKILL_SHARP_EYES, t0Weight));
        pool.add(new WeightedSkill(SpiritAwakenConfig.SKILL_STANCE, t0Weight));
        return pool;
    }

    public static List<WeightedSkill> forBranch(JobBranch branch) {
        return switch (branch) {
            case HERO -> List.of(
                    new WeightedSkill(1121008, 40),
                    new WeightedSkill(1121010, 50),
                    new WeightedSkill(1121006, 70),
                    new WeightedSkill(1111005, 90),
                    new WeightedSkill(1111006, 90),
                    new WeightedSkill(1111008, 70)
            );
            case PALADIN -> List.of(
                    new WeightedSkill(1221009, 40),
                    new WeightedSkill(1221011, 35),
                    new WeightedSkill(1221007, 70),
                    new WeightedSkill(1211002, 80)
            );
            case DARK_KNIGHT -> List.of(
                    new WeightedSkill(1311006, 80),
                    new WeightedSkill(1311005, 60),
                    new WeightedSkill(1311001, 90),
                    new WeightedSkill(1311002, 90),
                    new WeightedSkill(1321003, 70)
            );
            case FP -> List.of(
                    new WeightedSkill(2121007, 35),
                    new WeightedSkill(2121003, 50),
                    new WeightedSkill(2121001, 55),
                    new WeightedSkill(2111002, 80),
                    new WeightedSkill(2111003, 80)
            );
            case IL -> List.of(
                    new WeightedSkill(2221007, 35),
                    new WeightedSkill(2221006, 50),
                    new WeightedSkill(2221001, 55),
                    new WeightedSkill(2211002, 80),
                    new WeightedSkill(2211006, 80)
            );
            case BISHOP -> List.of(
                    new WeightedSkill(2321007, 45),
                    new WeightedSkill(2321008, 30),
                    new WeightedSkill(2321001, 55),
                    new WeightedSkill(2311004, 70)
            );
            case BOWMASTER -> List.of(
                    new WeightedSkill(3121004, 40),
                    new WeightedSkill(3121006, 45),
                    new WeightedSkill(3121003, 60),
                    new WeightedSkill(3111006, 90),
                    new WeightedSkill(3111004, 80)
            );
            case MARKSMAN -> List.of(
                    new WeightedSkill(3221001, 50),
                    new WeightedSkill(3221007, 40),
                    new WeightedSkill(3221005, 45),
                    new WeightedSkill(3221003, 60),
                    new WeightedSkill(3211006, 80)
            );
            case NIGHT_LORD -> List.of(
                    new WeightedSkill(4121007, 40),
                    new WeightedSkill(4121004, 55),
                    new WeightedSkill(4111005, 80),
                    new WeightedSkill(4121003, 45)
            );
            case SHADOWER -> List.of(
                    new WeightedSkill(4221001, 40),
                    new WeightedSkill(4211006, 70),
                    new WeightedSkill(4211002, 80),
                    new WeightedSkill(4221007, 50),
                    new WeightedSkill(4221003, 45)
            );
            case BUCCANEER -> List.of(
                    new WeightedSkill(5121001, 50),
                    new WeightedSkill(5121002, 60),
                    new WeightedSkill(5121004, 55),
                    new WeightedSkill(5121007, 45)
            );
            case CORSAIR -> List.of(
                    new WeightedSkill(5221004, 45),
                    new WeightedSkill(5221006, 40),
                    new WeightedSkill(5221003, 55),
                    new WeightedSkill(5221008, 50)
            );
            case UNKNOWN -> List.of();
        };
    }

    /**
     * 按角色 Job 映射到具体转职分支（含骑士团近似归类）。
     */
    public static JobBranch resolveBranch(int jobId) {
        int j = jobId;
        // 冒险家
        if (j >= 110 && j <= 112) {
            return JobBranch.HERO;
        }
        if (j >= 120 && j <= 122) {
            return JobBranch.PALADIN;
        }
        if (j >= 130 && j <= 132) {
            return JobBranch.DARK_KNIGHT;
        }
        if (j >= 210 && j <= 212) {
            return JobBranch.FP;
        }
        if (j >= 220 && j <= 222) {
            return JobBranch.IL;
        }
        if (j >= 230 && j <= 232) {
            return JobBranch.BISHOP;
        }
        if (j >= 310 && j <= 312) {
            return JobBranch.BOWMASTER;
        }
        if (j >= 320 && j <= 322) {
            return JobBranch.MARKSMAN;
        }
        if (j >= 410 && j <= 412) {
            return JobBranch.NIGHT_LORD;
        }
        if (j >= 420 && j <= 422) {
            return JobBranch.SHADOWER;
        }
        if (j >= 510 && j <= 512) {
            return JobBranch.BUCCANEER;
        }
        if (j >= 520 && j <= 522) {
            return JobBranch.CORSAIR;
        }
        // 骑士团
        if (j >= 1110 && j <= 1112) {
            return JobBranch.HERO;
        }
        if (j >= 1210 && j <= 1212) {
            return JobBranch.FP;
        }
        if (j >= 1310 && j <= 1312) {
            return JobBranch.BOWMASTER;
        }
        if (j >= 1410 && j <= 1412) {
            return JobBranch.NIGHT_LORD;
        }
        if (j >= 1510 && j <= 1512) {
            return JobBranch.BUCCANEER;
        }
        // 战神 → 战士池（黑骑近似）
        if (j >= 2100 && j <= 2112) {
            return JobBranch.DARK_KNIGHT;
        }
        // 龙神 → 法师池（火毒近似）
        if (j >= 2200 && j <= 2218) {
            return JobBranch.FP;
        }
        // 一转粗分
        if (j == 100 || (j >= 1100 && j < 1110)) {
            return JobBranch.HERO;
        }
        if (j == 200 || (j >= 1200 && j < 1210)) {
            return JobBranch.FP;
        }
        if (j == 300 || (j >= 1300 && j < 1310)) {
            return JobBranch.BOWMASTER;
        }
        if (j == 400 || (j >= 1400 && j < 1410)) {
            return JobBranch.NIGHT_LORD;
        }
        if (j == 500 || (j >= 1500 && j < 1510)) {
            return JobBranch.BUCCANEER;
        }
        return JobBranch.UNKNOWN;
    }
}
