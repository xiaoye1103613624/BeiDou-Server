package org.gms.config;

import com.mybatisflex.core.query.QueryWrapper;
import org.gms.dao.entity.DailyExploreFinalRewardDO;
import org.gms.dao.entity.DailyExploreMapDO;
import org.gms.dao.entity.DailyExploreRewardDO;
import org.gms.dao.mapper.DailyExploreFinalRewardMapper;
import org.gms.dao.mapper.DailyExploreMapMapper;
import org.gms.dao.mapper.DailyExploreRewardMapper;
import org.gms.manager.ServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 每日探索配置的静态缓存管理器。
 * <p>
 * 供 GraalVM JS 脚本通过 {@code Java.type()} 调用查询地图池和奖励。
 * 缓存启用的地图池列表、每轮随机奖励、完成奖励映射。
 * </p>
 */
public class DailyExploreConfigManager {

    private static final Logger log = LoggerFactory.getLogger(DailyExploreConfigManager.class);

    /** 启用的地图池列表（按 sortOrder 排序） */
    private static final List<DailyExploreMapDO> enabledMaps = new ArrayList<>();
    /** 启用的每轮随机奖励列表（weight>0，按 sortOrder 排序） */
    private static final List<DailyExploreRewardDO> enabledRewards = new ArrayList<>();
    /** 探索次数 → 完成奖励列表映射 */
    private static final Map<Integer, List<DailyExploreFinalRewardDO>> finalRewardMap = new ConcurrentHashMap<>();

    private DailyExploreConfigManager() {}

    // ==================== 缓存管理 ====================

    /**
     * 加载配置数据到缓存。
     *
     * @param maps     地图池列表
     * @param rewards  每轮随机奖励列表
     * @param finalRewards 完成奖励列表
     */
    public static synchronized void load(List<DailyExploreMapDO> maps,
                                         List<DailyExploreRewardDO> rewards,
                                         List<DailyExploreFinalRewardDO> finalRewards) {
        enabledMaps.clear();
        enabledRewards.clear();
        finalRewardMap.clear();

        int mapCount = 0;
        List<DailyExploreMapDO> enabled = new ArrayList<>();
        for (DailyExploreMapDO map : maps) {
            if (map.getEnabled() != null && map.getEnabled() == 1) {
                enabled.add(map);
                mapCount++;
            }
        }
        enabled.sort(Comparator.comparingInt(m ->
                m.getSortOrder() != null ? m.getSortOrder() : 0));
        enabledMaps.addAll(enabled);

        int rewardCount = 0;
        List<DailyExploreRewardDO> enabledRw = new ArrayList<>();
        for (DailyExploreRewardDO rw : rewards) {
            if (rw.getEnabled() != null && rw.getEnabled() == 1
                    && rw.getWeight() != null && rw.getWeight() > 0) {
                enabledRw.add(rw);
                rewardCount++;
            }
        }
        enabledRw.sort(Comparator.comparingInt(r ->
                r.getSortOrder() != null ? r.getSortOrder() : 0));
        enabledRewards.addAll(enabledRw);

        for (DailyExploreFinalRewardDO fr : finalRewards) {
            finalRewardMap.computeIfAbsent(fr.getExploreCount(), k -> new ArrayList<>()).add(fr);
        }

        log.info("DailyExploreConfigManager 缓存已刷新：地图池 {} 条（启用 {}），每轮奖励 {} 条（启用 {}），完成奖励 {} 条",
                maps.size(), mapCount, rewards.size(), rewardCount, finalRewards.size());
    }

    /**
     * 手动强制刷新缓存（从数据库重新加载）。
     * 脚本中可调用: {@code DailyExploreConfigManager.reload()}
     */
    public static void reload() {
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                var mapMapper = context.getBean(DailyExploreMapMapper.class);
                var rewardMapper = context.getBean(DailyExploreRewardMapper.class);
                var finalRewardMapper = context.getBean(DailyExploreFinalRewardMapper.class);
                load(mapMapper.selectAll(), rewardMapper.selectAll(), finalRewardMapper.selectAll());
            } else {
                log.warn("Spring 上下文不可用，无法重新加载每日探索配置");
            }
        } catch (Exception e) {
            log.error("重新加载每日探索配置失败", e);
        }
    }

    // ==================== 缓存查询 ====================

    /** 获取启用的地图池列表 */
    public static List<DailyExploreMapDO> getEnabledMaps() {
        return enabledMaps;
    }

    /** 获取启用的每轮随机奖励列表 */
    public static List<DailyExploreRewardDO> getRingRewards() {
        return enabledRewards;
    }

    /** 获取指定探索次数的完成奖励 */
    public static List<DailyExploreFinalRewardDO> getRewardsByExploreCount(int exploreCount) {
        return finalRewardMap.getOrDefault(exploreCount, Collections.emptyList());
    }

    /** 获取缓存统计信息 */
    public static Map<String, Integer> getCacheStats() {
        Map<String, Integer> stats = new LinkedHashMap<>();
        stats.put("mapCount", enabledMaps.size());
        stats.put("rewardCount", enabledRewards.size());
        int totalFinal = 0;
        for (List<DailyExploreFinalRewardDO> list : finalRewardMap.values()) {
            totalFinal += list.size();
        }
        stats.put("finalRewardCount", totalFinal);
        return stats;
    }

    // ==================== 直连数据库查询（供脚本使用） ====================

    /** 直连查询启用的地图池（不走缓存） */
    public static List<DailyExploreMapDO> queryEnabledMaps() {
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                var mapper = context.getBean(DailyExploreMapMapper.class);
                return mapper.selectListByQuery(
                        QueryWrapper.create()
                                .where("enabled = ?", 1)
                                .orderBy("sort_order", true));
            }
        } catch (Exception e) {
            log.error("直连查询每日探索地图池失败", e);
        }
        return Collections.emptyList();
    }

    /** 直连查询启用的每轮随机奖励（不走缓存） */
    public static List<DailyExploreRewardDO> queryRingRewards() {
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                var mapper = context.getBean(DailyExploreRewardMapper.class);
                return mapper.selectListByQuery(
                        QueryWrapper.create()
                                .where("enabled = ?", 1)
                                .and("weight > ?", 0)
                                .orderBy("sort_order", true));
            }
        } catch (Exception e) {
            log.error("直连查询每日探索随机奖励失败", e);
        }
        return Collections.emptyList();
    }

    /** 直连查询指定探索次数的完成奖励（不走缓存） */
    public static List<DailyExploreFinalRewardDO> queryRewardsByExploreCount(int exploreCount) {
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                var mapper = context.getBean(DailyExploreFinalRewardMapper.class);
                return mapper.selectListByQuery(
                        QueryWrapper.create()
                                .where("explore_count = ?", exploreCount)
                                .orderBy("sort_order", true));
            }
        } catch (Exception e) {
            log.error("直连查询每日探索完成奖励失败", e);
        }
        return Collections.emptyList();
    }

    // ==================== 游戏参数 ====================

    /** 获取每日探索上限 */
    public static int getDailyLimit() {
        return GameConfig.get("server", "Game Mechanics", "daily_explore_limit", 10);
    }
}
