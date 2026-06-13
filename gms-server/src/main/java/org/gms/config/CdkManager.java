package org.gms.config;

import org.gms.dao.entity.CdkConfigDO;
import org.gms.dao.entity.CdkItemDO;
import org.gms.dao.mapper.CdkConfigMapper;
import org.gms.dao.mapper.CdkItemMapper;
import org.gms.manager.ServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * CDK配置的静态缓存管理器。
 * <p>
 * 供 GraalVM JS 脚本通过 {@code Java.type()} 调用查询CDK信息。
 * 提供按兑换码查询配置和道具列表的功能。
 * </p>
 */
public class CdkManager {

    private static final Logger log = LoggerFactory.getLogger(CdkManager.class);

    /** 兑换码 → CDK配置映射（仅包含启用的配置） */
    private static final Map<String, CdkConfigDO> codeCache = new ConcurrentHashMap<>();

    /** CDK配置ID → 道具列表映射 */
    private static final Map<Long, List<CdkItemDO>> itemCache = new ConcurrentHashMap<>();

    private CdkManager() {}

    // ==================== 缓存管理 ====================

    /**
     * 加载配置数据到缓存
     *
     * @param configs CDK配置DO列表
     * @param items   道具DO列表
     */
    public static synchronized void load(List<CdkConfigDO> configs,
                                          List<CdkItemDO> items) {
        codeCache.clear();
        itemCache.clear();

        int enabledCount = 0;
        for (CdkConfigDO c : configs) {
            if (c.getEnabled() != null && c.getEnabled() == 1) {
                codeCache.put(c.getCode(), c);
                enabledCount++;
            }
        }

        for (CdkItemDO item : items) {
            itemCache.computeIfAbsent(item.getCdkId(), k -> new ArrayList<>()).add(item);
        }

        log.info("CdkManager 缓存已刷新：总配置 {} 条，启用的 {} 条，道具 {} 条",
                configs.size(), enabledCount, items.size());
    }

    /**
     * 手动强制刷新缓存（从数据库重新加载）。
     * 脚本中可调用: {@code CdkManager.reload()}
     */
    public static void reload() {
        try {
            var context = ServerManager.getApplicationContext();
            if (context != null) {
                var configMapper = context.getBean(CdkConfigMapper.class);
                var itemMapper = context.getBean(CdkItemMapper.class);
                load(configMapper.selectAll(), itemMapper.selectAll());
            } else {
                log.warn("CdkManager.reload() 失败：ApplicationContext 未就绪");
            }
        } catch (Exception e) {
            log.error("CdkManager.reload() 异常", e);
        }
    }

    // ==================== 查询接口 ====================

    /**
     * 根据兑换码获取启用的CDK配置
     *
     * @param code 兑换码
     * @return CDK配置DO，不存在或未启用返回null
     */
    public static CdkConfigDO getEnabledCdk(String code) {
        if (code == null) return null;
        return codeCache.get(code.toUpperCase().trim());
    }

    /**
     * 获取CDK对应的道具列表
     *
     * @param cdkId CDK配置ID
     * @return 道具列表
     */
    public static List<CdkItemDO> getItems(Long cdkId) {
        if (cdkId == null) return Collections.emptyList();
        return itemCache.getOrDefault(cdkId, Collections.emptyList());
    }

    /**
     * 检查兑换码是否可用（存在、启用、未过期、未达使用上限）
     *
     * @param code 兑换码
     * @return 可用返回null，否则返回错误信息
     */
    public static String checkAvailable(String code) {
        CdkConfigDO config = getEnabledCdk(code);
        if (config == null) return "CDK兑换码不存在或已禁用";

        if (config.getExpireTime() != null && config.getExpireTime().before(new Date())) {
            return "该CDK已过期";
        }

        int maxUse = config.getMaxUseCount() != null ? config.getMaxUseCount() : 1;
        int used = config.getUsedCount() != null ? config.getUsedCount() : 0;
        if (used >= maxUse) {
            return "该CDK已达使用上限（" + used + "/" + maxUse + "）";
        }

        return null; // 可用
    }
}
