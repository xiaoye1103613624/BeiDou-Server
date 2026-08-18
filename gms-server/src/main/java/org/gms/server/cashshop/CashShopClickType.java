package org.gms.server.cashshop;

/**
 * 分类点击类型。默认展示商品；未实现的类型先占位，便于 Web 下拉扩展。
 */
public enum CashShopClickType {
    SHOW_ITEMS,
    OPEN_WINDOW,
    SEND_PACKET,
    RUN_NPC,
    WARP;

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
