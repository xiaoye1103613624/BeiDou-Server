package org.gms.config;

import org.gms.dao.entity.EquipEnhanceConfigDO;
import org.gms.dao.entity.EquipEnhanceLevelDO;
import org.gms.dao.entity.EquipEnhanceCostDO;
import org.gms.manager.ServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 装备强化配置的静态缓存管理器。
 * 供 GraalVM JS 脚本通过 {@code Java.type()} 调用查询强化规则。
 */
public class EquipEnhanceManager {

    private static final Logger log = LoggerFactory.getLogger(EquipEnhanceManager.class);

    /** 物品ID → 配置实体映射（仅包含启用的配置） */
    private static final Map<Integer, EquipEnhanceConfigDO> configMap = new ConcurrentHashMap<>();
    /** 配置ID → 等级列表映射（按强化等级排序） */
    private static final Map<Long, List<EquipEnhanceLevelDO>> levelMap = new ConcurrentHashMap<>();
    /** 等级ID → 消耗物品列表映射 */
    private static final Map<Long, List<EquipEnhanceCostDO>> costMap = new ConcurrentHashMap<>();

    /** 私有构造函数，防止实例化 */
    private EquipEnhanceManager() {}

    /**
     * 加载配置数据到缓存
     * @param configs 配置DO列表
     * @param levels 等级DO列表
     * @param costs 消耗DO列表
     */
    public static synchronized void load(List<EquipEnhanceConfigDO> configs,
                                         List<EquipEnhanceLevelDO> levels,
                                         List<EquipEnhanceCostDO> costs) {
        // 清空缓存
        configMap.clear();
        levelMap.clear();
        costMap.clear();

        int enabledCount = 0;
        // 加载配置（仅启用的），key为物品ID
        for (EquipEnhanceConfigDO c : configs) {
            if (c.getEnabled() != null && c.getEnabled() == 1) {
                configMap.put(c.getItemId(), c);
                enabledCount++;
            }
        }
        // 加载等级映射
        for (EquipEnhanceLevelDO lv : levels) {
            levelMap.computeIfAbsent(lv.getConfigId(), k -> new ArrayList<>()).add(lv);
        }
        // 加载消耗映射
        for (EquipEnhanceCostDO co : costs) {
            costMap.computeIfAbsent(co.getLevelId(), k -> new ArrayList<>()).add(co);
        }
        // 等级列表按强化等级排序
        for (List<EquipEnhanceLevelDO> list : levelMap.values()) {
            list.sort(Comparator.comparingInt(EquipEnhanceLevelDO::getEnhanceLevel));
        }
        log.info("EquipEnhanceManager 缓存已刷新：总配置 {} 条，启用的 {} 条，等级 {} 条，消耗 {} 条",
                configs.size(), enabledCount, levels.size(), costs.size());
    }

    /**
     * 手动强制刷新缓存（从数据库重新加载）。
     * 适用于数据库配置变更后无需重启服务器的场景。
     * 脚本中可调用: {@code EquipEnhanceManager.reload()}
     */
    public static void reload() {
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                var configMapper = context.getBean(org.gms.dao.mapper.EquipEnhanceConfigMapper.class);
                var levelMapper = context.getBean(org.gms.dao.mapper.EquipEnhanceLevelMapper.class);
                var costMapper = context.getBean(org.gms.dao.mapper.EquipEnhanceCostMapper.class);
                load(configMapper.selectAll(), levelMapper.selectAll(), costMapper.selectAll());
            } else {
                log.warn("Spring 上下文不可用，无法重新加载装备强化配置");
            }
        } catch (Exception e) {
            log.error("重新加载装备强化配置失败", e);
        }
    }

    /**
     * 判断装备是否可强化
     * @param itemId 物品ID
     * @return 可强化返回true，否则返回false
     */
    public static boolean canEnhance(int itemId) {
        return configMap.containsKey(itemId);
    }

    /**
     * 获取装备强化配置
     * @param itemId 物品ID
     * @return 配置实体，不存在返回null
     */
    public static EquipEnhanceConfigDO getConfig(int itemId) {
        return configMap.get(itemId);
    }

    /**
     * 获取某配置的指定等级强化数据
     * @param configId 配置ID
     * @param enhanceLevel 强化等级
     * @return 强化等级实体，不存在返回null
     */
    public static EquipEnhanceLevelDO getLevel(Long configId, int enhanceLevel) {
        List<EquipEnhanceLevelDO> list = levelMap.get(configId);
        if (list == null) return null;
        for (EquipEnhanceLevelDO lv : list) {
            if (lv.getEnhanceLevel() == enhanceLevel) return lv;
        }
        return null;
    }

    /**
     * 获取某强化等级的道具消耗
     * @param levelId 等级ID
     * @return 消耗物品列表，无数据返回空列表
     */
    public static List<EquipEnhanceCostDO> getCosts(Long levelId) {
        return costMap.getOrDefault(levelId, Collections.emptyList());
    }

    /**
     * 获取配置的所有等级
     * @param configId 配置ID
     * @return 等级列表，无数据返回空列表
     */
    public static List<EquipEnhanceLevelDO> getAllLevels(Long configId) {
        return levelMap.getOrDefault(configId, Collections.emptyList());
    }

    /**
     * 获取所有已启用配置的Map（供脚本遍历）
     * @return 物品ID→配置实体映射
     */
    public static Map<Integer, EquipEnhanceConfigDO> getConfigMap() {
        return configMap;
    }
}