package org.gms.config;

import org.gms.dao.entity.ScrollDecomposeConfigDO;
import org.gms.dao.entity.ScrollExchangeConfigDO;
import org.gms.dao.mapper.ScrollDecomposeConfigMapper;
import org.gms.dao.mapper.ScrollExchangeConfigMapper;
import org.gms.manager.ServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 卷轴分解/兑换配置的静态缓存管理器。
 * <p>
 * 供 GraalVM JS 脚本通过 {@code Java.type()} 调用，查询哪些卷轴可被分解、
 * 哪些卷轴可用碎片兑换及其价格。
 * </p>
 */
public class ScrollDecomposeManager {

    private static final Logger log = LoggerFactory.getLogger(ScrollDecomposeManager.class);

    /** 可分解卷轴ID集合（仅启用的配置） */
    private static final Set<Integer> enabledDecomposeIds = ConcurrentHashMap.newKeySet();

    /** 可兑换卷轴 → 所需碎片数量（仅启用的配置） */
    private static final Map<Integer, Integer> exchangeCostMap = new ConcurrentHashMap<>();

    /** 所有启用的分解配置列表（按sortOrder排序） */
    private static final List<ScrollDecomposeConfigDO> enabledDecomposeList = new ArrayList<>();

    /** 所有启用的兑换配置列表（按cost升序排序） */
    private static final List<ScrollExchangeConfigDO> enabledExchangeList = new ArrayList<>();

    private ScrollDecomposeManager() {}

    // ==================== 缓存管理 ====================

    /**
     * 加载分解和兑换配置数据到缓存。
     *
     * @param decomposeConfigs 卷轴分解配置 DO 列表
     * @param exchangeConfigs 卷轴兑换配置 DO 列表
     */
    public static synchronized void load(List<ScrollDecomposeConfigDO> decomposeConfigs,
                                         List<ScrollExchangeConfigDO> exchangeConfigs) {
        // 加载分解配置
        enabledDecomposeIds.clear();
        enabledDecomposeList.clear();
        int decomposeCount = 0;
        for (ScrollDecomposeConfigDO config : decomposeConfigs) {
            if (config.getEnabled() != null && config.getEnabled() == 1) {
                enabledDecomposeIds.add(config.getScrollId());
                enabledDecomposeList.add(config);
                decomposeCount++;
            }
        }
        enabledDecomposeList.sort(Comparator
                .comparingInt((ScrollDecomposeConfigDO c) -> c.getSortOrder() != null ? c.getSortOrder() : 200)
                .thenComparingInt(ScrollDecomposeConfigDO::getScrollId));

        // 加载兑换配置
        exchangeCostMap.clear();
        enabledExchangeList.clear();
        int exchangeCount = 0;
        for (ScrollExchangeConfigDO config : exchangeConfigs) {
            if (config.getEnabled() != null && config.getEnabled() == 1) {
                exchangeCostMap.put(config.getScrollId(), config.getCost());
                enabledExchangeList.add(config);
                exchangeCount++;
            }
        }
        enabledExchangeList.sort(Comparator
                .comparingInt((ScrollExchangeConfigDO c) -> c.getCost() != null ? c.getCost() : 0)
                .thenComparingInt(c -> c.getSortOrder() != null ? c.getSortOrder() : 200)
                .thenComparingInt(ScrollExchangeConfigDO::getScrollId));

        log.info("ScrollDecomposeManager 缓存已刷新：分解配置 {} 条（启用 {}），兑换配置 {} 条（启用 {}）",
                decomposeConfigs.size(), decomposeCount, exchangeConfigs.size(), exchangeCount);
    }

    /**
     * 手动强制刷新缓存（从数据库重新加载）。
     * 脚本中可调用: {@code ScrollDecomposeManager.reload()}
     */
    public static void reload() {
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                var decomposeMapper = context.getBean(ScrollDecomposeConfigMapper.class);
                var exchangeMapper = context.getBean(ScrollExchangeConfigMapper.class);
                load(decomposeMapper.selectAll(), exchangeMapper.selectAll());
            } else {
                log.warn("Spring 上下文不可用，无法重新加载卷轴分解配置");
            }
        } catch (Exception e) {
            log.error("重新加载卷轴分解配置失败", e);
        }
    }

    // ==================== 分解查询方法 ====================

    /**
     * 检查卷轴是否可被分解（在白名单中且已启用）。
     * 脚本中可调用: {@code ScrollDecomposeManager.canDecompose(itemId)}
     *
     * @param scrollId 卷轴物品ID
     * @return 可以分解返回 true
     */
    public static boolean canDecompose(int scrollId) {
        return enabledDecomposeIds.contains(scrollId);
    }

    /**
     * 获取所有可分解的卷轴ID集合（供脚本遍历使用）。
     *
     * @return 已启用的分解卷轴ID集合
     */
    public static Set<Integer> getEnabledDecomposeIds() {
        return enabledDecomposeIds;
    }

    /**
     * 获取所有已启用的分解配置列表（按sortOrder排序）。
     *
     * @return 分解配置列表
     */
    public static List<ScrollDecomposeConfigDO> getEnabledDecomposeList() {
        return enabledDecomposeList;
    }

    // ==================== 兑换查询方法 ====================

    /**
     * 获取兑换指定卷轴所需的碎片数量。
     * 脚本中可调用: {@code ScrollDecomposeManager.getExchangeCost(scrollId)}
     *
     * @param scrollId 卷轴物品ID
     * @return 所需碎片数量（未配置返回0）
     */
    public static int getExchangeCost(int scrollId) {
        return exchangeCostMap.getOrDefault(scrollId, 0);
    }

    /**
     * 获取所有已启用的兑换配置列表（按cost升序排序）。
     * 脚本中调用此方法获取完整的兑换列表，替代硬编码 EXCHANGE_LIST。
     *
     * @return 兑换配置列表
     */
    public static List<ScrollExchangeConfigDO> getEnabledExchangeList() {
        return enabledExchangeList;
    }

    /**
     * 检查卷轴是否可被兑换（在兑换白名单中且已启用）。
     *
     * @param scrollId 卷轴物品ID
     * @return 可兑换返回 true
     */
    public static boolean canExchange(int scrollId) {
        return exchangeCostMap.containsKey(scrollId);
    }

    // ==================== 缓存统计 ====================

    /**
     * 获取缓存统计信息（供脚本诊断用）。
     *
     * @return 包含 decomposeCount/exchangeCount 的 Map
     */
    public static Map<String, Integer> getCacheStats() {
        Map<String, Integer> stats = new LinkedHashMap<>();
        stats.put("decomposeCount", enabledDecomposeIds.size());
        stats.put("exchangeCount", exchangeCostMap.size());
        return stats;
    }
}
