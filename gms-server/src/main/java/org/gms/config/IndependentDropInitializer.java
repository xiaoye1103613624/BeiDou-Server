package org.gms.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.dao.mapper.IndependentDropConfigMapper;
import org.springframework.stereotype.Component;

/**
 * 独立掉落配置初始化器
 * 服务器启动时从数据库加载独立掉落怪物列表到缓存
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IndependentDropInitializer {

    private final IndependentDropConfigMapper mapper;

    @PostConstruct
    public void init() {
        IndependentDropManager.load(mapper.selectAll());
        log.info("独立掉落配置初始化完成");
    }
}
