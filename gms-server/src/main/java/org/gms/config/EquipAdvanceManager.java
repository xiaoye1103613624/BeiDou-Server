package org.gms.config;

import com.mybatisflex.core.query.QueryWrapper;
import org.gms.dao.entity.EquipAdvanceCostDO;
import org.gms.dao.entity.EquipAdvanceRouteDO;
import org.gms.dao.entity.EquipAdvanceStageDO;
import org.gms.dao.mapper.EquipAdvanceCostMapper;
import org.gms.dao.mapper.EquipAdvanceRouteMapper;
import org.gms.dao.mapper.EquipAdvanceStageMapper;
import org.gms.manager.ServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 装备进阶配置的静态缓存管理器。
 * <p>
 * 供 GraalVM JS 脚本通过 {@code Java.type()} 调用查询进阶规则。
 * 核心提供 {@link #safeAddStat(short, int)} 方法，防止 JS 脚本中
 * 属性值溢出 Java short 范围导致客户端闪退。
 * </p>
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

    // ==================== 安全属性叠加 ====================

    /**
     * 安全叠加装备属性值，钳制到 [0, Short.MAX_VALUE]。
     * <p>
     * 同 {@link EquipEnhanceManager#safeAddStat(short, int)}，供进阶脚本使用。
     * </p>
     *
     * @param current 当前属性值（short）
     * @param add     待叠加值（int，来自数据库配置）
     * @return 叠加后钳制到 [0, 32767] 的值
     */
    public static short safeAddStat(short current, int add) {
        if (add <= 0) return current;
        int result = current + add;
        if (result > Short.MAX_VALUE) return Short.MAX_VALUE;
        return (short) result;
    }

    /**
     * 将阶段配置中的属性加成安全应用到装备对象上。
     * <p>
     * JS 进阶脚本中创建新装备后调用此方法，替代手动逐项 setStr(getStr()+add)。
     * </p>
     *
     * @param equip 装备对象（通常是从 WZ 数据新创建的）
     * @param stage 进阶阶段配置（包含各项属性加成）
     */
    public static void applyAdvanceStats(org.gms.client.inventory.Equip equip, EquipAdvanceStageDO stage) {
        if (stage.getStrAdd() > 0)   equip.setStr(safeAddStat(equip.getStr(), stage.getStrAdd()));
        if (stage.getDexAdd() > 0)   equip.setDex(safeAddStat(equip.getDex(), stage.getDexAdd()));
        if (stage.getIntAdd() > 0)   equip.setInt(safeAddStat(equip.getInt(), stage.getIntAdd()));
        if (stage.getLukAdd() > 0)   equip.setLuk(safeAddStat(equip.getLuk(), stage.getLukAdd()));
        if (stage.getHpAdd() > 0)    equip.setHp(safeAddStat(equip.getHp(), stage.getHpAdd()));
        if (stage.getMpAdd() > 0)    equip.setMp(safeAddStat(equip.getMp(), stage.getMpAdd()));
        if (stage.getWatkAdd() > 0)  equip.setWatk(safeAddStat(equip.getWatk(), stage.getWatkAdd()));
        if (stage.getMatkAdd() > 0)  equip.setMatk(safeAddStat(equip.getMatk(), stage.getMatkAdd()));
        if (stage.getWdefAdd() > 0)  equip.setWdef(safeAddStat(equip.getWdef(), stage.getWdefAdd()));
        if (stage.getMdefAdd() > 0)  equip.setMdef(safeAddStat(equip.getMdef(), stage.getMdefAdd()));
        if (stage.getAccAdd() > 0)   equip.setAcc(safeAddStat(equip.getAcc(), stage.getAccAdd()));
        if (stage.getAvoidAdd() > 0) equip.setAvoid(safeAddStat(equip.getAvoid(), stage.getAvoidAdd()));
        if (stage.getSpeedAdd() > 0) equip.setSpeed(safeAddStat(equip.getSpeed(), stage.getSpeedAdd()));
        if (stage.getJumpAdd() > 0)  equip.setJump(safeAddStat(equip.getJump(), stage.getJumpAdd()));
    }

    /**
     * 计算从第0阶段到指定阶段（含）的所有属性加成累计值。
     * <p>
     * 进阶系统采用属性叠加模式：每阶的加成与之前所有阶段累计。
     * 例如阶段2的属性 = 基础属性 + 阶段1加成 + 阶段2加成。
     * </p>
     *
     * @param routeId        路线ID
     * @param upToStageOrder 累计到哪个阶段（含），例如传2表示阶段1+阶段2的累计
     * @return 累计属性Map，key为 "strAdd", "dexAdd", ...，value为累计值
     */
    public static Map<String, Integer> getCumulativeStats(Long routeId, int upToStageOrder) {
        Map<String, Integer> stats = new LinkedHashMap<>();
        String[] keys = {"strAdd","dexAdd","intAdd","lukAdd","hpAdd","mpAdd",
                         "watkAdd","matkAdd","wdefAdd","mdefAdd","accAdd","avoidAdd",
                         "speedAdd","jumpAdd"};
        for (String k : keys) stats.put(k, 0);

        List<EquipAdvanceStageDO> stages = stageMap.get(routeId);
        if (stages == null) return stats;

        for (EquipAdvanceStageDO s : stages) {
            if (s.getStageOrder() > upToStageOrder) break;
            if (s.getStageOrder() == 0) continue;  // 第0阶段（初始装备）不叠加属性

            stats.merge("strAdd",   s.getStrAdd(),   Integer::sum);
            stats.merge("dexAdd",   s.getDexAdd(),   Integer::sum);
            stats.merge("intAdd",   s.getIntAdd(),   Integer::sum);
            stats.merge("lukAdd",   s.getLukAdd(),   Integer::sum);
            stats.merge("hpAdd",    s.getHpAdd(),    Integer::sum);
            stats.merge("mpAdd",    s.getMpAdd(),    Integer::sum);
            stats.merge("watkAdd",  s.getWatkAdd(),  Integer::sum);
            stats.merge("matkAdd",  s.getMatkAdd(),  Integer::sum);
            stats.merge("wdefAdd",  s.getWdefAdd(),  Integer::sum);
            stats.merge("mdefAdd",  s.getMdefAdd(),  Integer::sum);
            stats.merge("accAdd",   s.getAccAdd(),   Integer::sum);
            stats.merge("avoidAdd", s.getAvoidAdd(), Integer::sum);
            stats.merge("speedAdd", s.getSpeedAdd(), Integer::sum);
            stats.merge("jumpAdd",  s.getJumpAdd(),  Integer::sum);
        }
        return stats;
    }

    // ==================== 缓存管理 ====================

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

    // ==================== 查询方法 ====================

    /** 根据职业群获取进阶路线 */
    public static EquipAdvanceRouteDO getRoute(String jobGroup) {
        return routeMap.get(jobGroup);
    }

    /** 获取某路线的所有阶段（按阶段顺序排序） */
    public static List<EquipAdvanceStageDO> getStages(Long routeId) {
        if (routeId == null) return Collections.emptyList();
        return stageMap.getOrDefault(routeId, Collections.emptyList());
    }

    /** 获取某路线的指定阶段 */
    public static EquipAdvanceStageDO getStage(Long routeId, int stageOrder) {
        List<EquipAdvanceStageDO> list = stageMap.get(routeId);
        if (list == null) return null;
        for (EquipAdvanceStageDO s : list) {
            if (s.getStageOrder() == stageOrder) return s;
        }
        return null;
    }

    /** 获取某阶段的材料消耗 */
    public static List<EquipAdvanceCostDO> getCosts(Long stageId) {
        return costMap.getOrDefault(stageId, Collections.emptyList());
    }

    /** 获取所有已启用路线的Map（供脚本遍历） */
    public static Map<String, EquipAdvanceRouteDO> getRouteMap() {
        return routeMap;
    }

    // ==================== 直连数据库查询（供脚本绕过缓存使用） ====================

    /**
     * 直接查询数据库获取所有启用的进阶路线（不走缓存）。
     *
     * @return 启用状态（enabled=1）的进阶路线列表
     */
    public static List<EquipAdvanceRouteDO> queryEnabledRoutes() {
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                var mapper = context.getBean(EquipAdvanceRouteMapper.class);
                return mapper.selectListByQuery(
                        QueryWrapper.create().where("enabled = ?", 1));
            }
        } catch (Exception e) {
            log.error("直连查询装备进阶路线失败", e);
        }
        return Collections.emptyList();
    }

    /**
     * 直接查询数据库获取某路线的所有阶段（不走缓存）。
     *
     * @param routeId 路线ID
     * @return 阶段列表（按 stage_order 排序）
     */
    public static List<EquipAdvanceStageDO> queryStages(Long routeId) {
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                var mapper = context.getBean(EquipAdvanceStageMapper.class);
                var list = mapper.selectListByQuery(
                        QueryWrapper.create()
                                .where("route_id = ?", routeId)
                                .orderBy("stage_order", true));
                return list;
            }
        } catch (Exception e) {
            log.error("直连查询装备进阶阶段失败", e);
        }
        return Collections.emptyList();
    }

    /**
     * 直接查询数据库获取某阶段的消耗材料（不走缓存）。
     *
     * @param stageId 阶段ID
     * @return 消耗材料列表
     */
    public static List<EquipAdvanceCostDO> queryCosts(Long stageId) {
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                var mapper = context.getBean(EquipAdvanceCostMapper.class);
                return mapper.selectListByQuery(
                        QueryWrapper.create().where("stage_id = ?", stageId));
            }
        } catch (Exception e) {
            log.error("直连查询装备进阶消耗失败", e);
        }
        return Collections.emptyList();
    }

    /**
     * 直接查询数据库计算累计属性（不走缓存）。
     *
     * @param routeId 路线ID
     * @param upToStageOrder 累计到哪个阶段（含）
     * @return 累计属性Map
     */
    public static Map<String, Integer> queryCumulativeStats(Long routeId, int upToStageOrder) {
        Map<String, Integer> stats = new LinkedHashMap<>();
        String[] keys = {"strAdd","dexAdd","intAdd","lukAdd","hpAdd","mpAdd",
                         "watkAdd","matkAdd","wdefAdd","mdefAdd","accAdd","avoidAdd",
                         "speedAdd","jumpAdd"};
        for (String k : keys) stats.put(k, 0);

        List<EquipAdvanceStageDO> stages = queryStages(routeId);
        if (stages.isEmpty()) return stats;

        for (EquipAdvanceStageDO s : stages) {
            if (s.getStageOrder() > upToStageOrder) break;
            if (s.getStageOrder() == 0) continue;

            stats.merge("strAdd",   s.getStrAdd(),   Integer::sum);
            stats.merge("dexAdd",   s.getDexAdd(),   Integer::sum);
            stats.merge("intAdd",   s.getIntAdd(),   Integer::sum);
            stats.merge("lukAdd",   s.getLukAdd(),   Integer::sum);
            stats.merge("hpAdd",    s.getHpAdd(),    Integer::sum);
            stats.merge("mpAdd",    s.getMpAdd(),    Integer::sum);
            stats.merge("watkAdd",  s.getWatkAdd(),  Integer::sum);
            stats.merge("matkAdd",  s.getMatkAdd(),  Integer::sum);
            stats.merge("wdefAdd",  s.getWdefAdd(),  Integer::sum);
            stats.merge("mdefAdd",  s.getMdefAdd(),  Integer::sum);
            stats.merge("accAdd",   s.getAccAdd(),   Integer::sum);
            stats.merge("avoidAdd", s.getAvoidAdd(), Integer::sum);
            stats.merge("speedAdd", s.getSpeedAdd(), Integer::sum);
            stats.merge("jumpAdd",  s.getJumpAdd(),  Integer::sum);
        }
        return stats;
    }

    /**
     * 根据装备ID查找其所属的路线和阶段。
     * 用于判断玩家身上的装备是否可以进行进阶。
     *
     * @param itemId 装备物品ID
     * @return 包含路线和当前阶段的Map: {route: EquipAdvanceRouteDO, stage: EquipAdvanceStageDO}，未找到返回null
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
