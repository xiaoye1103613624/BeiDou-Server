package org.gms.server.cashshop;

/**
 * 伤害皮肤本体虚拟 SKU：{@code itemId = BASE + skinId}（历史窗口商城上架用，现已改走专用皮肤商店）。
 * <p>
 * skinId 来自 Effect WZ {@code damageSkin/<id>} / {@code xy_damageskin_catalog}；
 * {@code 5910000} 仅打开 picker。购买解锁 {@code xy_damageskin_inventory}，不发背包实物。
 * 窗口商城「XY玩法」页只卖入口道具，见 {@link XyPlayCashItems}。
 */
public final class DamageSkinCashItems {
    /** itemId = BASE + skinId；skin 1 → 5920001 */
    public static final int BASE = 5_920_000;
    public static final int MAX_ITEM_ID = 5_929_999;
    public static final int DEFAULT_NX_PRICE = 50_000;
    /** 入口券（打开 UI），非皮肤本体 */
    public static final int OPENER_ITEM_ID = 5_910_000;

    private DamageSkinCashItems() {
    }

    public static boolean isCashSku(int itemId) {
        return itemId > BASE && itemId <= MAX_ITEM_ID;
    }

    public static int toSkinId(int itemId) {
        if (!isCashSku(itemId)) {
            return -1;
        }
        return itemId - BASE;
    }

    public static int toItemId(int skinId) {
        if (skinId <= 0) {
            return -1;
        }
        final long id = (long) BASE + skinId;
        if (id > MAX_ITEM_ID) {
            return -1;
        }
        return (int) id;
    }

    public static String defaultName(int skinId) {
        return "伤害皮肤 #" + skinId;
    }
}
