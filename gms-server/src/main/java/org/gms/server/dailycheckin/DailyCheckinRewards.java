package org.gms.server.dailycheckin;

import org.gms.client.Client;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.gms.server.ItemInformationProvider;

/**
 * 每日签到奖励表 — 编辑 {@link #DAYS} 配置 28 天奖励。
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

    private static final Reward[] DAYS;

    static {
        DAYS = new Reward[CYCLE_DAYS];
        for (int d = 0; d < CYCLE_DAYS; d++) {
            DAYS[d] = new Reward(PLACEHOLDER_ICON, 1, NONE);
        }
    }

    private DailyCheckinRewards() {
    }

    private static String slotTabName(int t) {
        return switch (t) {
            case 1 -> "Equip";
            case 2 -> "Use";
            case 3 -> "Set-up";
            case 4 -> "Etc";
            case 5 -> "Cash";
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
        sb.append("Day ").append(day).append('\n');
        for (Grant g : r.grants()) {
            String name = ii.getName(g.itemId());
            if (name == null || name.isBlank()) {
                name = "Item " + g.itemId();
            }
            sb.append("- ").append(name);
            if (g.qty() > 1) {
                sb.append(" x").append(g.qty());
            }
            if (g.expireDays() > 0) {
                sb.append(" (").append(g.expireDays()).append(g.expireDays() == 1 ? " day)" : " days)");
            }
            sb.append('\n');
        }
        if (r.mesos() > 0) {
            sb.append("- ").append(String.format("%,d", r.mesos())).append(" mesos\n");
        }
        if (r.slotType() > 0 && r.slotCount() > 0) {
            sb.append("- +").append(r.slotCount()).append(' ').append(slotTabName(r.slotType())).append(" slots\n");
        }
        int len = sb.length();
        if (len > 0 && sb.charAt(len - 1) == '\n') {
            sb.setLength(len - 1);
        }
        return sb.toString();
    }
}
