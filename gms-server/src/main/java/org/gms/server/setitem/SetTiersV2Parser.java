package org.gms.server.setitem;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.gms.combat.stat.CombatStatType;
import org.gms.provider.Data;
import org.gms.provider.DataTool;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SetTiersV2Parser {
    private static final Pattern DAMAGE_PATTERN = Pattern.compile("(\\d+)");

    private SetTiersV2Parser() {}

    public static void applyTiersJson(SetDefinition def, String tiersJson) {
        if (tiersJson == null || tiersJson.isBlank()) {
            return;
        }
        String trimmed = tiersJson.trim();
        if (trimmed.startsWith("{")) {
            JSONObject root = JSONObject.parseObject(trimmed);
            if (root == null) {
                return;
            }
            JSONArray tiers = root.getJSONArray("tiers");
            if (tiers != null) {
                parseTierArray(def, tiers);
            }
            return;
        }
        JSONArray arr = JSONArray.parseArray(trimmed);
        parseTierArray(def, arr);
    }

    private static void parseTierArray(SetDefinition def, JSONArray arr) {
        if (arr == null) {
            return;
        }
        for (int i = 0; i < arr.size(); i++) {
            JSONObject tier = arr.getJSONObject(i);
            if (tier == null) {
                continue;
            }
            int count = tier.getIntValue("count");
            if (count <= 0) {
                continue;
            }
            SetBonus bonus = parseTierObject(tier);
            bonus.requiredCount = count;
            def.tiers.put(count, bonus);
        }
    }

    public static SetBonus parseTierObject(JSONObject tier) {
        SetBonus bonus = new SetBonus();
        if (tier.containsKey("enabled")) {
            bonus.tierEnabled = tier.getBooleanValue("enabled");
        }

        JSONObject stats = tier.getJSONObject("stats");
        if (stats != null) {
            bonus.str = stats.getIntValue("str");
            bonus.dex = stats.getIntValue("dex");
            bonus.int_ = stats.getIntValue("int");
            bonus.luk = stats.getIntValue("luk");
            bonus.pad = stats.getIntValue("pad");
            bonus.mad = stats.getIntValue("mad");
            bonus.pdd = stats.getIntValue("pdd");
            bonus.mdd = stats.getIntValue("mdd");
            bonus.acc = stats.getIntValue("acc");
            bonus.eva = stats.getIntValue("eva");
            bonus.mhp = stats.getIntValue("mhp");
            bonus.mmp = stats.getIntValue("mmp");
            bonus.speed = stats.getIntValue("speed");
            bonus.jump = stats.getIntValue("jump");
            int allStat = stats.getIntValue("allStat");
            if (allStat > 0) {
                bonus.str += allStat;
                bonus.dex += allStat;
                bonus.int_ += allStat;
                bonus.luk += allStat;
            }
        } else {
            bonus.str = tier.getIntValue("str");
            bonus.dex = tier.getIntValue("dex");
            bonus.int_ = tier.getIntValue("int");
            bonus.luk = tier.getIntValue("luk");
            bonus.pad = tier.getIntValue("pad");
            bonus.mad = tier.getIntValue("mad");
            bonus.mhp = tier.getIntValue("mhp");
            bonus.mmp = tier.getIntValue("mmp");
            int allStat = tier.getIntValue("allStat");
            if (allStat > 0) {
                bonus.str += allStat;
                bonus.dex += allStat;
                bonus.int_ += allStat;
                bonus.luk += allStat;
            }
        }

        JSONObject statsPercent = tier.getJSONObject("statsPercent");
        if (statsPercent != null) {
            bonus.strR = statsPercent.getIntValue("strR");
            bonus.dexR = statsPercent.getIntValue("dexR");
            bonus.intR = statsPercent.getIntValue("intR");
            bonus.lukR = statsPercent.getIntValue("lukR");
            bonus.mhpR = statsPercent.getIntValue("mhpR");
            bonus.mmpR = statsPercent.getIntValue("mmpR");
        }

        bonus.finalDamagePercent = tier.getIntValue("finalDamage");
        bonus.damageSkinId = tier.getIntValue("damageSkin");

        JSONObject combat = tier.getJSONObject("combatStats");
        if (combat != null) {
            for (String key : combat.keySet()) {
                int val = combat.getIntValue(key);
                if (val != 0) {
                    bonus.putCombatStat(key, val);
                }
            }
        }

        JSONArray skills = tier.getJSONArray("skills");
        if (skills == null) {
            skills = tier.getJSONArray("skillLevels");
        }
        if (skills != null) {
            for (int j = 0; j < skills.size(); j++) {
                JSONObject sk = skills.getJSONObject(j);
                if (sk == null) {
                    continue;
                }
                int id = sk.getIntValue("id");
                if (id == 0) {
                    id = sk.getIntValue("skillId");
                }
                int level = sk.getIntValue("level");
                if (id > 0 && level > 0) {
                    bonus.skillLevels.put(id, level);
                }
            }
        }

        JSONArray active = tier.getJSONArray("activeSkills");
        if (active != null) {
            for (int j = 0; j < active.size(); j++) {
                JSONObject sk = active.getJSONObject(j);
                if (sk == null) {
                    continue;
                }
                int id = sk.getIntValue("id");
                if (id == 0) {
                    id = sk.getIntValue("skillId");
                }
                int level = sk.getIntValue("level");
                if (id > 0 && level > 0) {
                    bonus.activeSkills.put(id, level);
                }
            }
        }

        JSONArray skillMods = tier.getJSONArray("skillMods");
        if (skillMods != null) {
            for (int j = 0; j < skillMods.size(); j++) {
                JSONObject mod = skillMods.getJSONObject(j);
                if (mod == null) {
                    continue;
                }
                int skillId = mod.getIntValue("skillId");
                if (skillId <= 0) {
                    continue;
                }
                int addAttack = mod.getIntValue("addAttackCount");
                int addLevel = mod.getIntValue("addLevel");
                String type = mod.getString("type");
                if (addAttack > 0 || "attackCount".equals(type)) {
                    bonus.skillMods.add(SetSkillMod.attackCount(skillId, addAttack));
                } else if (addLevel > 0) {
                    bonus.skillMods.add(SetSkillMod.level(skillId, addLevel));
                }
            }
        }
        return bonus;
    }

    public static SetBonus parseEffectTier(Data tierNode) {
        SetBonus bonus = new SetBonus();
        bonus.str = readStat(tierNode, "incSTR");
        bonus.dex = readStat(tierNode, "incDEX");
        bonus.int_ = readStat(tierNode, "incINT");
        bonus.luk = readStat(tierNode, "incLUK");
        bonus.pad = readStat(tierNode, "incPAD");
        bonus.mad = readStat(tierNode, "incMAD");
        bonus.pdd = readStat(tierNode, "incPDD");
        bonus.mdd = readStat(tierNode, "incMDD");
        bonus.acc = readStat(tierNode, "incACC");
        bonus.eva = readStat(tierNode, "incEVA");
        bonus.mhp = readStat(tierNode, "incMHP");
        bonus.mmp = readStat(tierNode, "incMMP");
        bonus.speed = readStat(tierNode, "incSpeed");
        bonus.jump = readStat(tierNode, "incJump");
        bonus.strR = readStat(tierNode, "incSTRr");
        bonus.dexR = readStat(tierNode, "incDEXr");
        bonus.intR = readStat(tierNode, "incINTr");
        bonus.lukR = readStat(tierNode, "incLUKr");
        bonus.mhpR = readStat(tierNode, "incMHPr");
        bonus.mmpR = readStat(tierNode, "incMMPr");

        int allStat = readStat(tierNode, "incAllStat");
        if (allStat > 0) {
            bonus.str += allStat;
            bonus.dex += allStat;
            bonus.int_ += allStat;
            bonus.luk += allStat;
        }

        bonus.finalDamagePercent = parseIncDamage(tierNode);
        putCombatIfPositive(bonus, CombatStatType.DAM_R, readStat(tierNode, "damR"));
        putCombatIfPositive(bonus, CombatStatType.BOSS_DAM_R, readStat(tierNode, "bdR"));
        // nbdR = 对普通怪伤害%，绝不可并入 bdR
        putCombatIfPositive(bonus, CombatStatType.NORMAL_DAM_R, readStat(tierNode, "nbdR"));
        putCombatIfPositive(bonus, CombatStatType.IGNORE_PDR, firstPositive(
                readStat(tierNode, "ignoreMobpdpR"), readStat(tierNode, "ignoreTargetDEF")));
        putCombatIfPositive(bonus, CombatStatType.IGNORE_MDR, readStat(tierNode, "ignoreMobmdR"));
        int fdR = readStat(tierNode, "fdR");
        if (fdR > 0) {
            bonus.putCombatStat(CombatStatType.FINAL_DAM_R.getKey(), fdR);
        }
        putCombatIfPositive(bonus, CombatStatType.CRIT_RATE, firstPositive(
                readStat(tierNode, "cr"), readStat(tierNode, "incCr")));
        putCombatIfPositive(bonus, CombatStatType.CRIT_DAM, readStat(tierNode, "cd"));
        bonus.damageSkinId = readStat(tierNode, "damageSkin");

        applyEffectOptions(bonus, tierNode);
        return bonus;
    }

    /** Effect/Option/{n}: level + option → ItemOption 属性；高版本缺失时用已知回退。 */
    private static void applyEffectOptions(SetBonus bonus, Data tierNode) {
        Data optionRoot = tierNode.getChildByPath("Option");
        if (optionRoot == null) {
            return;
        }
        for (Data entry : optionRoot.getChildren()) {
            int optionId = DataTool.getInt("option", entry, 0);
            int level = DataTool.getInt("level", entry, 0);
            if (optionId <= 0) {
                continue;
            }
            Map<String, Integer> stats = org.gms.potential.ItemOptionProvider.getInstance()
                    .getStats(optionId, level);
            if (stats == null || stats.isEmpty()) {
                applyKnownOptionFallback(bonus, optionId, level);
                continue;
            }
            applyOptionStatMap(bonus, stats);
        }
    }

    private static void applyOptionStatMap(SetBonus bonus, Map<String, Integer> stats) {
        for (Map.Entry<String, Integer> e : stats.entrySet()) {
            int v = e.getValue() == null ? 0 : e.getValue();
            if (v == 0) {
                continue;
            }
            switch (e.getKey()) {
                case "incSTR" -> bonus.str += v;
                case "incDEX" -> bonus.dex += v;
                case "incINT" -> bonus.int_ += v;
                case "incLUK" -> bonus.luk += v;
                case "incPAD" -> bonus.pad += v;
                case "incMAD" -> bonus.mad += v;
                case "incPDD" -> bonus.pdd += v;
                case "incMDD" -> bonus.mdd += v;
                case "incACC" -> bonus.acc += v;
                case "incEVA" -> bonus.eva += v;
                case "incMHP" -> bonus.mhp += v;
                case "incMMP" -> bonus.mmp += v;
                case "incSpeed" -> bonus.speed += v;
                case "incJump" -> bonus.jump += v;
                case "incSTRr" -> bonus.strR += v;
                case "incDEXr" -> bonus.dexR += v;
                case "incINTr" -> bonus.intR += v;
                case "incLUKr" -> bonus.lukR += v;
                case "incMHPr" -> bonus.mhpR += v;
                case "incMMPr" -> bonus.mmpR += v;
                case "incAllStat" -> {
                    bonus.str += v;
                    bonus.dex += v;
                    bonus.int_ += v;
                    bonus.luk += v;
                }
                case "bdR", "boss" -> putCombatIfPositive(bonus, CombatStatType.BOSS_DAM_R, v);
                case "nbdR" -> putCombatIfPositive(bonus, CombatStatType.NORMAL_DAM_R, v);
                case "damR", "incDAMr" -> putCombatIfPositive(bonus, CombatStatType.DAM_R, v);
                case "fdR", "incDamage" -> bonus.putCombatStat(CombatStatType.FINAL_DAM_R.getKey(), v);
                case "ignoreMobpdpR", "ignoreTargetDEF" ->
                        putCombatIfPositive(bonus, CombatStatType.IGNORE_PDR, v);
                case "ignoreMobmdR" -> putCombatIfPositive(bonus, CombatStatType.IGNORE_MDR, v);
                case "cr", "incCr" -> putCombatIfPositive(bonus, CombatStatType.CRIT_RATE, v);
                case "cd" -> putCombatIfPositive(bonus, CombatStatType.CRIT_DAM, v);
                default -> {
                    // 未知键若已是 CombatStatType key 则直接写入
                    if (CombatStatType.fromKey(e.getKey()) != null) {
                        bonus.putCombatStat(e.getKey(), v);
                    }
                }
            }
        }
    }

    /**
     * 083 ItemOption.img 无高版本套装 Option（如创世 60024）时的显示/战斗回退。
     * 60024@任意档：攻击首领怪物时的伤害 +10%（无尽辉耀 2/3/4 件）。
     */
    private static void applyKnownOptionFallback(SetBonus bonus, int optionId, int level) {
        if (optionId == 60024) {
            putCombatIfPositive(bonus, CombatStatType.BOSS_DAM_R, 10);
        }
        // 60087：资料站 5 件档仅显示攻魔，暂无明确战斗键，缺 WZ 时不臆造
    }

    private static void putCombatIfPositive(SetBonus bonus, CombatStatType type, int value) {
        if (value > 0) {
            bonus.putCombatStat(type.getKey(), value);
        }
    }

    private static int firstPositive(int a, int b) {
        return a > 0 ? a : b;
    }

    private static int readStat(Data tierNode, String path) {
        try {
            return DataTool.getInt(path, tierNode, 0);
        } catch (Exception e) {
            return 0;
        }
    }

    private static int parseIncDamage(Data tierNode) {
        Data node = tierNode.getChildByPath("incDamage");
        if (node == null || node.getData() == null) {
            return 0;
        }
        try {
            Object val = node.getData();
            if (val instanceof Number) {
                return ((Number) val).intValue();
            }
            if (val instanceof String) {
                Matcher m = DAMAGE_PATTERN.matcher((String) val);
                if (m.find()) {
                    return Integer.parseInt(m.group(1));
                }
            }
        } catch (Exception ignored) {
        }
        return 0;
    }

    public static String toTiersJson(SetDefinition def) {
        JSONObject root = new JSONObject();
        root.put("schemaVersion", 2);
        JSONArray tiers = new JSONArray();
        List<Integer> counts = new ArrayList<>(def.tiers.keySet());
        counts.sort(Comparator.naturalOrder());
        for (int count : counts) {
            SetBonus b = def.tiers.get(count);
            if (b == null) {
                continue;
            }
            JSONObject tier = new JSONObject();
            tier.put("count", count);
            tier.put("enabled", b.tierEnabled);
            JSONObject stats = new JSONObject();
            putIfNonZero(stats, "str", b.str);
            putIfNonZero(stats, "dex", b.dex);
            putIfNonZero(stats, "int", b.int_);
            putIfNonZero(stats, "luk", b.luk);
            putIfNonZero(stats, "pad", b.pad);
            putIfNonZero(stats, "mad", b.mad);
            putIfNonZero(stats, "pdd", b.pdd);
            putIfNonZero(stats, "mdd", b.mdd);
            putIfNonZero(stats, "acc", b.acc);
            putIfNonZero(stats, "eva", b.eva);
            putIfNonZero(stats, "mhp", b.mhp);
            putIfNonZero(stats, "mmp", b.mmp);
            putIfNonZero(stats, "speed", b.speed);
            putIfNonZero(stats, "jump", b.jump);
            if (!stats.isEmpty()) {
                tier.put("stats", stats);
            }
            JSONObject pct = new JSONObject();
            putIfNonZero(pct, "strR", b.strR);
            putIfNonZero(pct, "dexR", b.dexR);
            putIfNonZero(pct, "intR", b.intR);
            putIfNonZero(pct, "lukR", b.lukR);
            putIfNonZero(pct, "mhpR", b.mhpR);
            putIfNonZero(pct, "mmpR", b.mmpR);
            if (!pct.isEmpty()) {
                tier.put("statsPercent", pct);
            }
            if (!b.combatStats.isEmpty() || b.finalDamagePercent != 0) {
                JSONObject combat = new JSONObject();
                for (Map.Entry<String, Integer> e : b.combatStats.entrySet()) {
                    combat.put(e.getKey(), e.getValue());
                }
                if (b.finalDamagePercent != 0 && !combat.containsKey(CombatStatType.FINAL_DAM_R.getKey())) {
                    combat.put(CombatStatType.FINAL_DAM_R.getKey(), b.finalDamagePercent);
                }
                tier.put("combatStats", combat);
            }
            if (b.damageSkinId != 0) {
                tier.put("damageSkin", b.damageSkinId);
            }
            if (!b.skillLevels.isEmpty()) {
                JSONArray skills = new JSONArray();
                for (Map.Entry<Integer, Integer> e : b.skillLevels.entrySet()) {
                    JSONObject sk = new JSONObject();
                    sk.put("id", e.getKey());
                    sk.put("level", e.getValue());
                    skills.add(sk);
                }
                tier.put("skills", skills);
            }
            if (!b.activeSkills.isEmpty()) {
                JSONArray active = new JSONArray();
                for (Map.Entry<Integer, Integer> e : b.activeSkills.entrySet()) {
                    JSONObject sk = new JSONObject();
                    sk.put("skillId", e.getKey());
                    sk.put("level", e.getValue());
                    active.add(sk);
                }
                tier.put("activeSkills", active);
            }
            if (!b.skillMods.isEmpty()) {
                JSONArray mods = new JSONArray();
                for (SetSkillMod mod : b.skillMods) {
                    JSONObject m = new JSONObject();
                    m.put("skillId", mod.skillId());
                    if (mod.addAttackCount() != 0) {
                        m.put("addAttackCount", mod.addAttackCount());
                    }
                    if (mod.addLevel() != 0) {
                        m.put("addLevel", mod.addLevel());
                    }
                    if (mod.type() != null) {
                        m.put("type", mod.type());
                    }
                    mods.add(m);
                }
                tier.put("skillMods", mods);
            }
            tiers.add(tier);
        }
        root.put("tiers", tiers);
        return root.toJSONString();
    }

    private static void putIfNonZero(JSONObject obj, String key, int value) {
        if (value != 0) {
            obj.put(key, value);
        }
    }
}
