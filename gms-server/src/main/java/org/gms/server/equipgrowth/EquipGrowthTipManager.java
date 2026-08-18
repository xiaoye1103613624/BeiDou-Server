package org.gms.server.equipgrowth;

import org.gms.client.Character;
import org.gms.client.inventory.Equip;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.combat.stat.CombatStatModifier;
import org.gms.combat.stat.CombatStatType;
import org.gms.constants.inventory.ItemConstants;
import org.gms.dao.entity.EquipEnhanceRuleDO;
import org.gms.server.ItemInformationProvider;
import org.gms.server.combat.CombatSourceManager;
import org.gms.combat.stat.CombatStatJson;
import org.gms.combat.stat.CombatStatSource;
import org.gms.util.Pair;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 装备成长属性 tip 文本与主 tip 分色增量。
 * <p>
 * 展示规则（2026-08-03）：仅 WZ {@code info/level} 可成长装显示；文案按级分段
 * 「N级效果」+ 该级属性（对齐套装「N件套效果」），不含 Hyper / 砸卷战斗% 等无关内容。
 * <p>
 * 规范：悬停不默认拉全量；变更点（升级/强化/forceUpdate）可推送摘要一次。
 */
public final class EquipGrowthTipManager {
    private EquipGrowthTipManager() {}

    /** 与套装 tip 文案一致：中文属性名 + " : +N"；攻魔/血魔相等时合并。 */
    private static final String COLON = " : ";
    private static final boolean MERGE_EQUAL = true;

    /**
     * 装备变更后推送成长 tip 摘要（同 0x17B）。无成长数据时推送空包以便客户端清缓存。
     * 短去重：同角色+指纹 1.5s 内不重复推。
     */
    private static final long PUSH_DEDUP_MS = 1_500L;
    private static final java.util.concurrent.ConcurrentHashMap<String, Long> LAST_PUSH =
            new java.util.concurrent.ConcurrentHashMap<>();

    public static void pushGrowthTipIfNeeded(Character chr, Equip equip) {
        if (chr == null || equip == null || chr.getClient() == null) {
            return;
        }
        try {
            int itemId = equip.getItemId();
            int enhance = equip.getEnhance() & 0xFF;
            int itemLevel = equip.getItemLevel() & 0xFF;
            int scroll = equip.getLevel() & 0xFF;
            long flameKey = equip.getExGradeOption();
            String key = chr.getId() + ":" + itemId + ":" + enhance + ":" + itemLevel + ":" + scroll + ":" + flameKey;
            long now = System.currentTimeMillis();
            Long prev = LAST_PUSH.get(key);
            if (prev != null && now - prev < PUSH_DEDUP_MS) {
                return;
            }
            LAST_PUSH.put(key, now);

            boolean has = hasGrowthData(equip);
            String text = has ? buildGrowthText(equip) : "";
            int[] growthBonus = has ? growthBonusByStatIndex(itemId, itemLevel) : null;
            int[] flameBonus = flameBonusByStatIndex(equip);
            chr.sendPacket(org.gms.util.PacketCreator.equipGrowthTip(itemId, has, text, growthBonus, flameBonus));
            if (LAST_PUSH.size() > 4096) {
                LAST_PUSH.entrySet().removeIf(e -> now - e.getValue() > 60_000L);
            }
        } catch (Throwable ignored) {
            // 展示推送失败不影响库存事务
        }
    }

    public static Equip findEquip(Character chr, int itemId) {
        if (chr == null || itemId <= 0) {
            return null;
        }
        Equip best = null;
        for (InventoryType type : new InventoryType[]{InventoryType.EQUIPPED, InventoryType.EQUIP, InventoryType.CASH}) {
            for (Item item : chr.getInventory(type).list()) {
                if (item instanceof Equip eq && eq.getItemId() == itemId) {
                    if (best == null || growthScore(eq) > growthScore(best)) {
                        best = eq;
                    }
                }
            }
        }
        return best;
    }

    public static boolean hasGrowthData(Equip equip) {
        if (equip == null) {
            return false;
        }
        return hasGrowthDataForItemId(equip.getItemId());
    }

    /** WZ 可成长或 DB 自定义；DB enabled=0 则不显示。 */
    public static boolean hasGrowthDataForItemId(int itemId) {
        EquipGrowthConfigManager cfg = EquipGrowthConfigManager.get();
        if (cfg != null && cfg.isDisabled(itemId)) {
            return false;
        }
        if (cfg != null && cfg.hasCustomLevels(itemId)) {
            return true;
        }
        return resolveMaxItemLevel(itemId) > 1;
    }

