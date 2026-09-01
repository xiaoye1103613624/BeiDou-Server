package org.gms.server.cashshop;

import java.util.ArrayList;
import java.util.List;

/**
 * 窗口商城「XY玩法」页（legacy tab 10）：自定义功能入口/消耗道具，非伤害皮肤本体虚拟 SKU。
 */
public final class XyPlayCashItems {
    public static final int TAB = 10;
    public static final int CATEGORY = 0;
    public static final String CATEGORY_NAME = "XY玩法";

    public record Entry(int itemId, int price, int sort, String name, String remark) {
    }

    /**
     * 上架清单：入口栏 / 幻化锤 / 染色棱镜 / 美容院券 / 背包扩展券(+4/+8)。
     * 不含 {@code 5920001+} 伤害皮肤本体（仍走专用皮肤商店金币购买）。
     */
    public static final List<Entry> ENTRIES;

    static {
        final ArrayList<Entry> list = new ArrayList<>();
        list.add(new Entry(5910000, 50_000, 10, "伤害皮肤栏", "xy-play:damage-skin-opener"));
        list.add(new Entry(5900000, 50_000, 20, "融合外观锻造锤", "xy-play:fusion-anvil"));
        list.add(new Entry(5782000, 50_000, 30, "七彩棱镜", "xy-play:color-prism"));
        list.add(new Entry(5920000, 50_000, 40, "美容院解锁券", "xy-play:beauty-unlock"));
        int sort = 50;
        // +4 在前，+8 在后（同栏位相邻）
        for (InventorySlotCashItems.Spec s : InventorySlotCashItems.all()) {
            if (s.qty() == 4) {
                list.add(new Entry(s.itemId(), s.price(), sort++, s.name(), s.remark()));
            }
        }
        for (InventorySlotCashItems.Spec s : InventorySlotCashItems.all()) {
            if (s.qty() == 8) {
                list.add(new Entry(s.itemId(), s.price(), sort++, s.name(), s.remark()));
            }
        }
        ENTRIES = List.copyOf(list);
    }

    private XyPlayCashItems() {
    }

    public static boolean isDamageSkinBodySku(int itemId) {
        return DamageSkinCashItems.isCashSku(itemId);
    }
}
