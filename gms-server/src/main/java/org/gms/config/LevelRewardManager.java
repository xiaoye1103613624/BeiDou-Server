package org.gms.config;

import com.mybatisflex.core.query.QueryWrapper;
import org.gms.dao.entity.LevelRewardDO;
import org.gms.dao.entity.LevelRewardItemDO;
import org.gms.dao.mapper.LevelRewardItemMapper;
import org.gms.dao.mapper.LevelRewardMapper;
import org.gms.manager.ServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 等级奖励配置的静态缓存管理器。
 * <p>
 * 供 GraalVM JS 脚本通过 {@code Java.type()} 调用查询等级奖励规则。
 * 提供按等级查询奖励配置和道具列表的功能。
 * </p>
 */
public class LevelRewardManager {

    private static final Logger log = LoggerFactory.getLogger(LevelRewardManager.class);

    /** 等级 → 奖励配置映射（仅包含启用的配置） */
    private static final Map<Integer, LevelRewardDO> rewardMap = new ConcurrentHashMap<>();
    /** 奖励配置ID → 道具列表映射 */
    private static final Map<Long, List<LevelRewardItemDO>> itemMap = new ConcurrentHashMap<>();
    /** 所有奖励配置列表（按等级排序，仅启用） */
    private static final List<LevelRewardDO> enabledRewards = new ArrayList<>();

    private LevelRewardManager() {}

    // ==================== 缓存管理 ====================

    /**
     * 加载配置数据到缓存
     * @param rewards 奖励配置DO列表
     * @param items   道具DO列表
     */
    public static synchronized void load(List<LevelRewardDO> rewards,
                                         List<LevelRewardItemDO> items) {
        rewardMap.clear();
        itemMap.clear();
        enabledRewards.clear();

        int enabledCount = 0;
        List<LevelRewardDO> enabled = new ArrayList<>();
        for (LevelRewardDO r : rewards) {
            if (r.getEnabled() != null && r.getEnabled() == 1) {
                rewardMap.put(r.getLevel(), r);
                enabled.add(r);
                enabledCount++;
            }
        }
        // 按等级升序
        enabled.sort(Comparator.comparingInt(LevelRewardDO::getLevel));
        enabledRewards.addAll(enabled);

        for (LevelRewardItemDO item : items) {
            itemMap.computeIfAbsent(item.getRewardId(), k -> new ArrayList<>()).add(item);
        }

        log.info("LevelRewardManager 缓存已刷新：总配置 {} 条，启用的 {} 条，道具 {} 条",
                rewards.size(), enabledCount, items.size());
    }

    /**
     * 手动强制刷新缓存（从数据库重新加载）。
     * 脚本中可调用: {@code LevelRewardManager.reload()}
     */
    public static void reload() {
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                var rewardMapper = context.getBean(LevelRewardMapper.class);
                var itemMapper = context.getBean(LevelRewardItemMapper.class);
                load(rewardMapper.selectAll(), itemMapper.selectAll());
            } else {
                log.warn("Spring 上下文不可用，无法重新加载等级奖励配置");
            }
        } catch (Exception e) {
            log.error("重新加载等级奖励配置失败", e);
        }
    }

    // ==================== 查询方法 ====================

    /** 按等级获取奖励配置（仅启用状态） */
    public static LevelRewardDO getRewardByLevel(int level) {
        return rewardMap.get(level);
    }

    /** 获取指定奖励配置的道具列表 */
    public static List<LevelRewardItemDO> getRewardItems(Long rewardId) {
        return itemMap.getOrDefault(rewardId, Collections.emptyList());
    }

    /** 获取所有已启用的奖励配置（按等级升序） */
    public static List<LevelRewardDO> getEnabledRewards() {
        return enabledRewards;
    }

    /** 判断某等级是否存在奖励配置 */
    public static boolean hasReward(int level) {
        return rewardMap.containsKey(level);
    }

    /**
     * 获取缓存统计信息（供脚本诊断用）。
     * @return 包含 rewardCount/itemCount 的 Map
     */
    public static Map<String, Integer> getCacheStats() {
        Map<String, Integer> stats = new LinkedHashMap<>();
        stats.put("rewardCount", rewardMap.size());
        int totalItems = 0;
        for (List<LevelRewardItemDO> list : itemMap.values()) {
            totalItems += list.size();
        }
        stats.put("itemCount", totalItems);
        return stats;
    }

    // ==================== 直连数据库查询（供脚本绕过缓存使用） ====================

    /**
     * 直接查询数据库获取所有启用的等级奖励配置（不走缓存）。
     * <p>
     * 由于 GraalVM JS 的 Context 可能使用独立 ClassLoader，
     * 脚本中通过 {@code Java.type()} 加载的类与 Spring 容器的类不共享 static 字段。
     * 此方法绕过 static 缓存，直接通过 JDBC 查询数据库，
     * 确保脚本始终能获取到最新数据。
     * </p>
     *
     * @return 启用状态（enabled=1）的等级奖励配置列表（按等级升序）
     */
    public static List<LevelRewardDO> queryEnabledRewards() {
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                var mapper = context.getBean(LevelRewardMapper.class);
                List<LevelRewardDO> list = mapper.selectListByQuery(
                        QueryWrapper.create().where("enabled = ?", 1));
                list.sort(Comparator.comparingInt(LevelRewardDO::getLevel));
                return list;
            }
        } catch (Exception e) {
            log.error("直连查询等级奖励配置失败", e);
        }
        return Collections.emptyList();
    }

    /**
     * 直接查询数据库获取某奖励配置的道具列表（不走缓存）。
     *
     * @param rewardId 奖励配置ID
     * @return 道具列表
     */
    public static List<LevelRewardItemDO> queryRewardItems(Long rewardId) {
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                var mapper = context.getBean(LevelRewardItemMapper.class);
                return mapper.selectListByQuery(
                        QueryWrapper.create().where("reward_id = ?", rewardId));
            }
        } catch (Exception e) {
            log.error("直连查询等级奖励道具失败", e);
        }
        return Collections.emptyList();
    }
}
