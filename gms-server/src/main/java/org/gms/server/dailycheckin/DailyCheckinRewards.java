package org.gms.server.dailycheckin;

import org.gms.client.Client;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.gms.dao.entity.DailyCheckinRewardDO;
import org.gms.server.ItemInformationProvider;
import org.gms.util.I18nUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * 每日签到奖励表 — 默认占位，启动后由 {@code DailyCheckinService} 从 DB 热加载。
 */
public final class DailyCheckinRewards {

    private static final long ONE_DAY_MS = 86_400_000L;
    public static final int CYCLE_DAYS = 28;
    /** 低于该等级不可见/不可用签到窗口 */
    public static final int MIN_LEVEL = 10;

    public record Grant(int itemId, int qty, int expireDays) {
        public Grant(int itemId, int qty) {
            this(itemId, qty, 0);
        }
    }

    public record Reward(int iconItemId, int mesos, Grant[] grants, int slotType, int slotCount) {
        Reward(int iconItemId, int mesos, Grant[] grants) {
            this(iconItemId, mesos, grants, 0, 0);
        }
    }

    private static final Grant[] NONE = new Grant[0];
    private static final int PLACEHOLDER_ICON = 2000000;

    private static volatile Reward[] DAYS = buildPlaceholders();

    private DailyCheckinRewards() {
    }

    private static Reward[] buildPlaceholders() {
        Reward[] days = new Reward[CYCLE_DAYS];
        for (int d = 0; d < CYCLE_DAYS; d++) {
            days[d] = new Reward(PLACEHOLDER_ICON, 1, NONE);
        }
        return days;
    }

    /**
     * 用 DB 配置覆盖内存奖励表（不足 28 天的用占位补齐）。
     */
    public static synchronized void reload(List<DailyCheckinRewardDO> rows) {
        Reward[] next = buildPlaceholders();
        if (rows != null) {
            for (DailyCheckinRewardDO row : rows) {
                if (row == null || row.getDay() == null) {
                    continue;
                }
                int day = row.getDay();
                if (day < 1 || day > CYCLE_DAYS) {
                    continue;
                }
                next[day - 1] = fromDo(row);
            }
        }
        DAYS = next;
    }

    private static Reward fromDo(DailyCheckinRewardDO row) {
        List<Grant> grants = new ArrayList<>(2);
        int itemId = nz(row.getItemId());
        int itemQty = nz(row.getItemQty());
        if (itemId > 0 && itemQty > 0) {
            grants.add(new Grant(itemId, itemQty, nz(row.getExpireDays())));
        }
        int item2Id = nz(row.getItem2Id());
        int item2Qty = nz(row.getItem2Qty());
        if (item2Id > 0 && item2Qty > 0) {
            grants.add(new Grant(item2Id, item2Qty, nz(row.getItem2Expire())));
        }
        int icon = nz(row.getIconItemId());
        if (icon <= 0) {
            icon = itemId > 0 ? itemId : (item2Id > 0 ? item2Id : PLACEHOLDER_ICON);
        }
        return new Reward(
                icon,
                nz(row.getMesos()),
                grants.isEmpty() ? NONE : grants.toArray(Grant[]::new),
                nz(row.getSlotType()),
                nz(row.getSlotCount()));
    }

    private static int nz(Integer v) {
        return v == null ? 0 : v;
    }

    private static String slotTabName(int t) {
        return switch (t) {
            case 1 -> I18nUtil.getMessage("InventoryType.EQUIP");
            case 2 -> I18nUtil.getMessage("InventoryType.USE");
            case 3 -> I18nUtil.getMessage("InventoryType.SETUP");
            case 4 -> I18nUtil.getMessage("InventoryType.ETC");
            case 5 -> I18nUtil.getMessage("InventoryType.CASH");
            default -> "";
        };
    }

    public static int iconItemId(int day) {
        if (day < 1 || day > DAYS.length) {
            return 0;
        }
        return DAYS[day - 1].iconItemId();
    }

    public static boolean grantDay(Client c, int day) {
        if (day < 1 || day > DAYS.length) {
            return false;
        }
        Reward r = DAYS[day - 1];
        if (r.mesos() > 0) {
            c.getPlayer().gainMeso(r.mesos(), true);
        }
        for (Grant g : r.grants()) {
            long expiration = g.expireDays() > 0 ? System.currentTimeMillis() + g.expireDays() * ONE_DAY_MS : -1;
            InventoryManipulator.addById(c, g.itemId(), (short) g.qty(), expiration);
        }
        if (r.slotType() > 0 && r.slotCount() > 0) {
            c.getPlayer().gainSlots(r.slotType(), r.slotCount());
        }
        return true;
    }

    public static String tooltip(int day) {
        if (day < 1 || day > DAYS.length) {
            return "";
        }
        Reward r = DAYS[day - 1];
        ItemInformationProvider ii = ItemInformationProvider.getInstance();
        StringBuilder sb = new StringBuilder();
        sb.append(I18nUtil.getMessage("DailyCheckin.tooltip.day", day)).append('\n');
        for (Grant g : r.grants()) {
            String name = ii.getName(g.itemId());
            if (name == null || name.isBlank()) {
                name = I18nUtil.getMessage("DailyCheckin.tooltip.itemFallback", g.itemId());
            }
            sb.append("- ").append(name);
            if (g.qty() > 1) {
                sb.append(" x").append(g.qty());
            }
            if (g.expireDays() > 0) {
                sb.append(" (").append(I18nUtil.getMessage("DailyCheckin.tooltip.expireDays", g.expireDays())).append(')');
            }
            sb.append('\n');
        }
        if (r.mesos() > 0) {
            sb.append("- ").append(String.format("%,d", r.mesos()))
                    .append(' ')
                    .append(I18nUtil.getMessage("DailyCheckin.tooltip.mesos"))
                    .append('\n');
        }
        if (r.slotType() > 0 && r.slotCount() > 0) {
            sb.append("- +").append(r.slotCount()).append(' ')
                    .append(slotTabName(r.slotType()))
                    .append(I18nUtil.getMessage("DailyCheckin.tooltip.slots"))
                    .append('\n');
        }
        int len = sb.length();
        if (len > 0 && sb.charAt(len - 1) == '\n') {
            sb.setLength(len - 1);
        }
        return sb.toString();
    }

    public static String claimSuccessMessage(int day) {
        return I18nUtil.getMessage("DailyCheckin.claimSuccess", day);
    }
}
