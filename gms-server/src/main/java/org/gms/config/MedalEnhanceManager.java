package org.gms.config;

import org.gms.dao.entity.MedalEnhanceConfigDO;
import org.gms.dao.entity.MedalEnhanceCostDO;
import org.gms.dao.entity.MedalEnhanceLevelDO;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 勋章强化配置的静态缓存管理器。
 * 供 GraalVM JS 脚本通过 {@code Java.type()} 调用查询强化规则。
 */
public class MedalEnhanceManager {

    /** 配置ID → 配置实体映射（仅包含启用的配置） */
    private static final Map<Long, MedalEnhanceConfigDO> configMap = new ConcurrentHashMap<>();
    /** 配置ID → 等级列表映射（按强化等级排序） */
    private static final Map<Long, List<MedalEnhanceLevelDO>> levelMap = new ConcurrentHashMap<>();
    /** 等级ID → 消耗物品列表映射 */
    private static final Map<Long, List<MedalEnhanceCostDO>> costMap = new ConcurrentHashMap<>();

    /** 私有构造函数，防止实例化 */
    private MedalEnhanceManager() {}

    /**
     * 加载配置数据到缓存
     * @param configs 配置DO列表
     * @param levels 等级DO列表
     * @param costs 消耗DO列表
     */
    public static synchronized void load(List<MedalEnhanceConfigDO> configs,
                                         List<MedalEnhanceLevelDO> levels,
                                         List<MedalEnhanceCostDO> costs) {
        // 清空缓存
        configMap.clear();
        levelMap.clear();
        costMap.clear();

        // 加载配置（仅启用的）
        for (MedalEnhanceConfigDO c : configs) {
            if (c.getEnabled() != null && c.getEnabled() == 1) {
                configMap.put(c.getId(), c);
            }
        }
        // 加载等级映射
        for (MedalEnhanceLevelDO lv : levels) {
            levelMap.computeIfAbsent(lv.getConfigId(), k -> new ArrayList<>()).add(lv);
        }
        // 加载消耗映射
        for (MedalEnhanceCostDO co : costs) {
            costMap.computeIfAbsent(co.getLevelId(), k -> new ArrayList<>()).add(co);
        }
        // 等级列表按强化等级排序
        for (List<MedalEnhanceLevelDO> list : levelMap.values()) {
            list.sort(Comparator.comparingInt(MedalEnhanceLevelDO::getEnhanceLevel));
        }
    }

    /**
     * 获取第一个（全局）启用的配置
     * @return 配置实体，无启用配置返回null
     */
    public static MedalEnhanceConfigDO getFirstConfig() {
        return configMap.isEmpty() ? null : configMap.values().iterator().next();
    }

    /**
     * 获取某配置的指定等级强化数据
     * @param configId 配置ID
     * @param enhanceLevel 强化等级
     * @return 强化等级实体，不存在返回null
     */
    public static MedalEnhanceLevelDO getLevel(Long configId, int enhanceLevel) {
        List<MedalEnhanceLevelDO> list = levelMap.get(configId);
        if (list == null) return null;
        for (MedalEnhanceLevelDO lv : list) {
            if (lv.getEnhanceLevel() == enhanceLevel) return lv;
        }
        return null;
    }

    /**
     * 获取某强化等级的道具消耗
     * @param levelId 等级ID
     * @return 消耗物品列表，无数据返回空列表
     */
    public static List<MedalEnhanceCostDO> getCosts(Long levelId) {
        return costMap.getOrDefault(levelId, Collections.emptyList());
    }

    /**
     * 获取配置的所有等级
     * @param configId 配置ID
     * @return 等级列表，无数据返回空列表
     */
    public static List<MedalEnhanceLevelDO> getAllLevels(Long configId) {
        return levelMap.getOrDefault(configId, Collections.emptyList());
    }

    /**
     * 获取所有已启用配置的Map
     * @return 配置ID→配置实体映射
     */
    public static Map<Long, MedalEnhanceConfigDO> getConfigMap() {
        return configMap;
    }
}