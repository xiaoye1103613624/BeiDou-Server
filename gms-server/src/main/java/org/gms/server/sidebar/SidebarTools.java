package org.gms.server.sidebar;

import org.gms.dao.entity.SidebarToolConfigDO;

import java.util.ArrayList;
import java.util.List;

/**
 * 右边栏 ServerTool 内存表 — 由 {@link org.gms.service.SidebarToolService} 热加载。
 */
public final class SidebarTools {

    public static final int TOOL_COUNT = 10;

    public record ToolEntry(
            int toolIndex,
            String label,
            String scriptPath,
            String tipTitle,
            String tipDesc,
            boolean enabled
    ) {
        /** 可点击且客户端应显示图标：开启且脚本非空。 */
        public boolean visible() {
            return enabled && scriptPath != null && !scriptPath.isBlank();
        }
    }

    private static final String[] DEFAULT_SCRIPTS = {
            "xy/portal/便民工具",
            "xy/portal/装备中心",
            "xy/portal/兑换中心",
            "xy/portal/VIP会员",
            "xy/portal/成长系统",
            "xy/portal/每日任务",
            "xy/portal/社交系统",
            "xy/portal/收集系统",
            "xy/portal/GM工具",
            "在线奖励_nextlevel",
    };

    private static final String[] DEFAULT_LABELS = {
            "便民工具", "装备中心", "兑换中心", "VIP会员", "成长系统",
            "每日任务", "社交系统", "收集系统", "GM工具", "在线奖励",
    };

    private static final String[] DEFAULT_TIP_TITLES = {
            "便民工具", "装备中心", "兑换中心", "VIP 会员", "成长系统",
            "每日任务", "社交系统", "收集系统", "GM工具", "在线奖励",
    };

    private static final String[] DEFAULT_TIP_DESCS = {
            "仓库、发型、时装、答题、宠物",
            "强化、洗练、套装、转生",
            "物品、封印、抽奖、口令礼包",
            "开通会员、专属商店、赞助",
            "新人福利、转职、等级奖励",
            "探索、副本、跑环、Boss",
            "师徒、家族等社交玩法",
            "卡片、勋章、钓鱼、戒指",
            "管理员专用（长按会员分类亦可）",
            "在线累计时长，领取阶段奖励",
    };

    private static volatile ToolEntry[] ENTRIES = buildDefaults();

    private SidebarTools() {
    }

    private static ToolEntry[] buildDefaults() {
        ToolEntry[] arr = new ToolEntry[TOOL_COUNT];
        for (int i = 0; i < TOOL_COUNT; i++) {
            arr[i] = new ToolEntry(
                    i,
                    DEFAULT_LABELS[i],
                    DEFAULT_SCRIPTS[i],
                    DEFAULT_TIP_TITLES[i],
                    DEFAULT_TIP_DESCS[i],
                    true
            );
        }
        return arr;
    }

    public static void reload(List<SidebarToolConfigDO> rows) {
        ToolEntry[] next = buildDefaults();
        if (rows != null) {
            for (SidebarToolConfigDO row : rows) {
                if (row == null || row.getToolIndex() == null) {
                    continue;
                }
                int idx = row.getToolIndex();
                if (idx < 0 || idx >= TOOL_COUNT) {
                    continue;
                }
                String script = row.getScriptPath() == null ? "" : row.getScriptPath().trim();
                String label = blankTo(row.getLabel(), DEFAULT_LABELS[idx]);
                String title = blankTo(row.getTipTitle(), DEFAULT_TIP_TITLES[idx]);
                String desc = blankTo(row.getTipDesc(), DEFAULT_TIP_DESCS[idx]);
                boolean enabled = row.getEnabled() == null || row.getEnabled() != 0;
                next[idx] = new ToolEntry(idx, label, script, title, desc, enabled);
            }
        }
        ENTRIES = next;
    }

    public static ToolEntry get(int toolIndex) {
        ToolEntry[] cur = ENTRIES;
        if (toolIndex < 0 || toolIndex >= cur.length) {
            return null;
        }
        return cur[toolIndex];
    }

    public static String scriptOf(int toolIndex) {
        ToolEntry e = get(toolIndex);
        if (e == null || !e.visible()) {
            return null;
        }
        return e.scriptPath();
    }

    public static List<ToolEntry> list() {
        ToolEntry[] cur = ENTRIES;
        List<ToolEntry> out = new ArrayList<>(cur.length);
        for (ToolEntry e : cur) {
            out.add(e);
        }
        return out;
    }

    public static List<SidebarToolConfigDO> defaultRows() {
        List<SidebarToolConfigDO> rows = new ArrayList<>(TOOL_COUNT);
        for (int i = 0; i < TOOL_COUNT; i++) {
            rows.add(SidebarToolConfigDO.builder()
                    .toolIndex(i)
                    .label(DEFAULT_LABELS[i])
                    .scriptPath(DEFAULT_SCRIPTS[i])
                    .tipTitle(DEFAULT_TIP_TITLES[i])
                    .tipDesc(DEFAULT_TIP_DESCS[i])
                    .enabled(1)
                    .build());
        }
        return rows;
    }

    private static String blankTo(String value, String fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        return value.trim();
    }
}
