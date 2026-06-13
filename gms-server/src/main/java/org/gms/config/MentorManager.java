package org.gms.config;

import com.mybatisflex.core.query.QueryWrapper;
import org.gms.dao.entity.MentorConfigDO;
import org.gms.dao.mapper.MentorConfigMapper;
import org.gms.manager.ServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 师徒系统配置的静态缓存管理器。
 * <p>
 * 提供师徒系统的各项配置参数的查询功能，
 * 如创建师门所需等级、最大收徒数量、拜师等级上限、出师等级等。
 * 供 GraalVM JS 脚本通过 {@code Java.type()} 调用。
 * </p>
 */
public class MentorManager {

    private static final Logger log = LoggerFactory.getLogger(MentorManager.class);

    /** 配置键 → 配置值的映射（仅包含启用的配置） */
    private static final Map<String, String> configMap = new ConcurrentHashMap<>();

    /** 默认配置值，在数据库无记录时使用 */
    private static final Map<String, String> DEFAULT_CONFIGS = new LinkedHashMap<>();
    static {
        DEFAULT_CONFIGS.put("create_master_level", "70");
        DEFAULT_CONFIGS.put("max_disciples", "5");
        DEFAULT_CONFIGS.put("max_be_disciple_level", "50");
        DEFAULT_CONFIGS.put("graduate_level", "70");
    }

    private MentorManager() {}

    // ==================== 缓存管理 ====================

    /**
     * 加载配置数据到缓存
     * @param configs 配置DO列表
     */
    public static synchronized void load(List<MentorConfigDO> configs) {
        configMap.clear();
        int enabledCount = 0;
        for (MentorConfigDO c : configs) {
            if (c.getEnabled() != null && c.getEnabled() == 1) {
                configMap.put(c.getConfigKey(), c.getConfigValue());
                enabledCount++;
            }
        }
        log.info("MentorManager 缓存已刷新：总配置 {} 条，启用的 {} 条", configs.size(), enabledCount);
    }

    /**
     * 手动强制刷新缓存（从数据库重新加载）。
     * 脚本中可调用: {@code MentorManager.reload()}
     */
    public static void reload() {
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                var configMapper = context.getBean(MentorConfigMapper.class);
                load(configMapper.selectAll());
            } else {
                log.warn("Spring 上下文不可用，无法重新加载师徒系统配置");
            }
        } catch (Exception e) {
            log.error("重新加载师徒系统配置失败", e);
        }
    }

    // ==================== 配置查询方法 ====================

    /**
     * 获取配置的整数值
     * @param key 配置键
     * @param defaultValue 默认值（数据库无记录时使用）
     * @return 配置值
     */
    public static int getInt(String key, int defaultValue) {
        String value = configMap.get(key);
        if (value != null) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException e) {
                log.warn("配置 {} 的值 {} 无法转为整数，使用默认值 {}", key, value, defaultValue);
            }
        }
        // 使用代码默认值
        String defaultStr = DEFAULT_CONFIGS.get(key);
        if (defaultStr != null) {
            try {
                return Integer.parseInt(defaultStr);
            } catch (NumberFormatException ignored) {}
        }
        return defaultValue;
    }

    /**
     * 获取配置的字符串值
     * @param key 配置键
     * @param defaultValue 默认值
     * @return 配置值
     */
    public static String getString(String key, String defaultValue) {
        String value = configMap.get(key);
        return value != null ? value : DEFAULT_CONFIGS.getOrDefault(key, defaultValue);
    }

    /** 获取创建师门所需等级 */
    public static int getCreateMasterLevel() {
        return getInt("create_master_level", 70);
    }

    /** 获取最大收徒数量 */
    public static int getMaxDisciples() {
        return getInt("max_disciples", 5);
    }

    /** 获取可拜师的最高等级（超过此等级不可拜师） */
    public static int getMaxBeDiscipleLevel() {
        return getInt("max_be_disciple_level", 50);
    }

    /** 获取出师所需等级 */
    public static int getGraduateLevel() {
        return getInt("graduate_level", 70);
    }

    /** 获取所有配置（供诊断用） */
    public static Map<String, String> getAllConfigs() {
        Map<String, String> result = new LinkedHashMap<>();
        for (String key : DEFAULT_CONFIGS.keySet()) {
            result.put(key, getString(key, DEFAULT_CONFIGS.get(key)));
        }
        return result;
    }

    // ==================== 直连数据库查询（供脚本绕过缓存使用） ====================

    /**
     * 直接查询数据库获取所有启用的配置（不走缓存）。
     * <p>
     * 由于 GraalVM JS 的 Context 可能使用独立 ClassLoader，
     * 脚本中通过此方法可直接从数据库获取最新配置。
     * </p>
     * @return 配置列表
     */
    public static List<MentorConfigDO> queryEnabledConfigs() {
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                var mapper = context.getBean(MentorConfigMapper.class);
                return mapper.selectListByQuery(
                        QueryWrapper.create().where("enabled = ?", 1));
            }
        } catch (Exception e) {
            log.error("直连查询师徒系统配置失败", e);
        }
        return Collections.emptyList();
    }
}
