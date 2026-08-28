package org.gms.combat.stat;

import com.alibaba.fastjson2.JSONObject;
import org.gms.combat.stat.CombatStatType.StackRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** 从 stats_json / combatStats 节点解析 CombatStatModifier。 */
public final class CombatStatJson {
    private CombatStatJson() {}

    public static void appendCombatStats(List<CombatStatModifier> out, JSONObject combatStats,
                                         CombatStatSource source, String sourceId) {
        if (combatStats == null || combatStats.isEmpty()) {
            return;
        }
        for (Map.Entry<String, Object> e : combatStats.entrySet()) {
            CombatStatType type = CombatStatType.fromKey(e.getKey());
            if (type == null || e.getValue() == null) {
                continue;
            }
            int v = toInt(e.getValue());
            if (v == 0) {
                continue;
            }
            out.add(new CombatStatModifier(type, v, source, sourceId));
        }
    }

    public static List<CombatStatModifier> fromEnhanceStatsJson(String statsJson, int level,
                                                                CombatStatSource source, String sourceId) {
        List<CombatStatModifier> out = new ArrayList<>();
        if (statsJson == null || statsJson.isBlank() || level <= 0) {
            return out;
        }
        JSONObject root = JSONObject.parseObject(statsJson);
        if (root == null) {
            return out;
        }
        JSONObject perLevel = root.getJSONObject("perLevel");
        if (perLevel != null) {
            for (Map.Entry<String, Object> e : perLevel.entrySet()) {
                CombatStatType type = CombatStatType.fromKey(e.getKey());
                if (type == null) {
                    continue;
                }
                int per = toInt(e.getValue());
                if (per == 0) {
                    continue;
                }
                int total = per * level;
                if (type.getStackRule() == StackRule.MULTIPLICATIVE) {
                    // 每级 fdR：拆成 level 条乘法来源太重，按合计一次乘入
                    out.add(new CombatStatModifier(type, total, source, sourceId + ":perLevel"));
                } else {
                    out.add(new CombatStatModifier(type, total, source, sourceId + ":perLevel"));
                }
            }
        }
        JSONObject milestones = root.getJSONObject("milestones");
        if (milestones != null) {
            for (String key : milestones.keySet()) {
                int milestone = toInt(key);
                if (milestone <= 0 || level < milestone) {
                    continue;
                }
                JSONObject block = milestones.getJSONObject(key);
                appendCombatStats(out, block, source, sourceId + ":ms" + milestone);
            }
        }
        // 兼容直接写 combatStats 的扁平结构
        JSONObject combat = root.getJSONObject("combatStats");
        if (combat != null) {
            appendCombatStats(out, combat, source, sourceId);
        } else if (perLevel == null && milestones == null) {
            appendCombatStats(out, root, source, sourceId);
        }
        return out;
    }

    public static List<CombatStatModifier> fromCarryStatsJson(String statsJson,
                                                              CombatStatSource source, String sourceId) {
        List<CombatStatModifier> out = new ArrayList<>();
        if (statsJson == null || statsJson.isBlank()) {
            return out;
        }
        JSONObject root = JSONObject.parseObject(statsJson);
        if (root == null) {
            return out;
        }
        JSONObject combat = root.getJSONObject("combatStats");
        appendCombatStats(out, combat != null ? combat : root, source, sourceId);
        return out;
    }

    private static int toInt(Object v) {
        if (v instanceof Number n) {
            return n.intValue();
        }
        try {
            return Integer.parseInt(String.valueOf(v));
        } catch (Exception e) {
            return 0;
        }
    }
}
