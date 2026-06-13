package org.gms.config;

import com.mybatisflex.core.query.QueryWrapper;
import org.gms.dao.entity.DailyDungeonConfigDO;
import org.gms.dao.entity.DailyDungeonDailyRewardDO;
import org.gms.dao.entity.DailyDungeonRewardDO;
import org.gms.dao.entity.DailyDungeonVipConfigDO;
import org.gms.dao.mapper.DailyDungeonConfigMapper;
import org.gms.dao.mapper.DailyDungeonDailyRewardMapper;
import org.gms.dao.mapper.DailyDungeonRewardMapper;
import org.gms.dao.mapper.DailyDungeonVipConfigMapper;
import org.gms.manager.ServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 每日副本配置的静态缓存管理器。
 * 供 GraalVM JS 脚本通过 {@code Java.type()} 调用查询配置。
 */
public class DailyDungeonConfigManager {

    private static final Logger log = LoggerFactory.getLogger(DailyDungeonConfigManager.class);

    /** 配置ID → 配置DO（仅启用） */
    private static final Map<Long, DailyDungeonConfigDO> configMap = new ConcurrentHashMap<>();
    /** 配置ID → 奖励列表 */
    private static final Map<Long, List<DailyDungeonRewardDO>> rewardMap = new ConcurrentHashMap<>();
    /** 所有启用的配置（按排序升序） */
    private static final List<DailyDungeonConfigDO> enabledConfigs = new ArrayList<>();
    /** 每日完成奖励列表（所有副本完成后可领取） */
    private static final List<DailyDungeonDailyRewardDO> dailyRewards = new ArrayList<>();
    /** VIP物品配置列表（已启用） */
    private static final List<DailyDungeonVipConfigDO> vipConfigs = new ArrayList<>();

    private DailyDungeonConfigManager() {}

    /**
     * 加载配置到内存缓存
     */
    public static synchronized void load(List<DailyDungeonConfigDO> configs,
                                         List<DailyDungeonRewardDO> rewards,
                                         List<DailyDungeonDailyRewardDO> dailyRewardList,
                                         List<DailyDungeonVipConfigDO> vipConfigList) {
        configMap.clear();
        rewardMap.clear();
        enabledConfigs.clear();
        dailyRewards.clear();
        vipConfigs.clear();

        List<DailyDungeonConfigDO> enabled = new ArrayList<>();
        for (DailyDungeonConfigDO c : configs) {
            if (c.getEnabled() != null && c.getEnabled() == 1) {
                configMap.put(c.getId(), c);
                enabled.add(c);
            }
        }
        enabled.sort(Comparator.comparingInt(DailyDungeonConfigDO::getSortOrder));
        enabledConfigs.addAll(enabled);

        for (DailyDungeonRewardDO r : rewards) {
            rewardMap.computeIfAbsent(r.getConfigId(), k -> new ArrayList<>()).add(r);
        }

        // 每日完成奖励（按排序）
        dailyRewards.addAll(dailyRewardList);
        dailyRewards.sort(Comparator.comparingInt(DailyDungeonDailyRewardDO::getSortOrder));

        // VIP物品配置（仅启用，按排序）
        for (DailyDungeonVipConfigDO v : vipConfigList) {
            if (v.getEnabled() != null && v.getEnabled() == 1) {
                vipConfigs.add(v);
            }
        }
        vipConfigs.sort(Comparator.comparingInt(DailyDungeonVipConfigDO::getSortOrder));

        log.info("DailyDungeonConfigManager 缓存已刷新：配置 {} 条，奖励 {} 条，每日奖励 {} 条，VIP配置 {} 条",
                enabled.size(), rewards.size(), dailyRewards.size(), vipConfigs.size());
    }

    /**
     * 直连数据库查询所有启用的配置（供 JS 脚本使用，绕过 static 缓存）
     */
    public static List<DailyDungeonConfigDO> queryEnabledConfigs() {
        try {
            var ctx = ServerManager.getApplicationContext();
            if (ctx != null) {
                var mapper = ctx.getBean(DailyDungeonConfigMapper.class);
                List<DailyDungeonConfigDO> list = mapper.selectListByQuery(
                        QueryWrapper.create().where("enabled = ?", 1));
                list.sort(Comparator.comparingInt(DailyDungeonConfigDO::getSortOrder));
                return list;
            }
        } catch (Exception e) {
            log.error("直连查询每日副本配置失败", e);
        }
        return Collections.emptyList();
    }

    /**
     * 直连数据库查询某配置的奖励列表（供 JS 脚本使用）
     */
    public static List<DailyDungeonRewardDO> queryRewards(Long configId) {
        try {
            var ctx = ServerManager.getApplicationContext();
            if (ctx != null) {
                var mapper = ctx.getBean(DailyDungeonRewardMapper.class);
                return mapper.selectListByQuery(
                        QueryWrapper.create().where("config_id = ?", configId));
            }
        } catch (Exception e) {
            log.error("直连查询每日副本奖励失败", e);
        }
        return Collections.emptyList();
    }

    /**
     * 直连数据库查询每日完成奖励列表（供 JS 脚本使用）
     */
    public static List<DailyDungeonDailyRewardDO> queryDailyRewards() {
        try {
            var ctx = ServerManager.getApplicationContext();
            if (ctx != null) {
                var mapper = ctx.getBean(DailyDungeonDailyRewardMapper.class);
                List<DailyDungeonDailyRewardDO> list = mapper.selectAll();
                list.sort(Comparator.comparingInt(DailyDungeonDailyRewardDO::getSortOrder));
                return list;
            }
        } catch (Exception e) {
            log.error("直连查询每日完成奖励失败", e);
        }
        return Collections.emptyList();
    }

    /**
     * 直连数据库查询VIP物品配置列表（供 JS 脚本使用）
     */
    public static List<DailyDungeonVipConfigDO> queryVipConfigs() {
        try {
            var ctx = ServerManager.getApplicationContext();
            if (ctx != null) {
                var mapper = ctx.getBean(DailyDungeonVipConfigMapper.class);
                List<DailyDungeonVipConfigDO> list = mapper.selectListByQuery(
                        QueryWrapper.create().where("enabled = ?", 1));
                list.sort(Comparator.comparingInt(DailyDungeonVipConfigDO::getSortOrder));
                return list;
            }
        } catch (Exception e) {
            log.error("直连查询VIP物品配置失败", e);
        }
        return Collections.emptyList();
    }

    /**
     * 获取扫荡轮数（VIP玩家一键完成所有副本的轮数，0=禁用）
     */
    public static int getSweepRounds() {
        return GameConfig.get("server", "Game Mechanics", "daily_dungeon_sweep_rounds", 0);
    }
}
