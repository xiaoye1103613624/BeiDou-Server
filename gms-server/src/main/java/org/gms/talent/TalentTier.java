package org.gms.talent;

/**
 * 天赋阶层（1~4），对应 NPC 的页签与解锁门槛。
 * 枚举常量名与脚本 9031014.js 中的 {@code TalentTier.PRIMARY/MID/ADVANCED/ULTIMATE} 保持一致。
 */
public enum TalentTier {
    PRIMARY(1, 5, 20037, "精神"),
    MID(2, 10, 20038, "力量"),
    ADVANCED(3, 20, 20039, "财富"),
    ULTIMATE(4, 30, 20040, "智慧");

    private final int order;
    private final int unlockLevel;
    private final int itemId;
    private final String label;

    TalentTier(int order, int unlockLevel, int itemId, String label) {
        this.order = order;
        this.unlockLevel = unlockLevel;
        this.itemId = itemId;
        this.label = label;
    }

    public int getOrder() {
        return order;
    }

    public int getUnlockLevel() {
        return unlockLevel;
    }

    public int getItemId() {
        return itemId;
    }

    public String getLabel() {
        return label;
    }

    public static TalentTier fromOrder(int order) {
        for (TalentTier t : values()) {
            if (t.order == order) {
                return t;
            }
        }
        return null;
    }
}
