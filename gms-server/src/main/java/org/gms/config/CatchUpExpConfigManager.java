package org.gms.config;

import org.gms.dao.entity.CatchUpExpConfigDO;

import java.util.ArrayList;
import java.util.List;

/**
 * 追赶机制经验倍率管理器。
 * 服务启动时由 CatchUpExpConfigService 加载所有启用的配置到内存，
 * 在角色获取经验时通过 {@link #getMultiplier(int)} 获取对应等级的倍率。
 */
public class CatchUpExpConfigManager {

    /** 追赶经验配置列表（按等级区间升序排列） */
    private static final List<CatchUpExpConfigDO> configList = new ArrayList<>();

    private CatchUpExpConfigManager() {
    }

    /**
     * 加载配置列表到内存缓存（由 CatchUpExpConfigService 在 @PostConstruct 时调用）
     */
    public static void load(List<CatchUpExpConfigDO> configs) {
        configList.clear();
        // 按 level_min 升序排列，确保查找时优先匹配低等级段
        configs.sort((a, b) -> Integer.compare(a.getLevelMin(), b.getLevelMin()));
        configList.addAll(configs);
    }

    /**
     * 根据角色等级获取追赶经验倍率。
     * 遍历所有已启用的配置，返回第一个匹配 levelMin <= characterLevel <= levelMax 的倍率。
     * 若无匹配配置，返回 1.0（即不改变经验值）。
     *
     * @param characterLevel 角色当前等级
     * @return 经验倍率，最小为 1.0
     */
    public static float getMultiplier(int characterLevel) {
        for (CatchUpExpConfigDO config : configList) {
            if (config.getEnabled() != null && config.getEnabled() == 1
                    && config.getLevelMin() != null && config.getLevelMax() != null
                    && config.getExpMultiplier() != null
                    && characterLevel >= config.getLevelMin()
                    && characterLevel <= config.getLevelMax()) {
                return config.getExpMultiplier().floatValue();
            }
        }
        return 1.0f;
    }

    /**
     * 新增配置后刷新缓存
     */
    public static synchronized void addConfig(CatchUpExpConfigDO config) {
        if (config.getEnabled() != null && config.getEnabled() == 1) {
            configList.add(config);
            configList.sort((a, b) -> Integer.compare(a.getLevelMin(), b.getLevelMin()));
        }
    }

    /**
     * 更新配置后刷新缓存
     */
    public static synchronized void updateConfig(CatchUpExpConfigDO oldConfig, CatchUpExpConfigDO newConfig) {
        configList.removeIf(c -> c.getId().equals(oldConfig.getId()));
        if (newConfig.getEnabled() != null && newConfig.getEnabled() == 1) {
            configList.add(newConfig);
            configList.sort((a, b) -> Integer.compare(a.getLevelMin(), b.getLevelMin()));
        }
    }

    /**
     * 删除配置后刷新缓存
     */
    public static synchronized void removeConfig(Long id) {
        configList.removeIf(c -> c.getId().equals(id));
    }
}