    /** 纯逻辑判定（单测友好）：仅 WZ 成长树才显示 tip。 */
    public static boolean hasGrowthData(int enhance, int itemLevel, boolean hasEnhanceCombat) {
        return hasGrowthData(enhance, itemLevel, hasEnhanceCombat, false);
    }

    /**
     * @param hasWzItemLevelGrowth WZ {@code info/level} 可成长（maxLevel &gt; 1）
     */
    public static boolean hasGrowthData(int enhance, int itemLevel, boolean hasEnhanceCombat,
                                        boolean hasWzItemLevelGrowth) {
        return hasWzItemLevelGrowth;
    }

    public static String buildGrowthText(Equip equip) {
        if (equip == null) {
            return "";
        }
        EquipGrowthConfigManager cfg = EquipGrowthConfigManager.get();
        if (cfg != null && cfg.isDisabled(equip.getItemId())) {
            return "";
        }
        if (cfg != null && cfg.hasCustomLevels(equip.getItemId())) {
            return cfg.buildCustomTipText(equip.getItemId());
        }
        return buildGrowthText(
                equip.getItemId(),
                equip.getEnhance() & 0xFF,
                equip.getItemLevel() & 0xFF,
                equip.getLevel() & 0xFF,
                collectEnhanceCombat(equip),
                resolveMaxItemLevel(equip.getItemId()));
    }

    /** 仅有 itemId（未找到实例）时的成长提示（DB 优先）。 */
    public static String buildWzOnlyGrowthText(int itemId) {
        EquipGrowthConfigManager cfg = EquipGrowthConfigManager.get();
        if (cfg != null && cfg.isDisabled(itemId)) {
            return "";
        }
        if (cfg != null && cfg.hasCustomLevels(itemId)) {
            return cfg.buildCustomTipText(itemId);
        }
        int max = resolveMaxItemLevel(itemId);
        if (max <= 1) {
            return "";
        }
        return buildGrowthText(itemId, 0, 1, 0, Map.of(), max);
    }

    /**
     * 纯逻辑拼 tip（单测友好）。maxItemLevel 未知时传 0。
     */
    public static String buildGrowthText(int itemId, int enhance, int itemLevel, int scrollLevel,
                                         Map<CombatStatType, Integer> combat) {
        return buildGrowthText(itemId, enhance, itemLevel, scrollLevel, combat, resolveMaxItemLevel(itemId));
    }

    /**
     * 仅输出成长树分段：有几级成长就几段「N级效果」+ 该级 Min 属性。
     * Hyper / 砸卷战斗% 不进入成长 tip（喧宾夺主）。
     *
     * @param maxItemLevel WZ 成长上限；&gt;1 才展示
     */
    public static String buildGrowthText(int itemId, int enhance, int itemLevel, int scrollLevel,
                                         Map<CombatStatType, Integer> combat, int maxItemLevel) {
        if (maxItemLevel <= 1) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("装备成长属性\r\n");

        // WZ 节点 N → 「N级效果」（与客户端本地分段一致；验收含 1级效果/2级效果）
        boolean anySegment = false;
        for (int node = 1; node < maxItemLevel; node++) {
            Map<String, Integer> stats = levelNodeStatsMin(itemId, node);
            if (stats.isEmpty()) {
                continue;
            }
            anySegment = true;
            sb.append(node).append("级效果\r\n");
            appendTierStats(sb, stats);
        }
        if (!anySegment) {
            // 有成长树但节点暂不可读时仍给标题，避免客户端空缓存误判
            return sb.toString().trim();
        }
        return sb.toString().trim();
    }

    public static String buildGrowthText(Character chr, int itemId) {
        Equip eq = findEquip(chr, itemId);
        if (eq != null) {
            return buildGrowthText(eq);
        }
        return buildWzOnlyGrowthText(itemId);
    }

    /**
     * 已达成等级带来的成长加成（确定性取 Min；等级 L 的加成来自 WZ info/L-1 节点，
     * 与 {@link Equip#gainLevel} 在升到 L 前读取当前 itemLevel 节点一致）。
     */
    public static Map<String, Integer> sumAchievedLevelupStats(int itemId, int itemLevel) {
        Map<String, Integer> totals = new LinkedHashMap<>();
        if (itemId <= 0 || itemLevel <= 1) {
            return totals;
        }
        for (int lv = 1; lv < itemLevel; lv++) {
            for (Pair<String, Integer> p : getLevelupStatsMin(itemId, lv)) {
                if (p.getRight() != null && p.getRight() != 0) {
                    totals.merge(p.getLeft(), p.getRight(), Integer::sum);
                }
            }
        }
        return totals;
    }

