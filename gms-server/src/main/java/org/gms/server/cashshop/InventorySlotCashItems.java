package org.gms.server.cashshop;

import org.gms.client.inventory.InventoryType;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 窗口商城背包栏扩展券（虚拟履约，不进背包）。
 * <p>
 * {@code 911x000} = +8（WZ Special/0911 原 ID，图标空壳）；
 * {@code 911x004} = +4（虚拟 ID，对齐经典扩栏 4000 NX）。
 */
public final class InventorySlotCashItems {
    public record Spec(int itemId, int invType, int qty, int price, String name, String remark) {
    }

    private static final Map<Integer, Spec> BY_ID = new LinkedHashMap<>();

    static {
        // +8（既有）
        put(9110000, InventoryType.EQUIP, 8, 6_400, "装备栏扩展券(+8)", "xy-play:slot-equip-8");
        put(9111000, InventoryType.USE, 8, 6_400, "消耗栏扩展券(+8)", "xy-play:slot-use-8");
        put(9112000, InventoryType.SETUP, 8, 6_400, "设置栏扩展券(+8)", "xy-play:slot-setup-8");
        put(9113000, InventoryType.ETC, 8, 6_400, "其他栏扩展券(+8)", "xy-play:slot-etc-8");
        // +4（新增虚拟 SKU）
        put(9110004, InventoryType.EQUIP, 4, 4_000, "装备栏扩展券(+4)", "xy-play:slot-equip-4");
        put(9111004, InventoryType.USE, 4, 4_000, "消耗栏扩展券(+4)", "xy-play:slot-use-4");
        put(9112004, InventoryType.SETUP, 4, 4_000, "设置栏扩展券(+4)", "xy-play:slot-setup-4");
        put(9113004, InventoryType.ETC, 4, 4_000, "其他栏扩展券(+4)", "xy-play:slot-etc-4");
    }

    private static void put(int itemId, InventoryType type, int qty, int price, String name, String remark) {
        BY_ID.put(itemId, new Spec(itemId, type.getType(), qty, price, name, remark));
    }

    private InventorySlotCashItems() {
    }

    public static boolean isSlotCoupon(int itemId) {
        return BY_ID.containsKey(itemId);
    }

    public static Spec get(int itemId) {
        return BY_ID.get(itemId);
    }

    public static Collection<Spec> all() {
        return BY_ID.values();
    }

    /** 商城图标回退：同栏位 +8 原 ID（插件再映射到有图现金道具）。 */
    public static int iconFallbackItemId(int itemId) {
        final Spec s = BY_ID.get(itemId);
        if (s == null) {
            return itemId;
        }
        return switch (s.invType()) {
            case 1 -> 9110000;
            case 2 -> 9111000;
            case 3 -> 9112000;
            case 4 -> 9113000;
            default -> itemId;
        };
    }
}
