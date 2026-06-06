package org.gms.config;

import org.gms.dao.entity.EquipAdvanceCostDO;
import org.gms.dao.entity.EquipAdvanceRouteDO;
import org.gms.dao.entity.EquipAdvanceStageDO;
import org.gms.manager.ServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 装备进阶配置的静态缓存管理器。
 * 供 GraalVM JS 脚本通过 {@code Java.type()} 调用查询进阶规则。
 */
public class EquipAdvanceManager {

    private static final Logger log = LoggerFactory.getLogger(EquipAdvanceManager.class);

    /** 职业群 → 路线实体映射（仅包含启用的配置） */
    private static final Map<String, EquipAdvanceRouteDO> routeMap = new ConcurrentHashMap<>();
    /** 路线ID → 阶段列表映射（按阶段顺序排序） */
    private static final Map<Long, List<EquipAdvanceStageDO>> stageMap = new ConcurrentHashMap<>();
    /** 阶段ID → 消耗材料列表映射 */
    private static final Map<Long, List<EquipAdvanceCostDO>> costMap = new ConcurrentHashMap<>();

    private EquipAdvanceManager() {}

    /**
     * 加载配置数据到缓存
     * @param routes 路线DO列表
     * @param stages 阶段DO列表
     * @param costs 消耗DO列表
     */
    public static synchronized void load(List<EquipAdvanceRouteDO> routes,
                                         List<EquipAdvanceStageDO> stages,
                                         List<EquipAdvanceCostDO> costs) {
        routeMap.clear();
        stageMap.clear();
        costMap.clear();

        int enabledCount = 0;
        for (EquipAdvanceRouteDO r : routes) {
            if (r.getEnabled() != null && r.getEnabled() == 1) {
                routeMap.put(r.getJobGroup(), r);
                enabledCount++;
            }
        }
        for (EquipAdvanceStageDO s : stages) {
            stageMap.computeIfAbsent(s.getRouteId(), k -> new ArrayList<>()).add(s);
        }
        for (EquipAdvanceCostDO c : costs) {
            costMap.computeIfAbsent(c.getStageId(), k -> new ArrayList<>()).add(c);
        }
        // 阶段列表按阶段顺序排序
        for (List<EquipAdvanceStageDO> list : stageMap.values()) {
            list.sort(Comparator.comparingInt(EquipAdvanceStageDO::getStageOrder));
        }
        log.info("EquipAdvanceManager 缓存已刷新：总路线 {} 条，启用的 {} 条，阶段 {} 条，消耗 {} 条",
                routes.size(), enabledCount, stages.size(), costs.size());
    }

    /**
     * 手动强制刷新缓存（从数据库重新加载）。
     * 脚本中可调用: {@code EquipAdvanceManager.reload()}
     */
    public static void reload() {
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                var routeMapper = context.getBean(org.gms.dao.mapper.EquipAdvanceRouteMapper.class);
                var stageMapper = context.getBean(org.gms.dao.mapper.EquipAdvanceStageMapper.class);
                var costMapper = context.getBean(org.gms.dao.mapper.EquipAdvanceCostMapper.class);
                load(routeMapper.selectAll(), stageMapper.selectAll(), costMapper.selectAll());
            } else {
                log.warn("Spring 上下文不可用，无法重新加载装备进阶配置");
            }
        } catch (Exception e) {
            log.error("重新加载装备进阶配置失败", e);
        }
    }

    /**
     * 根据职业群获取进阶路线
     * @param jobGroup 职业群（warrior/archer/mage/thief/pirate）
     * @return 路线实体，不存在返回null
     */
    public static EquipAdvanceRouteDO getRoute(String jobGroup) {
        return routeMap.get(jobGroup);
    }

    /**
     * 获取某路线的所有阶段（按阶段顺序排序）
     * @param routeId 路线ID
     * @return 阶段列表，无数据返回空列表
     */
    public static List<EquipAdvanceStageDO> getStages(Long routeId) {
        if (routeId == null) {
            return Collections.emptyList();
        }
        return stageMap.getOrDefault(routeId, Collections.emptyList());
    }

    /**
     * 获取某路线的指定阶段
     * @param routeId 路线ID
     * @param stageOrder 阶段顺序
     * @return 阶段实体，不存在返回null
     */
    public static EquipAdvanceStageDO getStage(Long routeId, int stageOrder) {
        List<EquipAdvanceStageDO> list = stageMap.get(routeId);
        if (list == null) return null;
        for (EquipAdvanceStageDO s : list) {
            if (s.getStageOrder() == stageOrder) return s;
        }
        return null;
    }

    /**
     * 获取某阶段的材料消耗
     * @param stageId 阶段ID
     * @return 消耗材料列表，无数据返回空列表
     */
    public static List<EquipAdvanceCostDO> getCosts(Long stageId) {
        return costMap.getOrDefault(stageId, Collections.emptyList());
    }

    /**
     * 获取所有已启用路线的Map（供脚本遍历）
     * @return 职业群→路线实体映射
     */
    public static Map<String, EquipAdvanceRouteDO> getRouteMap() {
        return routeMap;
    }

    /**
     * 根据装备ID查找其所属的路线和阶段。
     * 用于判断玩家身上的装备是否可以进行进阶。
     * @param itemId 装备物品ID
     * @return 包含路线和当前阶段的Map，格式: {route: EquipAdvanceRouteDO, stage: EquipAdvanceStageDO}，未找到返回null
     */
    public static Map<String, Object> findRouteAndStage(int itemId) {
        for (var entry : stageMap.entrySet()) {
            Long routeId = entry.getKey();
            for (EquipAdvanceStageDO s : entry.getValue()) {
                if (s.getTargetItemId() != null && s.getTargetItemId() == itemId) {
                    EquipAdvanceRouteDO route = routeMap.values().stream()
                            .filter(r -> r.getId().equals(routeId))
                            .findFirst().orElse(null);
                    if (route != null) {
                        Map<String, Object> result = new HashMap<>();
                        result.put("route", route);
                        result.put("stage", s);
                        return result;
                    }
                }
            }
        }
        return null;
    }
}