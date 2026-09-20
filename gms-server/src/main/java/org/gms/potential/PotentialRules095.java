package org.gms.potential;

/**
 * 095 对齐的潜能规则：品阶(grade) ↔ 状态(state) 映射、激活条数、隐藏态判定。
 * <p>
 * 仅负责「等级/条数/隐藏」这类确定性规则；具体词条的随机选取在 {@link PotentialHyperService}。
 */
public final class PotentialRules095 {
    private PotentialRules095() {}

    /** 品阶下限/上限（1=普通 … 5=神话）。 */
    public static final int MIN_GRADE = 1;
    public static final int MAX_GRADE = 5;

    /** 默认隐藏附加潜能的品阶阈值：低于此品阶的附加潜能需放大镜揭示。 */
    public static final int REVEAL_GRADE_THRESHOLD = 3;

    /** 品阶 → 095 状态（1 普通 / 2 稀有 / 3 史诗 / 4 传说 / 5 神话）。 */
    public static int gradeToState(int grade) {
        return Math.max(MIN_GRADE, Math.min(MAX_GRADE, grade));
    }

    /** 状态 → 品阶。 */
    public static int stateToGrade(int state) {
        return Math.max(MIN_GRADE, Math.min(MAX_GRADE, state));
    }

    /** 该品阶下主潜能激活条数（1~3）。 */
    public static int mainLineCount(int grade) {
        return Math.max(1, Math.min(3, grade));
    }

    /** 该品阶下附加潜能激活条数（1~3）；0 表示无附加潜能。 */
    public static int bonusLineCount(int bonusGrade) {
        if (bonusGrade <= 0) {
            return 0;
        }
        return Math.max(1, Math.min(3, bonusGrade));
    }

    /** 附加潜能是否处于隐藏态（需放大镜揭示）。 */
    public static boolean isBonusHidden(int bonusGrade, boolean revealed) {
        return bonusGrade > 0 && !revealed && bonusGrade < REVEAL_GRADE_THRESHOLD;
    }

    /** 品阶是否合法。 */
    public static boolean isValidGrade(int grade) {
        return grade >= MIN_GRADE && grade <= MAX_GRADE;
    }
}
