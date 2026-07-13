package org.gms.server.setitem;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.gms.client.Character;
import org.gms.client.SkillFactory;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.combat.format.CombatStatFormatter;
import org.gms.combat.stat.CombatStatProfile;
import org.gms.combat.stat.CombatStatType;
import org.gms.net.server.Server;
import org.gms.provider.Data;
import org.gms.provider.DataProvider;
import org.gms.provider.DataProviderFactory;
import org.gms.provider.DataTool;
import org.gms.provider.wz.WZFiles;
import org.gms.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public final class SetItemManager {
    private static final Map<Integer, Integer> itemToSet = new HashMap<>();
    private static final Map<Integer, SetDefinition> setDefinitions = new HashMap<>();
    private static final Set<Integer> validWzSetIds = new HashSet<>();
    private static boolean loaded = false;

    private SetItemManager() {}

    public static synchronized void loadOrSeed() {
        if (loaded) {
            return;
        }
        try {
            loadFromWz();
            loadFromDb();
            loaded = true;
            log.info("SetItemManager: {} sets, {} item mappings", setDefinitions.size(), itemToSet.size());
        } catch (Exception e) {
            log.error("SetItemManager load failed", e);
        }
    }

    public static synchronized void reload() {
        itemToSet.clear();
        setDefinitions.clear();
        validWzSetIds.clear();
        loaded = false;
        loadOrSeed();
        refreshOnlinePlayers();
    }

    private static void refreshOnlinePlayers() {
        for (var wserv : Server.getInstance().getWorlds()) {
            for (var cserv : wserv.getChannels()) {
                for (Character chr : cserv.getPlayerStorage().getAllCharacters()) {
                    if (chr != null) {
                        chr.resetSetSkillBonusCache();
                        chr.markCombatStatsDirty();
                        chr.refreshSetBonus();
                    }
                }
            }
        }
    }

    private static void loadFromWz() {
        DataProvider dp = DataProviderFactory.getDataProvider(WZFiles.ETC);
        if (dp == null) {
            log.warn("SetItemManager: ETC data provider unavailable");
            return;
        }
        Data root = dp.getData("SetItemInfo.img");
        if (root == null) {
            log.warn("SetItemManager: SetItemInfo.img not found in WZ");
            return;
        }
        for (Data setNode : root.getChildren()) {
            int setId;
            try {
                setId = Integer.parseInt(setNode.getName());
            } catch (NumberFormatException nfe) {
                continue;
            }
            if (setId <= 0) {
                continue;
            }
            try {
                SetDefinition def = parseSetDefinition(setId, setNode);
                setDefinitions.put(setId, def);
                validWzSetIds.add(setId);
                for (int itemId : def.itemIds) {
                    itemToSet.put(itemId, setId);
                }
            } catch (Exception e) {
                log.warn("SetItemManager: skip set {} due to {}", setId, e.getMessage());
            }
        }
    }

    private static void collectItemIds(Data node, Set<Integer> out) {
        if (node == null) {
            return;
        }
        for (Data child : node.getChildren()) {
            if (child.getData() != null) {
                int itemId = DataTool.getInt(child);
                if (itemId > 0) {
                    out.add(itemId);
                }
            } else {
                collectItemIds(child, out);
            }
        }
    }

    private static SetDefinition parseSetDefinition(int setId, Data setNode) {
        SetDefinition def = new SetDefinition();
        def.setId = setId;
        def.setName = DataTool.getString("setItemName", setNode, "Set " + setId);
        def.completeCount = DataTool.getInt("completeCount", setNode, 0);
        def.enabled = true;
        def.fromWz = true;

        Data itemIds = setNode.getChildByPath("ItemID");
        if (itemIds != null) {
            collectItemIds(itemIds, def.itemIds);
        }

        Data effectRoot = setNode.getChildByPath("Effect");
        if (effectRoot != null) {
            for (Data tierNode : effectRoot.getChildren()) {
                int tierCount;
                try {
                    tierCount = Integer.parseInt(tierNode.getName());
                } catch (NumberFormatException nfe) {
                    continue;
                }
                SetBonus bonus = parseWzEffectTier(tierNode);
                bonus.requiredCount = tierCount;
                def.tiers.put(tierCount, bonus);
            }
        }
        return def;
    }

    private static SetBonus parseWzEffectTier(Data tierNode) {
        SetBonus bonus = SetTiersV2Parser.parseEffectTier(tierNode);

        Data activeSkill = tierNode.getChildByPath("activeSkill");
        if (activeSkill != null) {
            for (Data child : activeSkill.getChildren()) {
                int id = DataTool.getInt("id", child, 0);
                int level = DataTool.getInt("level", child, 0);
                if (id > 0 && level > 0) {
                    bonus.activeSkills.put(id, level);
                }
            }
        }

        Data incSkillLevel = tierNode.getChildByPath("incSkillLevel");
        if (incSkillLevel != null && hasSkillLevelContent(incSkillLevel)) {
            int id = DataTool.getInt("id", incSkillLevel, 0);
            int level = DataTool.getInt("level", incSkillLevel, 0);
            if (id > 0 && level > 0) {
                bonus.skillLevels.put(id, level);
            }
            for (Data child : incSkillLevel.getChildren()) {
                int childId = DataTool.getInt("id", child, 0);
                int childLevel = DataTool.getInt("level", child, 0);
                if (childId > 0 && childLevel > 0) {
                    bonus.skillLevels.put(childId, childLevel);
                }
            }
        }

        Data skillNode = tierNode.getChildByPath("Skill");
        if (skillNode != null) {
            int id = DataTool.getInt("id", skillNode, 0);
            int level = DataTool.getInt("level", skillNode, 0);
            if (id > 0 && level > 0) {
                bonus.skillLevels.put(id, level);
            }
        }
        return bonus;
    }

    private static boolean hasSkillLevelContent(Data node) {
        if (node.getChildren().isEmpty() && node.getData() == null) {
            return false;
        }
        if (DataTool.getInt("id", node, 0) > 0) {
            return true;
        }
        return !node.getChildren().isEmpty();
    }

    private static void loadFromDb() throws SQLException {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(
                     "SELECT set_id, set_name, complete_count, item_ids, enabled, tiers_json FROM xy_set_item");
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                int setId = rs.getInt("set_id");
                SetDefinition def = setDefinitions.getOrDefault(setId, new SetDefinition());
                def.setId = setId;
                def.fromDb = true;
                if (def.setName == null || def.setName.isBlank()) {
                    def.setName = "Set " + setId;
                }

                String name = rs.getString("set_name");
                if (name != null && !name.isBlank()) {
                    def.setName = name;
                }
                int completeCount = rs.getInt("complete_count");
                if (completeCount > 0) {
                    def.completeCount = completeCount;
                }
                def.enabled = rs.getInt("enabled") == 1;

                String itemIds = rs.getString("item_ids");
                if (itemIds != null && !itemIds.isBlank()) {
                    def.itemIds.clear();
                    for (String part : itemIds.split(",")) {
                        try {
                            int itemId = Integer.parseInt(part.trim());
                            if (itemId > 0) {
                                def.itemIds.add(itemId);
                            }
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }

                String tiersJson = rs.getString("tiers_json");
                if (tiersJson != null && !tiersJson.isBlank()) {
                    def.tiers.clear();
                    SetTiersV2Parser.applyTiersJson(def, tiersJson);
                }

                setDefinitions.put(setId, def);
                if (def.enabled && !def.itemIds.isEmpty()) {
                    reindexItemsForSet(setId, def);
                }
            }
        }
    }

    private static void reindexItemsForSet(int setId, SetDefinition def) {
        for (int itemId : def.itemIds) {
            itemToSet.put(itemId, setId);
        }
    }

    public static int getSetId(int itemId) {
        return itemToSet.getOrDefault(itemId, 0);
    }

    public static boolean isSetEnabled(int setId) {
        SetDefinition def = setDefinitions.get(setId);
        return def != null && def.enabled;
    }

    /**
     * 低档累加：合并所有「已启用 && 穿戴件数 ≥ count」的档位。
     */
    public static SetBonus getBonusCumulative(int setId, int equipCount) {
        SetDefinition def = setDefinitions.get(setId);
        if (def == null || !def.enabled || equipCount <= 0) {
            return null;
        }
        SetBonus total = new SetBonus();
        boolean any = false;
        List<Integer> counts = new ArrayList<>(def.tiers.keySet());
        counts.sort(Comparator.naturalOrder());
        for (int need : counts) {
            if (need > equipCount) {
                continue;
            }
            SetBonus tier = def.tiers.get(need);
            if (tier == null || !tier.tierEnabled) {
                continue;
            }
            total.merge(copyBonus(tier));
            any = true;
        }
        return any ? total : null;
    }

    /** @deprecated 使用 {@link #getBonusCumulative} */
    public static SetBonus getBonus(int setId, int equipCount) {
        return getBonusCumulative(setId, equipCount);
    }

    public static List<Integer> collectFinalDamageSources(SetBonus bonus) {
        List<Integer> sources = new ArrayList<>();
        if (bonus == null) {
            return sources;
        }
        sources.addAll(bonus.collectFinalDamageSources());
        return sources;
    }

    public static SetBonus getTotalSetBonus(Character chr) {
        SetBonus total = new SetBonus();
        Map<Integer, Integer> countMap = countEquippedSets(chr);
        for (Map.Entry<Integer, Integer> entry : countMap.entrySet()) {
            int setId = entry.getKey();
            if (!isSetEnabled(setId)) {
                continue;
            }
            SetBonus bonus = getBonusCumulative(setId, entry.getValue());
            if (bonus == null) {
                continue;
            }
            SetBonus copy = copyBonus(bonus);
            for (Map.Entry<Integer, Integer> sk : copy.skillLevels.entrySet()) {
                int converted = convertSkillIdByJob(sk.getKey(), chr.getJob().getId());
                if (converted != -1) {
                    total.skillLevels.merge(converted, sk.getValue(), Integer::sum);
                } else {
                    total.skillLevels.merge(sk.getKey(), sk.getValue(), Integer::sum);
                }
            }
            copy.skillLevels.clear();
            for (Map.Entry<Integer, Integer> sk : copy.activeSkills.entrySet()) {
                total.activeSkills.merge(sk.getKey(), sk.getValue(), Math::max);
            }
            copy.activeSkills.clear();
            total.skillMods.addAll(copy.skillMods);
            copy.skillMods.clear();
            total.merge(copy);
        }
        return total;
    }

    public static SetBonus getSetBonusForSet(Character chr, int setId) {
        if (!isSetEnabled(setId)) {
            return new SetBonus();
        }
        Map<Integer, Integer> countMap = countEquippedSets(chr);
        int count = countMap.getOrDefault(setId, 0);
        SetBonus bonus = getBonusCumulative(setId, count);
        if (bonus == null) {
            return new SetBonus();
        }
        SetBonus copy = copyBonus(bonus);
        Map<Integer, Integer> converted = new HashMap<>();
        for (Map.Entry<Integer, Integer> sk : copy.skillLevels.entrySet()) {
            int skillId = convertSkillIdByJob(sk.getKey(), chr.getJob().getId());
            if (skillId == -1) {
                skillId = sk.getKey();
            }
            converted.merge(skillId, sk.getValue(), Integer::sum);
        }
        copy.skillLevels.clear();
        copy.skillLevels.putAll(converted);
        return copy;
    }

    public static CombatStatProfile resolveSetCombatProfile(Character chr) {
        return org.gms.combat.provider.CombatProfileService.resolve(chr);
    }

    private static SetBonus copyBonus(SetBonus src) {
        SetBonus copy = new SetBonus(src.requiredCount);
        copy.tierEnabled = src.tierEnabled;
        copy.str = src.str;
        copy.dex = src.dex;
        copy.int_ = src.int_;
        copy.luk = src.luk;
        copy.pad = src.pad;
        copy.mad = src.mad;
        copy.pdd = src.pdd;
        copy.mdd = src.mdd;
        copy.acc = src.acc;
        copy.eva = src.eva;
        copy.mhp = src.mhp;
        copy.mmp = src.mmp;
        copy.speed = src.speed;
        copy.jump = src.jump;
        copy.strR = src.strR;
        copy.dexR = src.dexR;
        copy.intR = src.intR;
        copy.lukR = src.lukR;
        copy.mhpR = src.mhpR;
        copy.mmpR = src.mmpR;
        copy.finalDamagePercent = src.finalDamagePercent;
        copy.damageSkinId = src.damageSkinId;
        copy.combatStats.putAll(src.combatStats);
        copy.skillLevels.putAll(src.skillLevels);
        copy.activeSkills.putAll(src.activeSkills);
        copy.skillMods.addAll(src.skillMods);
        copy.finalDamageSources.addAll(src.finalDamageSources);
        if (copy.finalDamageSources.isEmpty()) {
            copy.finalDamageSources.addAll(src.collectFinalDamageSources());
        }
        return copy;
    }

    /** 当前穿戴套装对指定技能的额外攻击段数（累加）。 */
    public static int getAddAttackCount(Character chr, int skillId) {
        if (chr == null || skillId <= 0) {
            return 0;
        }
        SetBonus bonus = getTotalSetBonus(chr);
        int add = 0;
        for (SetSkillMod mod : bonus.skillMods) {
            if (mod.addAttackCount() <= 0) {
                continue;
            }
            int modSkill = convertSkillIdByJob(mod.skillId(), chr.getJob().getId());
            if (modSkill == -1) {
                modSkill = mod.skillId();
            }
            if (modSkill == skillId || mod.skillId() == skillId) {
                add += mod.addAttackCount();
            }
        }
        return add;
    }

    private static Map<Integer, Integer> countEquippedSets(Character chr) {
        Map<Integer, Integer> countMap = new HashMap<>();
        for (Item item : chr.getInventory(InventoryType.EQUIPPED).list()) {
            int setId = getSetId(item.getItemId());
            if (setId != 0) {
                countMap.put(setId, countMap.getOrDefault(setId, 0) + 1);
            }
        }
        return countMap;
    }

    public static String buildSetBonusText(Character chr, int setId) {
        if (!isSetEnabled(setId)) {
            return "";
        }
        SetDefinition def = setDefinitions.get(setId);
        if (def == null) {
            return "";
        }
        Map<Integer, Integer> countMap = countEquippedSets(chr);
        int count = countMap.getOrDefault(setId, 0);
        SetBonus bonus = getSetBonusForSet(chr, setId);
        StringBuilder sb = new StringBuilder();
        sb.append(def.setName).append(" (").append(count).append("/")
                .append(def.completeCount > 0 ? def.completeCount : def.itemIds.size()).append(")\r\n");
        CombatStatFormatter.appendSetBonusLines(sb, bonus);
        return sb.toString().trim();
    }

    public static Map<Integer, String> buildAllSetBonusTexts(Character chr) {
        Map<Integer, String> result = new LinkedHashMap<>();
        Map<Integer, Integer> countMap = countEquippedSets(chr);
        for (int setId : countMap.keySet()) {
            if (!isSetEnabled(setId)) {
                continue;
            }
            String text = buildSetBonusText(chr, setId);
            if (!text.isBlank()) {
                result.put(setId, text);
            }
        }
        return result;
    }

    public static Set<Integer> getValidWzSetIds() {
        return Collections.unmodifiableSet(validWzSetIds);
    }

    public static SetDefinition getDefinition(int setId) {
        return setDefinitions.get(setId);
    }

    public static List<SetDefinition> listAllDefinitions() {
        return setDefinitions.values().stream()
                .sorted(Comparator.comparingInt(d -> d.setId))
                .collect(Collectors.toList());
    }

    public static String itemIdsToCsv(SetDefinition def) {
        if (def == null || def.itemIds.isEmpty()) {
            return "";
        }
        return def.itemIds.stream().sorted().map(String::valueOf).collect(Collectors.joining(","));
    }

    public static String sourceLabel(SetDefinition def) {
        if (def == null) {
            return "";
        }
        if (def.fromWz && def.fromDb) {
            return "WZ+DB";
        }
        if (def.fromDb) {
            return "DB";
        }
        if (def.fromWz) {
            return "WZ";
        }
        return "CUSTOM";
    }

    public static SetBonus previewBonus(int setId, int equipCount, int jobId) {
        if (!isSetEnabled(setId)) {
            return new SetBonus();
        }
        SetBonus bonus = getBonusCumulative(setId, equipCount);
        if (bonus == null) {
            return new SetBonus();
        }
        SetBonus copy = copyBonus(bonus);
        Map<Integer, Integer> converted = new HashMap<>();
        for (Map.Entry<Integer, Integer> sk : copy.skillLevels.entrySet()) {
            int skillId = convertSkillIdByJob(sk.getKey(), jobId);
            if (skillId == -1) {
                skillId = sk.getKey();
            }
            converted.merge(skillId, sk.getValue(), Integer::sum);
        }
        copy.skillLevels.clear();
        copy.skillLevels.putAll(converted);
        return copy;
    }

    public static int convertSkillIdByJob(int originalSkillId, int jobId) {
        if (originalSkillId == 1000001) {
            return switch (jobId) {
                case 100 -> 1001005;
                case 200 -> 2001005;
                case 300 -> 3001005;
                case 400 -> 4000000;
                case 500 -> 5000000;
                default -> -1;
            };
        }
        if (originalSkillId == 1000002) {
            return switch (jobId) {
                case 110, 120, 130 -> 1001005;
                case 210 -> 2101004;
                case 220 -> 2201005;
                case 230 -> 2301004;
                case 310 -> 3101005;
                case 320 -> 3201005;
                case 410 -> 4100001;
                case 420 -> 4201005;
                case 510 -> 5101003;
                case 520 -> 5201001;
                default -> -1;
            };
        }
        if (originalSkillId == 1000003) {
            return switch (jobId) {
                case 111 -> 1111002;
                case 121 -> 1211002;
                case 131 -> 1311001;
                case 211 -> 2111003;
                case 221 -> 2211003;
                case 231 -> 2301004;
                case 311 -> 3111004;
                case 321 -> 3211004;
                case 411 -> 4111002;
                case 421 -> 4211004;
                case 511 -> 5111006;
                case 521 -> 5211004;
                default -> -1;
            };
        }
        if (originalSkillId == 1000004) {
            return switch (jobId) {
                case 112 -> 1121008;
                case 122 -> 1221009;
                case 132 -> 1311001;
                case 212 -> 2121006;
                case 222 -> 2221006;
                case 232 -> 2321000;
                case 312 -> 3121004;
                case 322 -> 3221001;
                case 412 -> 4121007;
                case 422 -> 4221007;
                case 512 -> 5121007;
                case 522 -> 5221004;
                default -> -1;
            };
        }
        return originalSkillId;
    }
}
