package org.gms.config;

import com.mybatisflex.core.query.QueryWrapper;
import org.gms.dao.entity.WarehouseConfigDO;
import org.gms.dao.entity.WarehouseItemDO;
import org.gms.dao.mapper.WarehouseConfigMapper;
import org.gms.dao.mapper.WarehouseItemMapper;
import org.gms.manager.ServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 仓库配置的静态缓存管理器。
 * <p>
 * 供 GraalVM JS 脚本通过 {@code Java.type()} 调用查询仓库配置和物品存取。
 * 缓存白名单物品ID集合和分类配置映射，提供仓库物品的存取操作方法。
 * </p>
 */
public class WarehouseManager {

    private static final Logger log = LoggerFactory.getLogger(WarehouseManager.class);

    /** 白名单物品ID集合（仅启用的配置） */
    private static final Set<Integer> allowedItemIds = ConcurrentHashMap.newKeySet();
    /** 物品栏类型 → 配置列表映射 */
    private static final Map<Integer, List<WarehouseConfigDO>> typeConfigMap = new ConcurrentHashMap<>();
    /** 所有启用的配置列表 */
    private static final List<WarehouseConfigDO> enabledConfigs = new ArrayList<>();

    private WarehouseManager() {}

    // ==================== 缓存管理 ====================

    /**
     * 加载配置数据到缓存。
     *
     * @param configs 仓库配置 DO 列表
     */
    public static synchronized void load(List<WarehouseConfigDO> configs) {
        allowedItemIds.clear();
        typeConfigMap.clear();
        enabledConfigs.clear();

        int enabledCount = 0;
        for (WarehouseConfigDO config : configs) {
            if (config.getEnabled() != null && config.getEnabled() == 1) {
                allowedItemIds.add(config.getItemId());
                typeConfigMap.computeIfAbsent(config.getInventoryType(), k -> new ArrayList<>()).add(config);
                enabledConfigs.add(config);
                enabledCount++;
            }
        }

        // 按 sortOrder 升序排序（然后按 itemId）
        Comparator<WarehouseConfigDO> sortComparator = Comparator
                .comparingInt((WarehouseConfigDO c) -> c.getSortOrder() != null ? c.getSortOrder() : 200)
                .thenComparingInt(WarehouseConfigDO::getItemId);
        typeConfigMap.values().forEach(list -> list.sort(sortComparator));
        enabledConfigs.sort(sortComparator);

        log.info("WarehouseManager 缓存已刷新：总配置 {} 条，启用的 {} 条", configs.size(), enabledCount);
    }

