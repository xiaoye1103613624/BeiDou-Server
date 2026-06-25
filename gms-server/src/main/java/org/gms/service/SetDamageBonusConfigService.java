package org.gms.service;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.config.SetDamageBonusManager;
import org.gms.dao.entity.SetDamageBonusConfigDO;
import org.gms.dao.mapper.SetDamageBonusConfigMapper;
import org.gms.model.dto.SetDamageBonusConfigDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 套装伤害加成配置服务类，负责按"套装ID+件数档位"的增删改查，并在变更后刷新缓存。
 */
@Slf4j
@Service
@AllArgsConstructor
public class SetDamageBonusConfigService {

    private final SetDamageBonusConfigMapper mapper;

    @PostConstruct
    public void init() {
        refreshCache();
    }

    /** 从数据库重新加载配置到 {@link SetDamageBonusManager} 缓存 */
    public void refreshCache() {
        SetDamageBonusManager.load(mapper.selectAll());
    }

    /**
     * 获取所有套装伤害加成配置列表。
     */
    public List<SetDamageBonusConfigDTO> getConfigList() {
        List<SetDamageBonusConfigDTO> result = new ArrayList<>();
        for (SetDamageBonusConfigDO d : mapper.selectAll()) {
            result.add(toDTO(d));
        }
        return result;
    }

    /**
     * 根据ID获取单个配置。
     */
    public SetDamageBonusConfigDTO getConfig(Long id) {
        SetDamageBonusConfigDO d = mapper.selectOneById(id);
        return d == null ? null : toDTO(d);
    }

    /**
     * 保存配置（新增或更新），保存后刷新缓存。
     */
    public SetDamageBonusConfigDTO saveConfig(SetDamageBonusConfigDTO dto) {
        SetDamageBonusConfigDO entity = SetDamageBonusConfigDO.builder()
                .id(dto.getId())
                .setItemId(dto.getSetItemId())
                .setName(dto.getSetName())
                .tierCount(dto.getTierCount())
                .damagePct(dto.getDamagePct() != null ? dto.getDamagePct() : 0)
                .bossDamagePct(dto.getBossDamagePct() != null ? dto.getBossDamagePct() : 0)
                .enabled(dto.getEnabled() != null ? dto.getEnabled() : 1)
                .build();
        if (entity.getId() != null) {
            mapper.update(entity);
        } else {
            mapper.insert(entity);
        }
        refreshCache();
        return toDTO(mapper.selectOneById(entity.getId()));
    }

    /**
     * 切换启用状态。
     */
    public void toggleEnabled(Long id) {
        SetDamageBonusConfigDO d = mapper.selectOneById(id);
        if (d == null) {
            return;
        }
        d.setEnabled(d.getEnabled() != null && d.getEnabled() == 1 ? 0 : 1);
        mapper.update(d);
        refreshCache();
    }

    /**
     * 删除配置，删除后刷新缓存。
     */
    public void deleteConfig(Long id) {
        mapper.deleteById(id);
        refreshCache();
    }

    private SetDamageBonusConfigDTO toDTO(SetDamageBonusConfigDO d) {
        return SetDamageBonusConfigDTO.builder()
                .id(d.getId())
                .setItemId(d.getSetItemId())
                .setName(d.getSetName())
                .tierCount(d.getTierCount())
                .damagePct(d.getDamagePct())
                .bossDamagePct(d.getBossDamagePct())
                .enabled(d.getEnabled())
                .build();
    }
}
