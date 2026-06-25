package org.gms.service;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.config.ExtraDamageConfigManager;
import org.gms.dao.entity.ExtraDamageConfigDO;
import org.gms.dao.mapper.ExtraDamageConfigMapper;
import org.springframework.stereotype.Service;

/**
 * 额外伤害全局配置服务类。
 * <p>
 * 启动时从数据库加载配置到 {@link ExtraDamageConfigManager} 内存缓存。
 * </p>
 */
@Slf4j
@Service
@AllArgsConstructor
public class ExtraDamageConfigService {

    private final ExtraDamageConfigMapper extraDamageConfigMapper;

    @PostConstruct
    public void init() {
        refreshCache();
        log.info("额外伤害配置加载完成");
    }

    /** 重新从数据库加载配置到内存缓存 */
    public void refreshCache() {
        ExtraDamageConfigDO config = extraDamageConfigMapper.selectOneById(1L);
        ExtraDamageConfigManager.load(config);
    }

    /** 获取当前配置（供管理后台读取） */
    public ExtraDamageConfigDO getConfig() {
        return extraDamageConfigMapper.selectOneById(1L);
    }

    /** 更新配置并同步刷新缓存（供管理后台调用） */
    public void updateConfig(ExtraDamageConfigDO config) {
        config.setId(1L);
        extraDamageConfigMapper.update(config);
        refreshCache();
    }
}
