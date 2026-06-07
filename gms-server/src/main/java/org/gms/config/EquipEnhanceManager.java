package org.gms.config;

import com.mybatisflex.core.query.QueryWrapper;
import org.gms.dao.entity.EquipEnhanceConfigDO;
import org.gms.dao.entity.EquipEnhanceLevelDO;
import org.gms.dao.entity.EquipEnhanceCostDO;
import org.gms.dao.mapper.EquipEnhanceConfigMapper;
import org.gms.dao.mapper.EquipEnhanceCostMapper;
import org.gms.dao.mapper.EquipEnhanceLevelMapper;
import org.gms.manager.ServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 装备强化配置的静态缓存管理器。
 * <p>
 * 供 GraalVM JS 脚本通过 {@code Java.type()} 调用查询强化规则。
 * 核心提供 {@link #safeAddStat(short, int)} 方法，防止 JS 脚本中
 * 属性值溢出 Java short 范围导致客户端闪退。
 * </p>
 */
public class EquipEnhanceManager {

    private static final Logger log = LoggerFactory.getLogger(EquipEnhanceManager.class);

    /** 物品ID → 配置实体映射（仅包含启用的配置） */
    private static final Map<Integer, EquipEnhanceConfigDO> configMap = new ConcurrentHashMap<>();
    /** 配置ID → 等级列表映射（按强化等级排序） */
    private static final Map<Long, List<EquipEnhanceLevelDO>> levelMap = new ConcurrentHashMap<>();
    /** 等级ID → 消耗物品列表映射 */
    private static final Map<Long, List<EquipEnhanceCostDO>> costMap = new ConcurrentHashMap<>();

    private EquipEnhanceManager() {}

    // ==================== 安全属性叠加 ====================

    /**
     * 安全叠加装备属性值，钳制到 [0, Short.MAX_VALUE]。
     * <p>
     * 在 GraalVM JS 中调用 {@code equip.getStr() + addValue} 时，
     * 结果可能超出 Java short 范围（-32768~32767），窄化时溢出为负数。
     * 负属性值通过 {@link org.gms.util.PacketCreator#addItemInfo} 写入封包后，
     * 客户端渲染装备提示时闪退。
     * </p>
     * <p>
     * 使用方式（JS 脚本中）：
     * <pre>{@code
     * equip.setStr(EquipEnhanceManager.safeAddStat(equip.getStr(), levelCfg.strAdd));
     * }</pre>
     * </p>
     *
     * @param current 当前属性值（short）
     * @param add     待叠加值（int，来自数据库配置）
     * @return 叠加后钳制到 [0, 32767] 的值
     */
    public static short safeAddStat(short current, int add) {
        if (add <= 0) return current;
        int result = current + add;  // short 自动提升为 int，无溢出风险
        if (result > Short.MAX_VALUE) return Short.MAX_VALUE;
        return (short) result;
    }

    /**
     * 将配置中的属性加成安全应用到装备对象上。
     * <p>
     * JS 脚本中可直接调用此方法一次性应用所有属性加成，避免逐项手动调用 safeAddStat。
     * </p>
     * <pre>{@code
     * EquipEnhanceManager.applyEnhanceStats(equip, levelCfg);
     * }</pre>
     *
     * @param equip 装备对象
     * @param level 强化等级配置（包含各项属性加成）
     */
    public static void applyEnhanceStats(org.gms.client.inventory.Equip equip, EquipEnhanceLevelDO level) {
        if (level.getStrAdd() > 0)   equip.setStr(safeAddStat(equip.getStr(), level.getStrAdd()));
        if (level.getDexAdd() > 0)   equip.setDex(safeAddStat(equip.getDex(), level.getDexAdd()));
        if (level.getIntAdd() > 0)   equip.setInt(safeAddStat(equip.getInt(), level.getIntAdd()));
        if (level.getLukAdd() > 0)   equip.setLuk(safeAddStat(equip.getLuk(), level.getLukAdd()));
        if (level.getHpAdd() > 0)    equip.setHp(safeAddStat(equip.getHp(), level.getHpAdd()));
        if (level.getMpAdd() > 0)    equip.setMp(safeAddStat(equip.getMp(), level.getMpAdd()));
        if (level.getWatkAdd() > 0)  equip.setWatk(safeAddStat(equip.getWatk(), level.getWatkAdd()));
        if (level.getMatkAdd() > 0)  equip.setMatk(safeAddStat(equip.getMatk(), level.getMatkAdd()));
        if (level.getWdefAdd() > 0)  equip.setWdef(safeAddStat(equip.getWdef(), level.getWdefAdd()));
        if (level.getMdefAdd() > 0)  equip.setMdef(safeAddStat(equip.getMdef(), level.getMdefAdd()));
        if (level.getAccAdd() > 0)   equip.setAcc(safeAddStat(equip.getAcc(), level.getAccAdd()));
        if (level.getAvoidAdd() > 0) equip.setAvoid(safeAddStat(equip.getAvoid(), level.getAvoidAdd()));
        if (level.getSpeedAdd() > 0) equip.setSpeed(safeAddStat(equip.getSpeed(), level.getSpeedAdd()));
        if (level.getJumpAdd() > 0)  equip.setJump(safeAddStat(equip.getJump(), level.getJumpAdd()));
    }

    // ==================== 缓存管理 ====================

    /**
     * 加载配置数据到缓存
     * @param configs 配置DO列表
     * @param levels 等级DO列表
     * @param costs 消耗DO列表
     */
    public static synchronized void load(List<EquipEnhanceConfigDO> configs,
                                         List<EquipEnhanceLevelDO> levels,
                                         List<EquipEnhanceCostDO> costs) {
        configMap.clear();
        levelMap.clear();
        costMap.clear();

        int enabledCount = 0;
        for (EquipEnhanceConfigDO c : configs) {
            if (c.getEnabled() != null && c.getEnabled() == 1) {
                configMap.put(c.getItemId(), c);
                enabledCount++;
            }
        }
        for (EquipEnhanceLevelDO lv : levels) {
            levelMap.computeIfAbsent(lv.getConfigId(), k -> new ArrayList<>()).add(lv);
        }
        for (EquipEnhanceCostDO co : costs) {
            costMap.computeIfAbsent(co.getLevelId(), k -> new ArrayList<>()).add(co);
        }
        // 等级列表按强化等级排序
        for (List<EquipEnhanceLevelDO> list : levelMap.values()) {
            list.sort(Comparator.comparingInt(EquipEnhanceLevelDO::getEnhanceLevel));
        }
        log.info("EquipEnhanceManager 缓存已刷新：总配置 {} 条，启用的 {} 条，等级 {} 条，消耗 {} 条",
                configs.size(), enabledCount, levels.size(), costs.size());
    }

    /**
     * 手动强制刷新缓存（从数据库重新加载）。
     * 脚本中可调用: {@code EquipEnhanceManager.reload()}
     */
    public static void reload() {
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                var configMapper = context.getBean(org.gms.dao.mapper.EquipEnhanceConfigMapper.class);
                var levelMapper = context.getBean(org.gms.dao.mapper.EquipEnhanceLevelMapper.class);
                var costMapper = context.getBean(org.gms.dao.mapper.EquipEnhanceCostMapper.class);
                load(configMapper.selectAll(), levelMapper.selectAll(), costMapper.selectAll());
            } else {
                log.warn("Spring 上下文不可用，无法重新加载装备强化配置");
            }
        } catch (Exception e) {
            log.error("重新加载装备强化配置失败", e);
        }
    }

    // ==================== 查询方法 ====================

    /** 判断装备是否可强化 */
    public static boolean canEnhance(int itemId) {
        return configMap.containsKey(itemId);
    }

    /** 获取装备强化配置 */
    public static EquipEnhanceConfigDO getConfig(int itemId) {
        return configMap.get(itemId);
    }

    /** 获取指定等级强化数据 */
    public static EquipEnhanceLevelDO getLevel(Long configId, int enhanceLevel) {
        List<EquipEnhanceLevelDO> list = levelMap.get(configId);
        if (list == null) return null;
        for (EquipEnhanceLevelDO lv : list) {
            if (lv.getEnhanceLevel() == enhanceLevel) return lv;
        }
        return null;
    }

    /** 获取某强化等级的消耗物品 */
    public static List<EquipEnhanceCostDO> getCosts(Long levelId) {
        return costMap.getOrDefault(levelId, Collections.emptyList());
    }

    /** 获取配置的所有等级 */
    public static List<EquipEnhanceLevelDO> getAllLevels(Long configId) {
        return levelMap.getOrDefault(configId, Collections.emptyList());
    }

    /** 获取所有已启用配置的Map（供脚本遍历） */
    public static Map<Integer, EquipEnhanceConfigDO> getConfigMap() {
        return configMap;
    }

    /**
     * 获取缓存统计信息（供脚本诊断用）。
     * @return 包含 configCount/levelCount/costCount 的 Map
     */
    public static Map<String, Integer> getCacheStats() {
        Map<String, Integer> stats = new LinkedHashMap<>();
        stats.put("configCount", configMap.size());
        int levelCount = 0, costCount = 0;
        for (List<EquipEnhanceLevelDO> list : levelMap.values()) {
            levelCount += list.size();
        }
        for (List<EquipEnhanceCostDO> list : costMap.values()) {
            costCount += list.size();
        }
        stats.put("levelCount", levelCount);
        stats.put("costCount", costCount);
        return stats;
    }

    // ==================== 直连数据库查询（供脚本绕过缓存使用） ====================

    /**
     * 直接查询数据库获取所有启用的强化配置（不走缓存）。
     * <p>
     * 由于 GraalVM JS 的 Context 可能使用独立 ClassLoader，
     * 脚本中通过 {@code Java.type()} 加载的类与 Spring 容器的类不共享 static 字段。
     * 此方法绕过 static 缓存，直接通过 JDBC 查询数据库，
     * 确保脚本始终能获取到最新数据。
     * </p>
     *
     * @return 启用状态（enabled=1）的强化配置列表
     */
    public static List<EquipEnhanceConfigDO> queryEnabledConfigs() {
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                var mapper = context.getBean(EquipEnhanceConfigMapper.class);
                return mapper.selectListByQuery(
                        QueryWrapper.create().where("enabled = ?", 1));
            }
        } catch (Exception e) {
            log.error("直连查询装备强化配置失败", e);
        }
        return Collections.emptyList();
    }

    /**
     * 直接查询数据库获取某配置的指定等级（不走缓存）。
     *
     * @param configId     配置ID
     * @param enhanceLevel 强化等级
     * @return 等级实体，不存在返回 null
     */
    public static EquipEnhanceLevelDO queryLevel(Long configId, int enhanceLevel) {
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                var mapper = context.getBean(EquipEnhanceLevelMapper.class);
                List<EquipEnhanceLevelDO> list = mapper.selectListByQuery(
                        QueryWrapper.create()
                                .where("config_id = ?", configId)
                                .where("enhance_level = ?", enhanceLevel));
                return list.isEmpty() ? null : list.get(0);
            }
        } catch (Exception e) {
            log.error("直连查询装备强化等级失败", e);
        }
        return null;
    }

    /**
     * 直接查询数据库获取某等级的消耗物品（不走缓存）。
     *
     * @param levelId 等级ID
     * @return 消耗物品列表
     */
    public static List<EquipEnhanceCostDO> queryCosts(Long levelId) {
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                var mapper = context.getBean(EquipEnhanceCostMapper.class);
                return mapper.selectListByQuery(
                        QueryWrapper.create().where("level_id = ?", levelId));
            }
        } catch (Exception e) {
            log.error("直连查询装备强化消耗失败", e);
        }
        return Collections.emptyList();
    }
}
