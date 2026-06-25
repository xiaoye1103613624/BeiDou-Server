package org.gms.service;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.config.EquipDamageBonusManager;
import org.gms.dao.entity.EquipDamageBonusConfigDO;
import org.gms.dao.mapper.EquipDamageBonusConfigMapper;
import org.gms.model.dto.EquipDamageBonusConfigDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 装备伤害加成配置服务类，负责按单件装备的增删改查，并在变更后刷新缓存。
 */
@Slf4j
@Service
@AllArgsConstructor
public class EquipDamageBonusConfigService {

    private final EquipDamageBonusConfigMapper mapper;

    @PostConstruct
    public void init() {
        refreshCache();
    }

    /** 从数据库重新加载配置到 {@link EquipDamageBonusManager} 缓存 */
    public void refreshCache() {
        EquipDamageBonusManager.load(mapper.selectAll());
    }

    /**
     * 获取所有装备伤害加成配置列表。
     */
    public List<EquipDamageBonusConfigDTO> getConfigList() {
        List<EquipDamageBonusConfigDTO> result = new ArrayList<>();
        for (EquipDamageBonusConfigDO d : mapper.selectAll()) {
            result.add(toDTO(d));
        }
        return result;
    }

    /**
     * 根据ID获取单个配置。
     */
    public EquipDamageBonusConfigDTO getConfig(Long id) {
        EquipDamageBonusConfigDO d = mapper.selectOneById(id);
        return d == null ? null : toDTO(d);
    }

    /**
     * 保存配置（新增或更新），保存后刷新缓存。
     */
    public EquipDamageBonusConfigDTO saveConfig(EquipDamageBonusConfigDTO dto) {
        EquipDamageBonusConfigDO entity = EquipDamageBonusConfigDO.builder()
                .id(dto.getId())
                .itemId(dto.getItemId())
                .itemName(dto.getItemName())
                .damagePct(dto.getDamagePct() != null ? dto.getDamagePct() : 0)
                .bossDamagePct(dto.getBossDamagePct() != null ? dto.getBossDamagePct() : 0)
                .fixedDamage(dto.getFixedDamage() != null ? dto.getFixedDamage() : 0L)
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
        EquipDamageBonusConfigDO d = mapper.selectOneById(id);
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

    private EquipDamageBonusConfigDTO toDTO(EquipDamageBonusConfigDO d) {
        return EquipDamageBonusConfigDTO.builder()
                .id(d.getId())
                .itemId(d.getItemId())
                .itemName(d.getItemName())
                .damagePct(d.getDamagePct())
                .bossDamagePct(d.getBossDamagePct())
                .fixedDamage(d.getFixedDamage())
                .enabled(d.getEnabled())
                .build();
    }
}
