package org.gms.service;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.config.IndependentDropManager;
import org.gms.dao.entity.IndependentDropConfigDO;
import org.gms.dao.mapper.IndependentDropConfigMapper;
import org.gms.model.dto.IndependentDropSaveDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 独立掉落配置服务
 * 管理 xy_independent_drop_config 表的增删改查
 */
@Slf4j
@Service
@AllArgsConstructor
public class IndependentDropService {

    private final IndependentDropConfigMapper mapper;

    @PostConstruct
    public void init() {
        refreshCache();
        log.info("独立掉落配置服务初始化完成");
    }

    /** 获取所有配置 */
    public List<IndependentDropConfigDO> getAllConfigs() {
        return mapper.selectAll().stream()
                .sorted(Comparator.comparingLong(IndependentDropConfigDO::getId))
                .collect(Collectors.toList());
    }

    /** 根据ID获取配置 */
    public IndependentDropConfigDO getConfigById(Long id) {
        return mapper.selectOneById(id);
    }

    /** 保存配置（新增或更新） */
    @Transactional
    public IndependentDropConfigDO saveConfig(IndependentDropSaveDTO dto) {
        IndependentDropConfigDO config = IndependentDropConfigDO.builder()
                .id(dto.getId())
                .mobId(dto.getMobId())
                .mobName(dto.getMobName())
                .enabled(dto.getEnabled() != null ? dto.getEnabled() : 1)
                .build();
        if (config.getId() != null) {
            mapper.update(config);
        } else {
            mapper.insert(config);
        }
        refreshCache();
        return getConfigById(config.getId());
    }

    /** 删除配置 */
    @Transactional
    public void deleteConfig(Long id) {
        mapper.deleteById(id);
        refreshCache();
    }

    /** 刷新缓存 */
    private void refreshCache() {
        IndependentDropManager.load(mapper.selectAll());
    }
}
