package org.gms.config;

import com.mybatisflex.core.query.QueryWrapper;
import org.gms.dao.entity.DailyBossConfigDO;
import org.gms.dao.entity.DailyBossRewardDO;
import org.gms.dao.mapper.DailyBossConfigMapper;
import org.gms.dao.mapper.DailyBossRewardMapper;
import org.gms.manager.ServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 每日Boss配置的静态缓存管理器。
 * 供 GraalVM JS 脚本通过 {@code Java.type()} 调用查询配置。
 */
public class DailyBossConfigManager {

    private static final Logger log = LoggerFactory.getLogger(DailyBossConfigManager.class);

    /** 配置ID → 配置DO（仅启用） */
    private static final Map<Long, DailyBossConfigDO> configMap = new ConcurrentHashMap<>();
    /** 配置ID → 奖励列表 */
    private static final Map<Long, List<DailyBossRewardDO>> rewardMap = new ConcurrentHashMap<>();
    /** 所有启用的配置（按排序升序） */
    private static final List<DailyBossConfigDO> enabledConfigs = new ArrayList<>();

    private DailyBossConfigManager() {}

    /**
     * 加载配置到内存缓存
     */
    public static synchronized void load(List<DailyBossConfigDO> configs,
                                         List<DailyBossRewardDO> rewards) {
        configMap.clear();
        rewardMap.clear();
        enabledConfigs.clear();

        List<DailyBossConfigDO> enabled = new ArrayList<>();
        for (DailyBossConfigDO c : configs) {
            if (c.getEnabled() != null && c.getEnabled() == 1) {
                configMap.put(c.getId(), c);
                enabled.add(c);
            }
        }
        enabled.sort(Comparator.comparingInt(DailyBossConfigDO::getSortOrder));
        enabledConfigs.addAll(enabled);

        for (DailyBossRewardDO r : rewards) {
            rewardMap.computeIfAbsent(r.getConfigId(), k -> new ArrayList<>()).add(r);
        }

        log.info("DailyBossConfigManager 缓存已刷新：配置 {} 条，奖励 {} 条",
                enabled.size(), rewards.size());
    }

    /**
     * 直连数据库查询所有启用的配置（供 JS 脚本使用）
     */
    public static List<DailyBossConfigDO> queryEnabledConfigs() {
        try {
            var ctx = ServerManager.getApplicationContext();
            if (ctx != null) {
                var mapper = ctx.getBean(DailyBossConfigMapper.class);
                List<DailyBossConfigDO> list = mapper.selectListByQuery(
                        QueryWrapper.create().where("enabled = ?", 1));
                list.sort(Comparator.comparingInt(DailyBossConfigDO::getSortOrder));
                return list;
            }
        } catch (Exception e) {
            log.error("直连查询每日Boss配置失败", e);
        }
        return Collections.emptyList();
    }

    /**
     * 直连数据库查询某配置的奖励列表（供 JS 脚本使用）
     */
    public static List<DailyBossRewardDO> queryRewards(Long configId) {
        try {
            var ctx = ServerManager.getApplicationContext();
            if (ctx != null) {
                var mapper = ctx.getBean(DailyBossRewardMapper.class);
                return mapper.selectListByQuery(
                        QueryWrapper.create().where("config_id = ?", configId));
            }
        } catch (Exception e) {
            log.error("直连查询每日Boss奖励失败", e);
        }
        return Collections.emptyList();
    }

    // ==================== 环式系统游戏参数 ====================

    /** 系统开关：0=旧里程碑系统, 1=新环式系统 */
    public static int getBossRingEnabled() {
        return GameConfig.get("server", "Game Mechanics", "boss_ring_enabled", 0);
    }

    /** 每日总环数 */
    public static int getDailyLimit() {
        return GameConfig.get("server", "Game Mechanics", "boss_ring_daily_limit", 5);
    }

    /** 每环基础经验（EXP = 环数 × 此值） */
    public static int getExpBase() {
        return GameConfig.get("server", "Game Mechanics", "boss_ring_exp_base", 5000);
    }

    /** 每环基础金币（Meso = 环数 × 此值） */
    public static int getMesoBase() {
        return GameConfig.get("server", "Game Mechanics", "boss_ring_meso_base", 5000);
    }

    /** 随机最少击杀数 */
    public static int getKillMin() {
        return GameConfig.get("server", "Game Mechanics", "boss_ring_kill_min", 3);
    }

    /** 随机最多击杀数 */
    public static int getKillMax() {
        return GameConfig.get("server", "Game Mechanics", "boss_ring_kill_max", 8);
    }

    /** 放弃任务手续费 */
    public static int getAbandonFee() {
        return GameConfig.get("server", "Game Mechanics", "boss_ring_abandon_fee", 50000);
    }

    /** 全部环完成奖励物品ID */
    public static int getFinalItemId() {
        return GameConfig.get("server", "Game Mechanics", "boss_ring_final_item_id", 4000048);
    }

    /** 全部环完成奖励数量 */
    public static int getFinalItemQty() {
        return GameConfig.get("server", "Game Mechanics", "boss_ring_final_item_qty", 5);
    }

    /** 里程碑奖励JSON */
    public static String getMilestoneRewardsJson() {
        return GameConfig.get("server", "Game Mechanics", "boss_ring_milestone_rewards", "[]");
    }

    /** 每环随机奖励池JSON */
    public static String getRandomRewardsJson() {
        return GameConfig.get("server", "Game Mechanics", "boss_ring_random_rewards", "[]");
    }

    /** 缓存统计（供诊断用） */
    public static Map<String, Integer> getCacheStats() {
        Map<String, Integer> stats = new LinkedHashMap<>();
        stats.put("configCount", configMap.size());
        stats.put("rewardGroupCount", rewardMap.size());
        stats.put("enabledCount", enabledConfigs.size());
        return stats;
    }
}