    /** 单级成长节点 Min 属性（tip 分段用）。 */
    public static Map<String, Integer> levelNodeStatsMin(int itemId, int levelNode) {
        Map<String, Integer> out = new LinkedHashMap<>();
        if (itemId <= 0 || levelNode <= 0) {
            return out;
        }
        for (Pair<String, Integer> p : getLevelupStatsMin(itemId, levelNode)) {
            if (p.getRight() != null && p.getRight() != 0) {
                out.merge(p.getLeft(), p.getRight(), Integer::sum);
            }
        }
        return out;
    }

    /** 主 tip 分色用：按常用属性顺序的成长增量（STR..Jump）。DB 自定义优先。 */
    public static int[] growthBonusByStatIndex(int itemId, int itemLevel) {
        int[] out = new int[15];
        Map<String, Integer> gained;
        EquipGrowthConfigManager cfg = EquipGrowthConfigManager.get();
        if (cfg != null && cfg.hasCustomLevels(itemId)) {
            gained = cfg.sumAchievedCustomStats(itemId, itemLevel);
        } else {
            gained = sumAchievedLevelupStats(itemId, itemLevel);
        }
        out[0] = gained.getOrDefault("incSTR", 0);
        out[1] = gained.getOrDefault("incDEX", 0);
        out[2] = gained.getOrDefault("incINT", 0);
        out[3] = gained.getOrDefault("incLUK", 0);
        out[4] = gained.getOrDefault("incMHP", 0);
        out[5] = gained.getOrDefault("incMMP", 0);
        out[6] = gained.getOrDefault("incPAD", 0);
        out[7] = gained.getOrDefault("incMAD", 0);
        out[8] = gained.getOrDefault("incPDD", 0);
        out[9] = gained.getOrDefault("incMDD", 0);
        out[10] = gained.getOrDefault("incACC", 0);
        out[11] = gained.getOrDefault("incEVA", 0);
        out[13] = gained.getOrDefault("incSpeed", 0);
        out[14] = gained.getOrDefault("incJump", 0);
        return out;
    }

    /** 主 tip 火花绿字：STR..Jump（与 tip statIdx / EquipFlame.toTipStats15 对齐）。 */
    public static int[] flameBonusByStatIndex(Equip equip) {
        if (equip == null || equip.getExGradeOption() == 0L) {
            return null;
        }
        org.gms.flame.EquipFlame fl = equip.getFlameStat();
        if (fl == null) {
            org.gms.flame.FlameService.decodeToFlameStat(equip);
            fl = equip.getFlameStat();
        }
        if (fl == null) {
            return null;
        }
        return fl.toTipStats15();
    }

    /** 供其它系统复用：汇总砸卷强化规则战斗%（不再写入成长 tip）。 */
    public static Map<CombatStatType, Integer> collectEnhanceCombat(Equip equip) {
        Map<CombatStatType, Integer> totals = new EnumMap<>(CombatStatType.class);
        if (equip == null) {
            return totals;
        }
        int level = equip.getLevel() & 0xFF;
        if (level <= 0) {
            return totals;
        }
        CombatSourceManager.loadOrSeed();
        String type = classifyEquip(equip.getItemId());
        for (EquipEnhanceRuleDO rule : CombatSourceManager.listEnhanceRules()) {
            if (rule.getEnabled() != null && rule.getEnabled() == 0) {
                continue;
            }
            int min = rule.getMinLevel() == null ? 0 : rule.getMinLevel();
            int max = rule.getMaxLevel() == null ? 99 : rule.getMaxLevel();
            if (level < min || level > max) {
                continue;
            }
            if (!matchType(rule.getEquipType(), type)) {
                continue;
            }
            List<CombatStatModifier> mods = CombatStatJson.fromEnhanceStatsJson(
                    rule.getStatsJson(), level, CombatStatSource.ENHANCE,
                    "enhance:" + rule.getId() + ":" + equip.getItemId());
            for (CombatStatModifier mod : mods) {
                totals.merge(mod.type(), mod.value(), Integer::sum);
            }
        }
        return totals;
    }

