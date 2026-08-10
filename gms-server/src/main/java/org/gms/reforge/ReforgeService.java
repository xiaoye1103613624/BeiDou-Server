package org.gms.reforge;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.client.inventory.Equip;
import org.gms.dao.entity.ReforgeAffixDO;
import org.gms.dao.mapper.ReforgeAffixMapper;
import org.gms.util.Randomizer;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 洗炼（Reforge）系统服务。
 * 给装备附加1~3条随机词条(affix)，每条词条有前缀等级①~⑤，
 * 属性增量 = ①级基值 × prefixLv。
 * 血/防词条仅①级（不升至②~⑤）。
 * <p>
 * 编码规则：reforge字段 = (affixOrdinal << 16) | (prefixLv & 0xFFFF)
 * affixOrdinal是按配置表顺序的序号(0-based)，prefixLv范围1~5。
 */
@Slf4j
@Service
@AllArgsConstructor
public class ReforgeService {

    private final ReforgeAffixMapper affixMapper;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private static volatile List<ReforgeAffixDO> AFFIX_CACHE = List.of();

    @PostConstruct
    void warmAffixCache() {
        try {
            listEnabledAffixes();
        } catch (Exception e) {
            log.warn("Reforge affix cache warm failed: {}", e.toString());
        }
    }

    /** 最大词条数 */
    public static final int MAX_LINES = 3;
    /** 前缀等级范围 */
    public static final int PREFIX_MIN = 1;
    public static final int PREFIX_MAX = 5;
    /** 前缀等级权重（①概率最高，⑤概率最低） */
    public static final int[] PREFIX_WEIGHTS = {0, 35, 25, 20, 12, 8};

    // ==================== 静态API（供JS脚本调用） ====================

    /**
     * 从装备字段解码词条列表。
     * @return [{affixCode, prefixLv, displayName}, ...] 空位返回null
     */
    public static List<Map<String, Object>> decodeLines(Equip equip, List<ReforgeAffixDO> affixes) {
        List<Map<String, Object>> result = new ArrayList<>();
        int[] raw = {equip.getReforge1(), equip.getReforge2(), equip.getReforge3()};
        for (int i = 0; i < MAX_LINES; i++) {
            if (raw[i] <= 0) {
                result.add(null);
                continue;
            }
            int ordinal = (raw[i] >> 16) & 0xFFFF;
            int prefixLv = raw[i] & 0xFFFF;
            if (ordinal < 0 || ordinal >= affixes.size()) {
                result.add(null);
                continue;
            }
            ReforgeAffixDO affix = affixes.get(ordinal);
            Map<String, Object> line = new LinkedHashMap<>();
            line.put("slot", i);
            line.put("affixCode", affix.getCode());
            line.put("nameZh", affix.getNameZh());
            line.put("prefixLv", prefixLv);
            line.put("displayName", circleNum(prefixLv) + affix.getNameZh());
            line.put("locked", isLineLocked(equip, i));
            // 计算属性
            line.put("stats", computeStats(affix, prefixLv));
            result.add(line);
        }
        return result;
    }

    /**
     * 给装备进行首次洗炼（全随机1~3条）。
     * 调用前需确认装备无洗炼数据。
     */
    public static void rollFirstTime(Equip equip, List<ReforgeAffixDO> affixes) {
        int lineCount = rollLineCount();
        List<Integer> selectedOrdinals = new ArrayList<>();
        List<Integer> selectedPrefixes = new ArrayList<>();
        for (int i = 0; i < lineCount; i++) {
            int ordinal = pickAffixOrdinal(affixes);
            ReforgeAffixDO affix = affixes.get(ordinal);
            int prefix = rollPrefix(affix.getMaxPrefix());
            selectedOrdinals.add(ordinal);
            selectedPrefixes.add(prefix);
        }
        applyLines(equip, selectedOrdinals, selectedPrefixes);
    }

