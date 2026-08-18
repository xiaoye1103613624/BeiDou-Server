package org.gms.skilltech;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.mybatisflex.core.query.QueryWrapper;
import org.gms.client.Skill;
import org.gms.client.SkillFactory;
import org.gms.dao.entity.SkillTechDO;
import org.gms.dao.mapper.SkillTechMapper;
import org.gms.manager.ServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 技改：加载 xy_skill_tech，扩展技能效果等级并锁定手动 SP 上限。
 */
public final class SkillTechManager {
    private static final Logger log = LoggerFactory.getLogger(SkillTechManager.class);

    private static final ConcurrentHashMap<Integer, SkillTechDO> BY_SKILL = new ConcurrentHashMap<>();

    private SkillTechManager() {}

    public static void applyAll() {
        reloadAndApply();
    }

    public static synchronized void reloadAndApply() {
        try {
            SkillTechMapper mapper = ServerManager.getApplicationContext().getBean(SkillTechMapper.class);
            List<SkillTechDO> rows = mapper.selectListByQuery(
                    QueryWrapper.create().eq(SkillTechDO::getEnabled, 1));
            BY_SKILL.clear();
            int applied = 0;
            if (rows != null) {
                for (SkillTechDO row : rows) {
                    if (row.getSkillId() == null) {
                        continue;
                    }
                    BY_SKILL.put(row.getSkillId(), row);
                    if (applyOne(row)) {
                        applied++;
                    }
                }
            }
            log.info("SkillTechManager applied {} skill tech configs (enabled={})",
                    applied, BY_SKILL.size());
        } catch (Exception e) {
            log.warn("SkillTechManager apply skipped: {}", e.getMessage());
        }
    }

    public static boolean applyOne(SkillTechDO row) {
        if (row == null || row.getSkillId() == null) {
            return false;
        }
        Skill skill = SkillFactory.getSkill(row.getSkillId());
        if (skill == null) {
            log.warn("SkillTech: skill {} not loaded in SkillFactory", row.getSkillId());
            return false;
        }
        int spMax = row.getSpMaxLevel() == null ? skill.getMaxLevel() : row.getSpMaxLevel();
        int effectMax = row.getEffectMaxLevel() == null ? skill.getMaxLevel() : row.getEffectMaxLevel();
        if (spMax <= 0) {
            spMax = skill.getMaxLevel();
        }
        if (effectMax < spMax) {
            effectMax = spMax;
        }
        Map<Integer, Map<String, Integer>> overrides = parseLevelsJson(row.getLevelsJson());
        // 先设 SP 上限（基于技改前原始上限），再扩展效果等级
        skill.setSpMaxLevel(spMax);
        skill.applyTechExtension(effectMax, overrides);
        return true;
    }

    public static Map<Integer, Map<String, Integer>> parseLevelsJson(String json) {
        Map<Integer, Map<String, Integer>> out = new HashMap<>();
        if (json == null || json.isBlank()) {
            return out;
        }
        try {
            JSONObject root = JSON.parseObject(json);
            if (root == null) {
                return out;
            }
            for (String key : root.keySet()) {
                int lv;
                try {
                    lv = Integer.parseInt(key);
                } catch (NumberFormatException e) {
                    continue;
                }
                JSONObject attrs = root.getJSONObject(key);
                if (attrs == null) {
                    continue;
                }
                Map<String, Integer> map = new HashMap<>();
                for (String ak : attrs.keySet()) {
                    Integer v = attrs.getInteger(ak);
                    if (v != null) {
                        map.put(ak, v);
                    }
                }
                if (!map.isEmpty()) {
                    out.put(lv, map);
                }
            }
        } catch (Exception e) {
            log.warn("SkillTech levels_json parse failed: {}", e.getMessage());
        }
        return out;
    }

    public static SkillTechDO get(int skillId) {
        return BY_SKILL.get(skillId);
    }
}
