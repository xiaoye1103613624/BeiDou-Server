package org.gms.config;

import org.gms.dao.entity.TownConfigDO;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 城镇开放状态缓存管理器。
 * 服务启动时由 TownConfigService 加载所有关闭的城镇到内存，
 * 在玩家切换地图时通过 {@link #isTownClosed(int)} 检查是否需要拦截。
 */
public class TownConfigManager {

    /** 已关闭城镇地图ID集合 */
    private static final Set<Integer> closedTownMapIds = ConcurrentHashMap.newKeySet();

    private TownConfigManager() {
    }

    /**
     * 加载配置列表到内存缓存（由 TownConfigService 在 @PostConstruct 时调用）
     */
    public static void load(List<TownConfigDO> configs) {
        closedTownMapIds.clear();
        for (TownConfigDO config : configs) {
            if (config.getEnabled() != null && config.getEnabled() == 0) {
                closedTownMapIds.add(config.getMapId());
            }
        }
    }

    /**
     * 判断指定城镇地图是否关闭
     */
    public static boolean isTownClosed(int mapId) {
        return closedTownMapIds.contains(mapId);
    }

    /**
     * 新增关闭城镇到缓存
     */
    public static void addClosedTown(int mapId) {
        closedTownMapIds.add(mapId);
    }

    /**
     * 从缓存中移除以开放城镇
     */
    public static void removeClosedTown(int mapId) {
        closedTownMapIds.remove(mapId);
    }
}