    /** 供单测：不依赖 DB 的强化规则战斗% 汇总。 */
    public static Map<CombatStatType, Integer> aggregateEnhanceMods(List<CombatStatModifier> mods) {
        Map<CombatStatType, Integer> totals = new EnumMap<>(CombatStatType.class);
        if (mods == null) {
            return totals;
        }
        for (CombatStatModifier mod : mods) {
            if (mod == null || mod.value() == 0) {
                continue;
            }
            totals.merge(mod.type(), mod.value(), Integer::sum);
        }
        return totals;
    }

    public static int resolveMaxItemLevel(int itemId) {
        if (itemId <= 0) {
            return 0;
        }
        int wz = 0;
        try {
            wz = ItemInformationProvider.getInstance().getEquipLevel(itemId, true);
        } catch (Throwable ignored) {
            wz = 0;
        }
        EquipGrowthConfigManager cfg = EquipGrowthConfigManager.get();
        if (cfg != null) {
            return cfg.resolveMaxLevel(itemId, wz);
        }
        return wz;
    }

    private static void appendStatLine(StringBuilder sb, String label, int value) {
        if (value != 0) {
            sb.append(label).append(COLON).append("+").append(value).append("\r\n");
        }
    }

    /** 对齐套装 tip：攻魔/血魔相等合并；顺序 STR→Jump。 */
    private static void appendTierStats(StringBuilder sb, Map<String, Integer> stats) {
        int str = stats.getOrDefault("incSTR", 0);
        int dex = stats.getOrDefault("incDEX", 0);
        int intel = stats.getOrDefault("incINT", 0);
        int luk = stats.getOrDefault("incLUK", 0);
        int pad = stats.getOrDefault("incPAD", 0);
        int mad = stats.getOrDefault("incMAD", 0);
        int mhp = stats.getOrDefault("incMHP", 0);
        int mmp = stats.getOrDefault("incMMP", 0);

        if (MERGE_EQUAL && str != 0 && str == dex && str == intel && str == luk) {
            appendStatLine(sb, "所有属性", str);
        } else {
            appendStatLine(sb, "力量", str);
            appendStatLine(sb, "敏捷", dex);
            appendStatLine(sb, "智力", intel);
            appendStatLine(sb, "运气", luk);
        }
        if (MERGE_EQUAL && pad != 0 && pad == mad) {
            appendStatLine(sb, "攻击力/魔力", pad);
        } else {
            appendStatLine(sb, "攻击力", pad);
            appendStatLine(sb, "魔法力", mad);
        }
        if (MERGE_EQUAL && mhp != 0 && mhp == mmp) {
            appendStatLine(sb, "最大血量/最大魔量", mhp);
        } else {
            appendStatLine(sb, "最大血量", mhp);
            appendStatLine(sb, "最大魔量", mmp);
        }
        appendStatLine(sb, "防御力", stats.getOrDefault("incPDD", 0));
        appendStatLine(sb, "魔法防御", stats.getOrDefault("incMDD", 0));
        appendStatLine(sb, "命中", stats.getOrDefault("incACC", 0));
        appendStatLine(sb, "回避", stats.getOrDefault("incEVA", 0));
        appendStatLine(sb, "移动速度", stats.getOrDefault("incSpeed", 0));
        appendStatLine(sb, "跳跃", stats.getOrDefault("incJump", 0));
    }

    /** 确定性读取某成长节点的 Min 属性（不掷随机）。 */
    private static List<Pair<String, Integer>> getLevelupStatsMin(int itemId, int levelNode) {
        try {
            return ItemInformationProvider.getInstance().getItemLevelupStatsMin(itemId, levelNode);
        } catch (Throwable ignored) {
            return List.of();
        }
    }

    private static int growthScore(Equip eq) {
        return (eq.getEnhance() & 0xFF) * 1000
                + (eq.getItemLevel() & 0xFF) * 10
                + (eq.getLevel() & 0xFF);
    }

    private static boolean matchType(String ruleType, String equipType) {
        if (ruleType == null || ruleType.isBlank() || "ALL".equalsIgnoreCase(ruleType)) {
            return true;
        }
        return ruleType.equalsIgnoreCase(equipType);
    }

    private static String classifyEquip(int itemId) {
        if (ItemConstants.isWeapon(itemId)) {
            return "WEAPON";
        }
        int prefix = itemId / 10000;
        if (prefix == 111 || prefix == 112 || prefix == 113 || prefix == 114 || prefix == 115 || prefix == 103) {
            return "ACCESSORY";
        }
        return "ARMOR";
    }
}
