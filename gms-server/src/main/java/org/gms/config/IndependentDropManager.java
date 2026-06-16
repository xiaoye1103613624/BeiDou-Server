package org.gms.config;

import com.mybatisflex.core.query.QueryWrapper;
import org.gms.dao.entity.IndependentDropConfigDO;
import org.gms.dao.mapper.IndependentDropConfigMapper;
import org.gms.manager.ServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 独立掉落配置管理器
 * <p>
 * 管理哪些怪物启用独立掉落模式（每个队员独立随机一份掉落）。
 * 配合 xy_game_config 中的 independent_drop_enabled 全局开关使用。
 * </p>
 */
public class IndependentDropManager {

    private static final Logger log = LoggerFactory.getLogger(IndependentDropManager.class);

    /** 启用独立掉落的怪物ID集合 */
    private static final Set<Integer> enabledMobIds = ConcurrentHashMap.newKeySet();

    private IndependentDropManager() {}

    /** 加载配置 */
    public static synchronized void load(List<IndependentDropConfigDO> configs) {
        enabledMobIds.clear();
        for (IndependentDropConfigDO c : configs) {
            if (c.getEnabled() != null && c.getEnabled() == 1) {
                enabledMobIds.add(c.getMobId());
            }
        }
        log.info("IndependentDropManager 缓存已刷新：启用独立掉落的怪物 {} 个", enabledMobIds.size());
    }

    /** 强制刷新缓存 */
    public static void reload() {
        try {
            var ctx = ServerManager.getApplicationContext();
            if (ctx != null) {
                var mapper = ctx.getBean(IndependentDropConfigMapper.class);
                load(mapper.selectAll());
            }
        } catch (Exception e) {
            log.error("重新加载独立掉落配置失败", e);
        }
    }

    /** 全局开关是否启用 */
    public static boolean isGlobalEnabled() {
        return GameConfig.getServerBoolean("independent_drop_enabled");
    }

    /** 指定怪物是否启用了独立掉落 */
    public static boolean isIndependentDrop(int mobId) {
        return enabledMobIds.contains(mobId);
    }

    /** 获取所有启用独立掉落的怪物ID */
    public static Set<Integer> getEnabledMobIds() {
        return Collections.unmodifiableSet(enabledMobIds);
    }

    /** 直连数据库查询（供后台管理使用） */
    public static List<IndependentDropConfigDO> queryAll() {
        try {
            var ctx = ServerManager.getApplicationContext();
            if (ctx != null) {
                var mapper = ctx.getBean(IndependentDropConfigMapper.class);
                return mapper.selectAll();
            }
        } catch (Exception e) {
            log.error("查询独立掉落配置失败", e);
        }
        return Collections.emptyList();
    }
}