    /**
     * 整类重洗（保留锁定的行）。
     */
    public static void rerollAll(Equip equip, List<ReforgeAffixDO> affixes) {
        List<Integer> ordinals = new ArrayList<>();
        List<Integer> prefixes = new ArrayList<>();
        int[] raw = {equip.getReforge1(), equip.getReforge2(), equip.getReforge3()};
        for (int i = 0; i < MAX_LINES; i++) {
            if (isLineLocked(equip, i) && raw[i] > 0) {
                // 锁定行保留
                ordinals.add((raw[i] >> 16) & 0xFFFF);
                prefixes.add(raw[i] & 0xFFFF);
            } else {
                int ordinal = pickAffixOrdinal(affixes);
                ReforgeAffixDO affix = affixes.get(ordinal);
                int prefix = rollPrefix(affix.getMaxPrefix());
                ordinals.add(ordinal);
                prefixes.add(prefix);
            }
        }
        applyLines(equip, ordinals, prefixes);
    }

    /**
     * 单行重洗（替换指定slot的词条）。
     */
    public static void rerollLine(Equip equip, int slot, List<ReforgeAffixDO> affixes) {
        if (slot < 0 || slot >= MAX_LINES) return;
        if (isLineLocked(equip, slot)) return; // 锁定行不可重洗

        int ordinal = pickAffixOrdinal(affixes);
        ReforgeAffixDO affix = affixes.get(ordinal);
        int prefix = rollPrefix(affix.getMaxPrefix());
        int encoded = (ordinal << 16) | (prefix & 0xFFFF);

        switch (slot) {
            case 0 -> equip.setReforge1(encoded);
            case 1 -> equip.setReforge2(encoded);
            case 2 -> equip.setReforge3(encoded);
        }
    }

    /**
     * 锁定/解锁指定行。
     */
    public static void setLineLock(Equip equip, int slot, boolean locked) {
        int mask = equip.getReforgeLock() & 0xFF;
        if (locked) {
            mask |= (1 << slot);
        } else {
            mask &= ~(1 << slot);
        }
        equip.setReforgeLock((byte) mask);
    }

    /** 检查行是否锁定 */
    public static boolean isLineLocked(Equip equip, int slot) {
        return ((equip.getReforgeLock() & 0xFF) & (1 << slot)) != 0;
    }

    /** 清空洗炼数据 */
    public static void clear(Equip equip) {
        equip.setReforge1(0);
        equip.setReforge2(0);
        equip.setReforge3(0);
        equip.setReforgeLock((byte) 0);
    }

    /** 计算词条属性（base × prefix） */
    public static Map<String, Integer> computeStats(ReforgeAffixDO affix, int prefixLv) {
        Map<String, Integer> stats = new LinkedHashMap<>();
        try {
            Map<String, Integer> base = objectMapper.readValue(affix.getBaseJson(),
                    new TypeReference<Map<String, Integer>>() {});
            int effectivePrefix = Math.max(1, Math.min(affix.getMaxPrefix(), prefixLv));
            for (Map.Entry<String, Integer> e : base.entrySet()) {
                String key = e.getKey();
                int val = e.getValue() * effectivePrefix;
                // ALLSTAT展开为四维
                if ("ALLSTAT".equals(key)) {
                    stats.merge("STR", val, Integer::sum);
                    stats.merge("DEX", val, Integer::sum);
                    stats.merge("INT", val, Integer::sum);
                    stats.merge("LUK", val, Integer::sum);
                } else {
                    stats.merge(key, val, Integer::sum);
                }
            }
        } catch (Exception e) {
            log.error("Failed to compute stats for affix {}", affix.getCode(), e);
        }
        return stats;
    }

    /** 汇总装备所有洗炼词条的总属性 */
    public static Map<String, Integer> computeTotalStats(Equip equip, List<ReforgeAffixDO> affixes) {
        Map<String, Integer> total = new LinkedHashMap<>();
        List<Map<String, Object>> lines = decodeLines(equip, affixes);
        for (Map<String, Object> line : lines) {
            if (line == null) continue;
            @SuppressWarnings("unchecked")
            Map<String, Integer> stats = (Map<String, Integer>) line.get("stats");
            if (stats != null) {
                for (Map.Entry<String, Integer> e : stats.entrySet()) {
                    total.merge(e.getKey(), e.getValue(), Integer::sum);
                }
            }
        }
        return total;
    }

