package org.gms.talent;

public enum TalentTier {
    PRIMARY(1, "初级", 20),
    MID(2, "中级", 20),
    ADVANCED(3, "高级", 0),
    ULTIMATE(4, "终极", 0);

    private final int order;
    private final String label;
    /** 花费多少本阶点数才可解锁下一阶（初级→中级、中级→高级）。 */
    private final int pointsToUnlockNext;

    TalentTier(int order, String label, int pointsToUnlockNext) {
        this.order = order;
        this.label = label;
        this.pointsToUnlockNext = pointsToUnlockNext;
    }

    public int order() {
        return order;
    }

    public String label() {
        return label;
    }

    public int pointsToUnlockNext() {
        return pointsToUnlockNext;
    }
}