    /**
     * 手动强制刷新缓存（从数据库重新加载）。
     * 脚本中可调用: {@code WarehouseManager.reload()}
     */
    public static void reload() {
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                var configMapper = context.getBean(WarehouseConfigMapper.class);
                load(configMapper.selectAll());
            } else {
                log.warn("Spring 上下文不可用，无法重新加载仓库配置");
            }
        } catch (Exception e) {
            log.error("重新加载仓库配置失败", e);
        }
    }

    // ==================== 配置查询方法 ====================

    /**
     * 检查物品是否在仓库白名单中且已启用。
     *
     * @param itemId 物品ID
     * @return 可以存放返回 true
     */
    public static boolean canStoreItem(int itemId) {
        return allowedItemIds.contains(itemId);
    }

    /**
     * 获取指定分类的配置列表。
     *
     * @param inventoryType 物品栏类型(1=装备 2=消耗 3=设置 4=其他 5=现金)
     * @return 配置列表
     */
    public static List<WarehouseConfigDO> getConfigByType(int inventoryType) {
        return typeConfigMap.getOrDefault(inventoryType, Collections.emptyList());
    }

    /**
     * 获取所有已启用的配置。
     *
     * @return 配置列表
     */
    public static List<WarehouseConfigDO> getEnabledConfigs() {
        return enabledConfigs;
    }

    // ==================== 游戏参数 ====================

    /**
     * 获取仓库可叠加物品的最大存放数量。
     *
     * @return 最大堆叠数量，默认 30000
     */
    public static int getMaxStack() {
        return GameConfig.get("server", "Game Mechanics", "warehouse_max_stack", 30000);
    }

    /**
     * 检查仓库是否为账号共享模式。
     * 脚本中可调用: {@code WarehouseManager.isAccountShared()}
     *
     * @return 账号共享返回 true
     */
    public static boolean isAccountShared() {
        return GameConfig.get("server", "Game Mechanics", "warehouse_account_shared", false);
    }

    // ==================== 缓存统计 ====================

    /**
     * 获取缓存统计信息（供脚本诊断用）。
     *
     * @return 包含 configCount/typeCount 的 Map
     */
    public static Map<String, Integer> getCacheStats() {
        Map<String, Integer> stats = new LinkedHashMap<>();
        stats.put("configCount", allowedItemIds.size());
        stats.put("typeCount", typeConfigMap.size());
        return stats;
    }

    // ==================== 直连数据库查询（供脚本使用） ====================

    /**
     * 直接查询数据库获取仓库物品列表（不走缓存）。
     * <p>
     * 由于 GraalVM JS 的 Context 可能使用独立 ClassLoader，
     * 脚本中通过 {@code Java.type()} 加载的类与 Spring 容器的类不共享 static 字段。
     * 此方法绕过 static 缓存，直接通过 MyBatis-Flex 查询数据库。
     * </p>
     *
     * @param accountId     账号ID
     * @param characterId   角色ID（账号共享模式下可为 null）
     * @param inventoryType 物品栏类型（null=所有类型）
     * @return 仓库物品列表
     */
    public static List<WarehouseItemDO> queryItems(Integer accountId, Integer characterId, Integer inventoryType) {
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                var itemMapper = context.getBean(WarehouseItemMapper.class);
                QueryWrapper qw = QueryWrapper.create()
                        .where("account_id = ?", accountId);
                if (!isAccountShared() && characterId != null) {
                    qw.and("character_id = ?", characterId);
                }
                if (inventoryType != null) {
                    qw.and("inventory_type = ?", inventoryType);
                }
                qw.orderBy("inventory_type", true).orderBy("item_id", true);
                return itemMapper.selectListByQuery(qw);
            }
        } catch (Exception e) {
            log.error("直连查询仓库物品失败", e);
        }
        return Collections.emptyList();
    }

    /**
     * 直接查询数据库获取某物品在仓库中的存放数量（不走缓存）。
     *
     * @param accountId   账号ID
     * @param characterId 角色ID（账号共享模式下可为 null）
     * @param itemId      物品ID
     * @return 已存放的数量
     */
    public static int queryItemQuantity(Integer accountId, Integer characterId, Integer itemId) {
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                var itemMapper = context.getBean(WarehouseItemMapper.class);
                QueryWrapper qw = QueryWrapper.create()
                        .where("account_id = ?", accountId)
                        .and("item_id = ?", itemId);
                if (!isAccountShared() && characterId != null) {
                    qw.and("character_id = ?", characterId);
                }
                List<WarehouseItemDO> items = itemMapper.selectListByQuery(qw);
                return items.stream().mapToInt(WarehouseItemDO::getQuantity).sum();
            }
        } catch (Exception e) {
            log.error("直连查询仓库物品数量失败", e);
        }
        return 0;
    }

    /**
     * 直接操作数据库存入物品到仓库（脚本用）。
     *
     * @param accountId     账号ID
     * @param characterId   角色ID
     * @param itemId        物品ID
     * @param inventoryType 物品栏类型
     * @param quantity      存入数量
     * @return 存入成功返回 true
     */
    public static boolean depositItem(Integer accountId, Integer characterId, Integer itemId,
                                      Integer inventoryType, Integer quantity) {
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                var warehouseService = context.getBean(org.gms.service.WarehouseService.class);
                return warehouseService.depositItem(accountId, characterId, itemId, inventoryType, quantity);
            }
        } catch (Exception e) {
            log.error("存入仓库物品失败", e);
        }
        return false;
    }

    /**
     * 直接操作数据库从仓库取出物品（脚本用）。
     *
     * @param accountId     账号ID
     * @param characterId   角色ID
     * @param itemId        物品ID
     * @param inventoryType 物品栏类型
     * @param quantity      取出数量
     * @return 实际取出的数量（-1=操作失败）
     */
    public static int withdrawItem(Integer accountId, Integer characterId, Integer itemId,
                                   Integer inventoryType, Integer quantity) {
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                var warehouseService = context.getBean(org.gms.service.WarehouseService.class);
                return warehouseService.withdrawItem(accountId, characterId, itemId, inventoryType, quantity);
            }
        } catch (Exception e) {
            log.error("从仓库取出物品失败", e);
        }
        return -1;
    }

    /**
     * 根据物品ID查询仓库配置中的掉落地图ID（供 JS 脚本传送使用）
     *
     * @param itemId 物品ID
     * @return 掉落地图ID（未配置或查询失败返回0）
     */
    public static int getDropMap(int itemId) {
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                var mapper = context.getBean(WarehouseConfigMapper.class);
                List<WarehouseConfigDO> list = mapper.selectListByQuery(
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
