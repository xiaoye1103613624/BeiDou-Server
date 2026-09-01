package org.gms.server.cashshop;

import org.gms.server.ItemInformationProvider;
import org.springframework.util.StringUtils;

/**
 * 现金商城商品显示名：优先 WZ {@link ItemInformationProvider#getName(int)}，否则回退 DB 名。
 */
public final class CashShopItemNames {
    private static final String NO_NAME = "NO-NAME";

    private CashShopItemNames() {
    }

    public static boolean isUsableWzName(String name) {
        return StringUtils.hasText(name) && !NO_NAME.equals(name);
    }

    /**
     * @return 可用显示名，或 {@code null} 若 WZ 与 fallback 均不可用
     */
    public static String resolve(int itemId, String fallbackName) {
        if (DamageSkinCashItems.isCashSku(itemId)) {
            final int skinId = DamageSkinCashItems.toSkinId(itemId);
            if (StringUtils.hasText(fallbackName)) {
                return truncate(fallbackName.strip());
            }
            return truncate(DamageSkinCashItems.defaultName(skinId));
        }
        final String wz = ItemInformationProvider.getInstance().getName(itemId);
        if (isUsableWzName(wz)) {
            return truncate(wz);
        }
        if (StringUtils.hasText(fallbackName)) {
            return truncate(fallbackName.strip());
        }
        return null;
    }

    public static String truncate(String name) {
        if (name == null) {
            return null;
        }
        if (name.length() > CashShopWindowPackets.MAX_NAME) {
            return name.substring(0, CashShopWindowPackets.MAX_NAME);
        }
        return name;
    }
}
