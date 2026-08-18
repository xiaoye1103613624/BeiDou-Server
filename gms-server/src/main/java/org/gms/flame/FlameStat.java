package org.gms.flame;

/**
 * 对齐 265 FlameStat：val=池下标，exGrade=exGradeOption 编码类型。
 * LevelReduction 在官机 tip 通常不上绿。
 */
public enum FlameStat {
    STR(0, 0),
    DEX(1, 1),
    INT(2, 2),
    LUK(3, 3),
    STRDEX(4, 4),
    STRINT(5, 5),
    STRLUK(6, 6),
    DEXINT(7, 7),
    DEXLUK(8, 8),
    INTLUK(9, 9),
    ATTACK(10, 17),
    MAGIC_ATTACK(11, 18),
    DEFENSE(12, 13),
    MAX_HP(13, 10),
    MAX_MP(14, 11),
    SPEED(15, 19),
    JUMP(16, 20),
    ALL_STATS(17, 24),
    BOSS_DAMAGE(18, 21),
    DAMAGE(19, 23),
    LEVEL_REDUCTION(20, 12);

    private final int val;
    private final int exGrade;

    FlameStat(int val, int exGrade) {
        this.val = val;
        this.exGrade = exGrade;
    }

    public int getVal() {
        return val;
    }

    public int getExGrade() {
        return exGrade;
    }

    public static FlameStat byVal(int val) {
        for (FlameStat s : values()) {
            if (s.val == val) {
                return s;
            }
        }
        return null;
    }

    public static FlameStat byExGrade(int ex) {
        for (FlameStat s : values()) {
            if (s.exGrade == ex) {
                return s;
            }
        }
        return null;
    }
}