    /** 格式化展示（用于tip/dropMessage） */
    public static String describe(Equip equip, List<ReforgeAffixDO> affixes) {
        List<Map<String, Object>> lines = decodeLines(equip, affixes);
        StringBuilder sb = new StringBuilder();
        boolean hasAny = false;
        for (Map<String, Object> line : lines) {
            if (line == null) continue;
            hasAny = true;
            if (sb.length() > 0) sb.append(" ");
            sb.append(line.get("displayName"));
            if (Boolean.TRUE.equals(line.get("locked"))) sb.append("🔒");
        }
        if (!hasAny) return "";
        // 附总属性摘要
        Map<String, Integer> total = computeTotalStats(equip, affixes);
        sb.append(" (");
        List<String> parts = new ArrayList<>();
        for (Map.Entry<String, Integer> e : total.entrySet()) {
            parts.add(e.getKey() + "+" + e.getValue());
        }
        sb.append(String.join(", ", parts));
        sb.append(")");
        return sb.toString();
    }

    // ==================== 内部工具方法 ====================

    private static void applyLines(Equip equip, List<Integer> ordinals, List<Integer> prefixes) {
        equip.setReforge1(encodeLine(ordinals, prefixes, 0));
        equip.setReforge2(encodeLine(ordinals, prefixes, 1));
        equip.setReforge3(encodeLine(ordinals, prefixes, 2));
    }

    private static int encodeLine(List<Integer> ordinals, List<Integer> prefixes, int idx) {
        if (idx >= ordinals.size()) return 0;
        return (ordinals.get(idx) << 16) | (prefixes.get(idx) & 0xFFFF);
    }

    private static int rollLineCount() {
        // 75% 2条, 15% 3条, 10% 1条
        int r = Randomizer.nextInt(100);
        if (r < 10) return 1;
        if (r < 85) return 2;
        return 3;
    }

    private static int pickAffixOrdinal(List<ReforgeAffixDO> affixes) {
        // 按权重加权抽取
        List<ReforgeAffixDO> enabled = affixes.stream()
                .filter(a -> a.getEnabled() != null && a.getEnabled() == 1)
                .collect(Collectors.toList());
        if (enabled.isEmpty()) return 0;

        int totalWeight = enabled.stream().mapToInt(a -> Math.max(1, a.getWeight() != null ? a.getWeight() : 10)).sum();
        int roll = Randomizer.nextInt(totalWeight);
        int cum = 0;
        for (int i = 0; i < enabled.size(); i++) {
            cum += Math.max(1, enabled.get(i).getWeight() != null ? enabled.get(i).getWeight() : 10);
            if (roll < cum) return affixes.indexOf(enabled.get(i));
        }
        return 0;
    }

    private static int rollPrefix(int maxPrefix) {
        int effectiveMax = Math.max(1, Math.min(PREFIX_MAX, maxPrefix));
        int total = 0;
        for (int i = 1; i <= effectiveMax; i++) total += PREFIX_WEIGHTS[i];
        int roll = Randomizer.nextInt(total);
        int cum = 0;
        for (int i = 1; i <= effectiveMax; i++) {
            cum += PREFIX_WEIGHTS[i];
            if (roll < cum) return i;
        }
        return 1;
    }

    private static String circleNum(int n) {
        return switch (n) {
            case 1 -> "①";
            case 2 -> "②";
            case 3 -> "③";
            case 4 -> "④";
            case 5 -> "⑤";
            default -> "①";
        };
    }

    // ==================== DB查询（供初始化/缓存） ====================

    /** 加载所有启用的词条配置（用于缓存，避免每次脚本调用都查DB） */
    public List<ReforgeAffixDO> listEnabledAffixes() {
        List<ReforgeAffixDO> list = affixMapper.selectListByQuery(
                QueryWrapper.create().where("enabled = 1").orderBy("code", true));
        AFFIX_CACHE = List.copyOf(list);
        return list;
    }

    /** 战斗/tip 无 Spring 注入时使用；未加载则空列表（洗炼不加战斗）。 */
    public static List<ReforgeAffixDO> cachedAffixes() {
        return AFFIX_CACHE;
    }
}
