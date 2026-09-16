package org.gms.server.cashshop;

/**
 * 分类点击类型。Web 只暴露已实现项，避免误配未接线的类型。
 */
public enum CashShopClickType {
    SHOW_ITEMS,
    OPEN_WINDOW,
    /** 占位：尚未接线，管理端不展示。 */
    SEND_PACKET,
    /** 占位：尚未接线，管理端不展示。 */
    RUN_NPC,
    /** 占位：尚未接线，管理端不展示。 */
    WARP;

    public boolean implemented() {
        return this == SHOW_ITEMS || this == OPEN_WINDOW;
    }

    public static CashShopClickType from(String raw) {
        if (raw == null || raw.isBlank()) {
            return SHOW_ITEMS;
        }
        try {
            return CashShopClickType.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return SHOW_ITEMS;
        }
    }
}
