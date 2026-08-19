package org.gms.server.equipgrowth;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.gms.client.Character;
import org.gms.client.inventory.Equip;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.dao.entity.EquipGrowthDO;
import org.gms.dao.mapper.EquipGrowthMapper;
import org.gms.server.ItemInformationProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * DB 覆盖层：{@code xy_equip_growth} 优先于 WZ info/level。
 * <p>
 * levels_json schemaVersion=1:
 * {@code {"schemaVersion":1,"levels":[{"level":1,"enabled":true,"stats":{"str":1},"skills":[{"id":1000,"level":1}]}]}}
 * <p>
 * 语义（对齐 WZ）：
 * <ul>
 *   <li>{@code max_level} = 物品等级上限（itemLevel 可达到的最大值）</li>
 *   <li>等级段 {@code level=N} 在 itemLevel &gt; N 时生效（升到 N+1 时拿到 N 段效果）</li>
 *   <li>因此 WZ 导入时：段数通常 = max_level - 1；技能挂在各段上，已达成段叠加</li>
 * </ul>
 * {@code skills_json} 仅作旧数据兼容（穿戴即生效一次）；新配置请写在各等级段 skills。
 */
@Component
public class EquipGrowthConfigManager {
    private static final Logger log = LoggerFactory.getLogger(EquipGrowthConfigManager.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private static final String[][] STAT_KEYS = {
            {"str", "incSTR", "力量"},
            {"dex", "incDEX", "敏捷"},
            {"int", "incINT", "智力"},
            {"luk", "incLUK", "运气"},
            {"pad", "incPAD", "攻击力"},
            {"mad", "incMAD", "魔力"},
            {"mhp", "incMHP", "最大血量"},
            {"mmp", "incMMP", "最大魔量"},
            {"pdd", "incPDD", "防御力"},
            {"mdd", "incMDD", "魔法防御"},
            {"acc", "incACC", "命中"},
            {"eva", "incEVA", "回避"},
            {"speed", "incSpeed", "移动速度"},
            {"jump", "incJump", "跳跃"},
    };

    private static EquipGrowthConfigManager INSTANCE;

    private final EquipGrowthMapper mapper;
    private final ConcurrentHashMap<Integer, EquipGrowthDO> byItemId = new ConcurrentHashMap<>();

    public EquipGrowthConfigManager(EquipGrowthMapper mapper) {
        this.mapper = mapper;
    }

    @PostConstruct
    public void init() {
        INSTANCE = this;
        reload();
    }

    public static EquipGrowthConfigManager get() {
        return INSTANCE;
    }

    public synchronized void reload() {
        byItemId.clear();
        try {
            List<EquipGrowthDO> rows = mapper.selectAll();
            for (EquipGrowthDO row : rows) {
                if (row.getItemId() != null) {
                    byItemId.put(row.getItemId(), row);
                }
            }
            log.info("EquipGrowthConfigManager: loaded {} rows", byItemId.size());
        } catch (Throwable t) {
            log.warn("EquipGrowthConfigManager reload failed: {}", t.toString());
        }
    }

    public EquipGrowthDO get(int itemId) {
        return byItemId.get(itemId);
    }

    public List<EquipGrowthDO> listAll() {
        return new ArrayList<>(byItemId.values());
    }

    /** DB 显式停用。 */
    public boolean isDisabled(int itemId) {
        EquipGrowthDO row = byItemId.get(itemId);
        return row != null && row.getEnabled() != null && row.getEnabled() == 0;
    }

    /** 有自定义 levels_json 且启用。 */
    public boolean hasCustomLevels(int itemId) {
        EquipGrowthDO row = byItemId.get(itemId);
        return row != null
                && (row.getEnabled() == null || row.getEnabled() != 0)
                && row.getLevelsJson() != null
                && !row.getLevelsJson().isBlank()
                && countLevels(row.getLevelsJson()) > 0;
    }

    public int resolveMaxLevel(int itemId, int wzMax) {
        EquipGrowthDO row = byItemId.get(itemId);
        if (row == null || row.getEnabled() != null && row.getEnabled() == 0) {
            return wzMax;
        }
        if (row.getMaxLevel() != null && row.getMaxLevel() > 0) {
            return row.getMaxLevel();
        }
        int derived = deriveMaxLevelFromLevelsJson(row.getLevelsJson());
        if (derived > 0) {
            return derived;
        }
        return wzMax;
    }

    /** 列表用：正确统计 levels 数组长度（勿用字符串切 "level"，会把技能 level 算进去）。 */
    public static int countLevels(String levelsJson) {
        if (levelsJson == null || levelsJson.isBlank()) {
            return 0;
        }
        try {
            JsonNode levels = MAPPER.readTree(levelsJson).path("levels");
            return levels.isArray() ? levels.size() : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 由等级段推导物品等级上限：max(段.level) + 1（WZ：段 N 在 itemLevel=N+1 时生效）。
     * 无段时返回 0。
     */
    public static int deriveMaxLevelFromLevelsJson(String levelsJson) {
        if (levelsJson == null || levelsJson.isBlank()) {
            return 0;
        }
        try {
            JsonNode levels = MAPPER.readTree(levelsJson).path("levels");
            if (!levels.isArray() || levels.isEmpty()) {
                return 0;
            }
            int maxSeg = 0;
            for (JsonNode lv : levels) {
                maxSeg = Math.max(maxSeg, lv.path("level").asInt(0));
            }
            return maxSeg > 0 ? maxSeg + 1 : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 穿戴中的可成长装备：按当前 itemLevel 叠加已达成段的技能等级加成。
     */
    public Map<Integer, Integer> sumEquippedSkillBonuses(Character chr) {
        Map<Integer, Integer> out = new LinkedHashMap<>();
        if (chr == null) {
            return out;
        }
        for (Item item : chr.getInventory(InventoryType.EQUIPPED).list()) {
            if (!(item instanceof Equip eq)) {
                continue;
            }
            int itemId = eq.getItemId();
            if (get(itemId) == null || isDisabled(itemId)) {
                continue;
            }
            int itemLevel = eq.getItemLevel() & 0xFF;
            for (Map.Entry<Integer, Integer> e : sumAchievedSkillBonuses(itemId, itemLevel).entrySet()) {
                out.merge(e.getKey(), e.getValue(), Integer::sum);
            }
        }
        return out;
    }

    /**
     * 已达成等级段的技能加成（段 level &lt; itemLevel 时叠加；与属性成长节点约定一致）。
     */
    public Map<Integer, Integer> sumAchievedSkillBonuses(int itemId, int itemLevel) {
        Map<Integer, Integer> out = new LinkedHashMap<>();
        EquipGrowthDO row = byItemId.get(itemId);
        if (row == null || row.getEnabled() != null && row.getEnabled() == 0) {
            return out;
        }
        if (row.getLevelsJson() != null && !row.getLevelsJson().isBlank()) {
            try {
                JsonNode levels = MAPPER.readTree(row.getLevelsJson()).path("levels");
                if (levels.isArray()) {
                    for (JsonNode lv : levels) {
                        if (!lv.path("enabled").asBoolean(true)) {
                            continue;
                        }
                        int seg = lv.path("level").asInt(0);
                        if (seg <= 0 || itemLevel <= seg) {
                            continue;
                        }
                        mergeSkills(out, lv.path("skills"));
                    }
                }
            } catch (Exception e) {
                log.warn("sumAchievedSkillBonuses levels itemId={}: {}", itemId, e.toString());
            }
        }
        // 旧版全局 skills_json：穿戴且 itemLevel>=1 时生效一次（不随段叠加）
        if (itemLevel >= 1 && row.getSkillsJson() != null && !row.getSkillsJson().isBlank()) {
            try {
                mergeSkills(out, MAPPER.readTree(row.getSkillsJson()));
            } catch (Exception e) {
                log.warn("sumAchievedSkillBonuses legacy skills itemId={}: {}", itemId, e.toString());
            }
        }
        return out;
    }

    /** 已达成段的自定义属性（短 key：str/pad…）；无自定义时返回空。 */
    public Map<String, Integer> sumAchievedCustomStats(int itemId, int itemLevel) {
        Map<String, Integer> out = new LinkedHashMap<>();
        if (!hasCustomLevels(itemId) || itemLevel <= 1) {
            return out;
        }
        EquipGrowthDO row = byItemId.get(itemId);
        try {
            JsonNode levels = MAPPER.readTree(row.getLevelsJson()).path("levels");
            if (!levels.isArray()) {
                return out;
            }
            for (JsonNode lv : levels) {
                if (!lv.path("enabled").asBoolean(true)) {
                    continue;
                }
                int seg = lv.path("level").asInt(0);
                if (seg <= 0 || itemLevel <= seg) {
                    continue;
                }
                JsonNode stats = lv.path("stats");
                for (String[] k : STAT_KEYS) {
                    int v = stats.path(k[0]).asInt(0);
                    if (v == 0) {
                        v = stats.path(k[1]).asInt(0);
                    }
                    if (v != 0) {
                        out.merge(k[1], v, Integer::sum);
                    }
                }
            }
        } catch (Exception e) {
            log.warn("sumAchievedCustomStats itemId={}: {}", itemId, e.toString());
        }
        return out;
    }

    private static void mergeSkills(Map<Integer, Integer> out, JsonNode skills) {
        if (skills == null || !skills.isArray()) {
            return;
        }
        for (JsonNode sk : skills) {
            int sid = sk.path("id").asInt(sk.path("skillId").asInt(0));
            int slv = sk.path("level").asInt(1);
            if (sid > 0 && slv != 0) {
                out.merge(sid, slv, Integer::sum);
            }
        }
    }

    public String buildCustomTipText(int itemId) {
        EquipGrowthDO row = byItemId.get(itemId);
        if (row == null || row.getLevelsJson() == null || row.getLevelsJson().isBlank()) {
            return "";
        }
        try {
            JsonNode root = MAPPER.readTree(row.getLevelsJson());
            JsonNode levels = root.path("levels");
            if (!levels.isArray() || levels.isEmpty()) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            sb.append("装备成长属性\r\n");
            boolean any = false;
            for (JsonNode lv : levels) {
                if (lv.path("enabled").asBoolean(true) == false) {
                    continue;
                }
                int level = lv.path("level").asInt(0);
                if (level <= 0) {
                    continue;
                }
                JsonNode stats = lv.path("stats");
                Map<String, Integer> gained = new LinkedHashMap<>();
                for (String[] k : STAT_KEYS) {
                    int v = stats.path(k[0]).asInt(0);
                    if (v == 0) {
                        v = stats.path(k[1]).asInt(0);
                    }
                    if (v != 0) {
                        gained.put(k[1], v);
                    }
                }
                JsonNode skills = lv.path("skills");
                boolean hasSkills = skills.isArray() && skills.size() > 0;
                if (gained.isEmpty() && !hasSkills) {
                    continue;
                }
                any = true;
                sb.append(level).append("级效果\r\n");
                EquipGrowthTipManager.appendTierStats(sb, gained);
                if (hasSkills) {
                    for (JsonNode sk : skills) {
                        int sid = sk.path("id").asInt(sk.path("skillId").asInt(0));
                        int slv = sk.path("level").asInt(1);
                        if (sid > 0) {
                            sb.append("技能等级 ").append(sid).append(" : +").append(slv).append("\r\n");
                        }
                    }
                }
            }
            // 旧全局技能：单独一行说明（新配置不应再写 skills_json）
            if (row.getSkillsJson() != null && !row.getSkillsJson().isBlank()) {
                JsonNode gSkills = MAPPER.readTree(row.getSkillsJson());
                if (gSkills.isArray() && gSkills.size() > 0) {
                    any = true;
                    sb.append("穿戴技能\r\n");
                    for (JsonNode sk : gSkills) {
                        int sid = sk.path("id").asInt(sk.path("skillId").asInt(0));
                        int slv = sk.path("level").asInt(1);
                        if (sid > 0) {
                            sb.append("技能等级 ").append(sid).append(" : +").append(slv).append("\r\n");
                        }
                    }
                }
            }
            return any ? sb.toString().trim() : sb.toString().trim();
        } catch (Exception e) {
            log.warn("buildCustomTipText itemId={} failed: {}", itemId, e.toString());
            return "";
        }
    }

    /** 从 WZ 成长树生成默认 levels_json（初始化用）。 */
    public static String buildLevelsJsonFromWz(int itemId) {
        try {
            int max = ItemInformationProvider.getInstance().getEquipLevel(itemId, true);
            if (max <= 1) {
                return "{\"schemaVersion\":1,\"levels\":[]}";
            }
            List<Map<String, Object>> levels = new ArrayList<>();
            for (int node = 1; node < max; node++) {
                Map<String, Integer> stats = new LinkedHashMap<>();
                for (var p : ItemInformationProvider.getInstance().getItemLevelupStatsMin(itemId, node)) {
                    if (p.getRight() != null && p.getRight() != 0) {
                        String key = toShortStatKey(p.getLeft());
                        if (key != null) {
                            stats.put(key, p.getRight());
                        }
                    }
                }
                if (stats.isEmpty()) {
                    continue;
                }
                Map<String, Object> lv = new LinkedHashMap<>();
                lv.put("level", node);
                lv.put("enabled", true);
                lv.put("stats", stats);
                lv.put("skills", List.of());
                levels.add(lv);
            }
            Map<String, Object> root = new LinkedHashMap<>();
            root.put("schemaVersion", 1);
            root.put("levels", levels);
            return MAPPER.writeValueAsString(root);
        } catch (Exception e) {
            return "{\"schemaVersion\":1,\"levels\":[]}";
        }
    }

    private static String toShortStatKey(String wzKey) {
        if (wzKey == null) {
            return null;
        }
        for (String[] k : STAT_KEYS) {
            if (k[1].equalsIgnoreCase(wzKey)) {
                return k[0];
            }
        }
        return null;
    }
}
