package org.gms.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.gms.dao.entity.ExtendValueDO;
import org.gms.constants.string.ExtendType;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 时装强化属性读取工具
 * 从 extend_value 表读取 "cashEquipEnhance_v2" 数据，
 * 供 {@code Character.recalcEquipStats()} 应用强化属性加成。
 *
 * @author 萧曵
 */
public class CashEquipEnhanceManager {

    public static final String ENHANCE_KEY = "cashEquipEnhance_v2";

    /**
     * 加载角色所有时装强化数据
     *
     * @param characterId 角色ID
     * @return slotKey → statMap，无数据返回空Map
     */
    public static Map<String, Map<String, Integer>> loadEnhanceData(int characterId) {
        ExtendValueDO extendDO = ExtendUtil.getExtendValue(
                String.valueOf(characterId),
                ExtendType.CHARACTER_EXTEND.getType(),
                ENHANCE_KEY
        );
        if (extendDO == null || extendDO.getExtendValue() == null || extendDO.getExtendValue().isEmpty()) {
            return Collections.emptyMap();
        }

        JSONObject root;
        try {
            root = JSON.parseObject(extendDO.getExtendValue());
        } catch (Exception e) {
            return Collections.emptyMap();
        }
        if (root == null || root.isEmpty()) {
            return Collections.emptyMap();
        }

        Map<String, Map<String, Integer>> result = new HashMap<>();
        for (String slotKey : root.keySet()) {
            JSONObject slotData = root.getJSONObject(slotKey);
            if (slotData == null) {
                continue;
            }
            JSONObject statsJson = slotData.getJSONObject("stats");
            if (statsJson == null) {
                continue;
            }
            Map<String, Integer> stats = new HashMap<>();
            for (String statName : statsJson.keySet()) {
                stats.put(statName, statsJson.getInteger(statName));
            }
            if (!stats.isEmpty()) {
                result.put(slotKey, stats);
            }
        }
        return result;
    }
}
