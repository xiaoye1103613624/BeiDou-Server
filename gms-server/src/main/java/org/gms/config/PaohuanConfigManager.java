package org.gms.config;

import com.mybatisflex.core.query.QueryWrapper;
import org.gms.dao.entity.PaohuanConfigDO;
import org.gms.dao.entity.PaohuanRewardDO;
import org.gms.dao.entity.PaohuanRingRewardDO;
import org.gms.dao.entity.WarehouseItemDO;
import org.gms.dao.mapper.PaohuanConfigMapper;
import org.gms.dao.mapper.PaohuanRewardMapper;
import org.gms.dao.mapper.PaohuanRingRewardMapper;
import org.gms.dao.mapper.WarehouseItemMapper;
import org.gms.manager.ServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 跑环配置的静态缓存管理器。
 * <p>
 * 供 GraalVM JS 脚本通过 {@code Java.type()} 调用查询跑环物品池和里程碑奖励。
 * 缓存启用的物品池列表和里程碑奖励映射。
 * </p>
 */
public class PaohuanConfigManager {

    private static final Logger log = LoggerFactory.getLogger(PaohuanConfigManager.class);

    /** 启用的物品池列表（按 sortOrder 排序） */
    private static final List<PaohuanConfigDO> enabledItems = new ArrayList<>();
    /** 环数 → 里程碑奖励列表映射 */
    private static final Map<Integer, List<PaohuanRewardDO>> rewardMap = new ConcurrentHashMap<>();
    /** 每环随机奖励池（启用且 weight>0 的项，按权重排序供随机选取） */
    private static final List<PaohuanRingRewardDO> ringRewards = new ArrayList<>();

    private PaohuanConfigManager() {}

    // ==================== 缓存管理 ====================

    /**
     * 加载配置数据到缓存。
     *
     * @param configs 物品池配置列表
     * @param rewards 里程碑奖励列表
     */
    public static synchronized void load(List<PaohuanConfigDO> configs,
                                         List<PaohuanRewardDO> rewards,
                                         List<PaohuanRingRewardDO> ringRwList) {
        enabledItems.clear();
        rewardMap.clear();
        ringRewards.clear();

        int enabledCount = 0;
        List<PaohuanConfigDO> enabled = new ArrayList<>();
        for (PaohuanConfigDO config : configs) {
            if (config.getEnabled() != null && config.getEnabled() == 1) {
                enabled.add(config);
                enabledCount++;
            }
        }
        enabled.sort(Comparator.comparingInt(c ->
                c.getSortOrder() != null ? c.getSortOrder() : 0));
        enabledItems.addAll(enabled);

        for (PaohuanRewardDO reward : rewards) {
            rewardMap.computeIfAbsent(reward.getRingCount(), k -> new ArrayList<>()).add(reward);
        }

        // 每环随机奖励池：仅保留启用且权重>0的
        for (PaohuanRingRewardDO rw : ringRwList) {
            if (rw.getEnabled() != null && rw.getEnabled() == 1 && rw.getWeight() != null && rw.getWeight() > 0) {
                ringRewards.add(rw);
            }
        }
        ringRewards.sort(Comparator.comparingInt(r -> r.getSortOrder() != null ? r.getSortOrder() : 0));

        log.info("PaohuanConfigManager 缓存已刷新：物品池 {} 条（启用 {}），里程碑奖励 {} 条，随机奖励 {} 条",
                configs.size(), enabledCount, rewards.size(), ringRewards.size());
    }

    /**
     * 手动强制刷新缓存（从数据库重新加载）。
     * 脚本中可调用: {@code PaohuanConfigManager.reload()}
     */
    public static void reload() {
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                var configMapper = context.getBean(PaohuanConfigMapper.class);
                var rewardMapper = context.getBean(PaohuanRewardMapper.class);
                var ringRewardMapper = context.getBean(PaohuanRingRewardMapper.class);
                load(configMapper.selectAll(), rewardMapper.selectAll(), ringRewardMapper.selectAll());
            } else {
                log.warn("Spring 上下文不可用，无法重新加载跑环配置");
            }
        } catch (Exception e) {
            log.error("重新加载跑环配置失败", e);
        }
    }

    // ==================== 查询方法 ====================

    /**
     * 获取启用的物品池列表。
     *
     * @return 物品池配置列表
     */
    public static List<PaohuanConfigDO> getEnabledItems() {
        return enabledItems;
    }

    /**
     * 获取指定环数的里程碑奖励。
     *
     * @param ringCount 环数
     * @return 奖励列表
     */
    public static List<PaohuanRewardDO> getRewardsByRing(int ringCount) {
        return rewardMap.getOrDefault(ringCount, Collections.emptyList());
    }

    /**
     * 获取每环随机奖励池（启用的，weight>0）。
     */
    public static List<PaohuanRingRewardDO> getRingRewards() {
        return ringRewards;
    }

    /**
     * 获取缓存统计信息（供脚本诊断用）。
     */
    public static Map<String, Integer> getCacheStats() {
        Map<String, Integer> stats = new LinkedHashMap<>();
        stats.put("itemCount", enabledItems.size());
        int totalRewards = 0;
        for (List<PaohuanRewardDO> list : rewardMap.values()) {
            totalRewards += list.size();
        }
        stats.put("milestoneCount", totalRewards);
        stats.put("ringRewardCount", ringRewards.size());
        return stats;
    }

    // ==================== 直连数据库查询（供脚本使用） ====================

    /**
     * 查询该账号已存入跑环仓库的物品及数量。
     * <p>
     * 只返回在跑环物品池白名单中的物品，按 itemId 升序排列。
     * 每项包含 itemId、quantity、inventoryType。
     * 脚本中可调用: {@code PaohuanConfigManager.queryDepositedItems(accountId)}
     * </p>
     *
     * @param accountId 账号ID
     * @return 已存放物品列表
     */
    public static List<Map<String, Object>> queryDepositedItems(int accountId) {
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                // 取启用的配置白名单，建成 itemId → config 映射
                var configMapper = context.getBean(PaohuanConfigMapper.class);
                List<PaohuanConfigDO> enabledConfigs = configMapper.selectListByQuery(
                        QueryWrapper.create()
                                .where("enabled = ?", 1)
                                .orderBy("item_id", true));
                Map<Integer, PaohuanConfigDO> configMap = new LinkedHashMap<>();
                for (PaohuanConfigDO c : enabledConfigs) {
                    configMap.put(c.getItemId(), c);
                }

                // 查询该账号的仓库物品记录
                var itemMapper = context.getBean(WarehouseItemMapper.class);
                List<WarehouseItemDO> storedItems = itemMapper.selectListByQuery(
                        QueryWrapper.create()
                                .where("account_id = ?", accountId));

                // 汇总数量并过滤白名单
                Map<Integer, Integer> qtyMap = new LinkedHashMap<>();
                Map<Integer, Integer> invTypeMap = new HashMap<>();
                for (WarehouseItemDO item : storedItems) {
                    int itemId = item.getItemId();
                    if (configMap.containsKey(itemId)) {
                        qtyMap.merge(itemId, item.getQuantity(), Integer::sum);
                        invTypeMap.putIfAbsent(itemId, item.getInventoryType());
                    }
                }

                // 按 itemId 升序输出
                List<Integer> sortedItemIds = new ArrayList<>(qtyMap.keySet());
                sortedItemIds.sort(Integer::compareTo);
                List<Map<String, Object>> result = new ArrayList<>();
                for (int itemId : sortedItemIds) {
                    Integer qty = qtyMap.get(itemId);
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("itemId", itemId);
                    item.put("quantity", qty);
                    item.put("inventoryType", invTypeMap.get(itemId));
                    result.add(item);
                }
                return result;
            }
        } catch (Exception e) {
            log.error("查询跑环仓库已存放物品失败", e);
        }
        return Collections.emptyList();
    }

    /**
     * 直接查询数据库获取启用的物品池列表（不走缓存）。
     *
     * @return 启用的物品池配置列表（按 sortOrder 排序）
     */
    public static List<PaohuanConfigDO> queryEnabledItems() {
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                var mapper = context.getBean(PaohuanConfigMapper.class);
                List<PaohuanConfigDO> list = mapper.selectListByQuery(
                        QueryWrapper.create()
                                .where("enabled = ?", 1)
                                .orderBy("sort_order", true));
                return list;
            }
        } catch (Exception e) {
            log.error("直连查询跑环物品池失败", e);
        }
        return Collections.emptyList();
    }

    /**
     * 直接查询数据库获取指定环数的里程碑奖励（不走缓存）。
     *
     * @param ringCount 环数
     * @return 奖励列表
     */
    public static List<PaohuanRewardDO> queryRewardsByRing(int ringCount) {
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                var mapper = context.getBean(PaohuanRewardMapper.class);
                return mapper.selectListByQuery(
                        QueryWrapper.create()
                                .where("ring_count = ?", ringCount)
                                .orderBy("sort_order", true));
            }
        } catch (Exception e) {
            log.error("直连查询跑环里程碑奖励失败", e);
        }
        return Collections.emptyList();
    }

    /**
     * 直接查询数据库获取每环随机奖励池（不走缓存，仅启用且 weight>0 的）。
     */
    public static List<PaohuanRingRewardDO> queryRingRewards() {
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                var mapper = context.getBean(PaohuanRingRewardMapper.class);
                return mapper.selectListByQuery(
                        QueryWrapper.create()
                                .where("enabled = ?", 1)
                                .and("weight > ?", 0)
                                .orderBy("sort_order", true));
            }
        } catch (Exception e) {
            log.error("直连查询跑环随机奖励池失败", e);
        }
        return Collections.emptyList();
    }

    // ==================== 游戏参数 ====================

    /**
     * 获取每日跑环上限。
     */
    public static int getDailyLimit() {
        return GameConfig.get("server", "Game Mechanics", "paohuan_daily_limit", 20);
    }

    /**
     * 获取每环基础经验。
     */
    public static int getExpPerRing() {
        return GameConfig.get("server", "Game Mechanics", "paohuan_exp_per_ring", 10000);
    }

    /**
     * 获取每环基础金币。
     */
    public static int getMesoPerRing() {
        return GameConfig.get("server", "Game Mechanics", "paohuan_meso_per_ring", 10000);
    }

    /**
     * 获取跑环VIP物品ID（拥有该物品的玩家开启跑环后可使用传送功能）。
     * 0 表示禁用传送功能。
     */
    public static int getVipItemId() {
        return GameConfig.get("server", "Game Mechanics", "paohuan_vip_item_id", 0);
    }

    /**
     * 根据物品ID查询跑环物品池中的掉落地图ID（供 JS 脚本传送使用）
     *
     * @param itemId 物品ID
     * @return 掉落地图ID（未配置或查询失败返回0）
     */
    public static int queryDropMap(int itemId) {
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                var mapper = context.getBean(PaohuanConfigMapper.class);
                List<PaohuanConfigDO> list = mapper.selectListByQuery(
                        QueryWrapper.create()
                                .where("item_id = ?", itemId)
                                .and("enabled = ?", 1)
                                .limit(1));
                if (!list.isEmpty() && list.get(0).getDropMapId() != null) {
                    return list.get(0).getDropMapId();
                }
            }
        } catch (Exception e) {
            log.error("查询物品 {} 的掉落地图失败", itemId, e);
        }
        return 0;
    }
}
