package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.config.EquipEnhanceManager;
import org.gms.dao.entity.EquipEnhanceConfigDO;
import org.gms.dao.entity.EquipEnhanceCostDO;
import org.gms.dao.entity.EquipEnhanceLevelDO;
import org.gms.dao.mapper.EquipEnhanceConfigMapper;
import org.gms.dao.mapper.EquipEnhanceCostMapper;
import org.gms.dao.mapper.EquipEnhanceLevelMapper;
import org.gms.model.dto.EquipEnhanceSaveDTO;
import org.gms.model.dto.EquipEnhanceSaveDTO.CostDTO;
import org.gms.model.dto.EquipEnhanceSaveDTO.LevelDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 装备强化服务类，负责装备强化系统的配置管理。
 * <p>
 * 提供装备强化配置的增删改查操作，包括强化配置、等级属性、消耗物品等，
 * 并在配置变更时同步更新 {@link EquipEnhanceManager} 的内存缓存。
 * </p>
 */
@Slf4j
@Service
@AllArgsConstructor
public class EquipEnhanceService {

    private final EquipEnhanceConfigMapper configMapper;
    private final EquipEnhanceLevelMapper levelMapper;
    private final EquipEnhanceCostMapper costMapper;

    /**
     * 初始化方法，在 Spring 容器启动时执行。
     * 从数据库加载所有装备强化配置并初始化到 EquipEnhanceManager。
     */
    @PostConstruct
    public void init() {
        refreshCache();
        log.info("装备强化配置加载完成");
    }

    /** 获取所有装备强化配置列表 */
    public List<EquipEnhanceSaveDTO> getConfigList() {
        List<EquipEnhanceConfigDO> configs = configMapper.selectAll();
        List<EquipEnhanceLevelDO> allLevels = levelMapper.selectAll();
        List<EquipEnhanceCostDO> allCosts = costMapper.selectAll();

        List<EquipEnhanceSaveDTO> result = new ArrayList<>();
        for (EquipEnhanceConfigDO config : configs) {
            result.add(toDTO(config, allLevels, allCosts));
        }
        return result;
    }

    /** 根据ID获取装备强化配置 */
    public EquipEnhanceSaveDTO getConfigById(Long id) {
        EquipEnhanceConfigDO config = configMapper.selectOneById(id);
        if (config == null) return null;
        return toDTO(config, levelMapper.selectAll(), costMapper.selectAll());
    }

    /**
     * 保存装备强化配置（新增或更新）。
     * 所有字段支持空值默认值处理。
     */
    @Transactional
    public EquipEnhanceSaveDTO saveConfig(EquipEnhanceSaveDTO dto) {
        EquipEnhanceConfigDO config = EquipEnhanceConfigDO.builder()
                .id(dto.getId())
                .itemId(dto.getItemId())
                .itemName(dto.getItemName())
                .uniquePerChar(dto.getUniquePerChar() != null ? dto.getUniquePerChar() : 0)
                .maxEnhance(dto.getMaxEnhance() != null ? dto.getMaxEnhance() : 10)
                .enabled(dto.getEnabled() != null ? dto.getEnabled() : 1)
                .build();
        if (config.getId() != null) {
            configMapper.update(config);
            deleteLevelsByConfigId(config.getId());
        } else {
            configMapper.insert(config);
        }

        if (dto.getLevels() != null) {
            for (LevelDTO lv : dto.getLevels()) {
                EquipEnhanceLevelDO level = EquipEnhanceLevelDO.builder()
                        .configId(config.getId())
                        .enhanceLevel(lv.getEnhanceLevel())
                        .successRate(lv.getSuccessRate() != null ? lv.getSuccessRate() : 100)
                        .destroyOnFail(lv.getDestroyOnFail() != null ? lv.getDestroyOnFail() : 0)
                        .mesoCost(lv.getMesoCost() != null ? lv.getMesoCost() : 0)
                        .strAdd(lv.getStrAdd() != null ? lv.getStrAdd() : 0)
                        .dexAdd(lv.getDexAdd() != null ? lv.getDexAdd() : 0)
                        .intAdd(lv.getIntAdd() != null ? lv.getIntAdd() : 0)
                        .lukAdd(lv.getLukAdd() != null ? lv.getLukAdd() : 0)
                        .hpAdd(lv.getHpAdd() != null ? lv.getHpAdd() : 0)
                        .mpAdd(lv.getMpAdd() != null ? lv.getMpAdd() : 0)
                        .watkAdd(lv.getWatkAdd() != null ? lv.getWatkAdd() : 0)
                        .matkAdd(lv.getMatkAdd() != null ? lv.getMatkAdd() : 0)
                        .wdefAdd(lv.getWdefAdd() != null ? lv.getWdefAdd() : 0)
                        .mdefAdd(lv.getMdefAdd() != null ? lv.getMdefAdd() : 0)
                        .accAdd(lv.getAccAdd() != null ? lv.getAccAdd() : 0)
                        .avoidAdd(lv.getAvoidAdd() != null ? lv.getAvoidAdd() : 0)
                        .speedAdd(lv.getSpeedAdd() != null ? lv.getSpeedAdd() : 0)
                        .jumpAdd(lv.getJumpAdd() != null ? lv.getJumpAdd() : 0)
                        .build();
                levelMapper.insert(level);

                if (lv.getCosts() != null) {
                    for (CostDTO co : lv.getCosts()) {
                        EquipEnhanceCostDO cost = EquipEnhanceCostDO.builder()
                                .levelId(level.getId())
                                .itemId(co.getItemId())
                                .count(co.getCount() != null ? co.getCount() : 1)
                                .build();
                        costMapper.insert(cost);
                    }
                }
            }
        }

        refreshCache();
        return getConfigById(config.getId());
    }

    /** 删除装备强化配置（级联删除等级和消耗物品） */
    @Transactional
    public void deleteConfig(Long id) {
        deleteLevelsByConfigId(id);
        configMapper.deleteById(id);
        refreshCache();
    }

    /** 根据配置ID删除所有关联的等级和消耗物品 */
    private void deleteLevelsByConfigId(Long configId) {
        List<EquipEnhanceLevelDO> existingLevels = levelMapper.selectListByQuery(
                QueryWrapper.create().where("config_id = ?", configId));
        for (EquipEnhanceLevelDO lv : existingLevels) {
            costMapper.deleteByQuery(
                    QueryWrapper.create().where("level_id = ?", lv.getId()));
        }
        levelMapper.deleteByQuery(QueryWrapper.create().where("config_id = ?", configId));
    }

    /** 刷新缓存 */
    private void refreshCache() {
        EquipEnhanceManager.load(configMapper.selectAll(), levelMapper.selectAll(), costMapper.selectAll());
    }

    /** DO转DTO */
    private EquipEnhanceSaveDTO toDTO(EquipEnhanceConfigDO config,
                                      List<EquipEnhanceLevelDO> allLevels,
                                      List<EquipEnhanceCostDO> allCosts) {
        List<LevelDTO> levelDTOs = new ArrayList<>();
        for (EquipEnhanceLevelDO lv : allLevels) {
            if (!lv.getConfigId().equals(config.getId())) continue;
            List<CostDTO> costDTOs = new ArrayList<>();
            for (EquipEnhanceCostDO co : allCosts) {
                if (co.getLevelId().equals(lv.getId())) {
                    costDTOs.add(CostDTO.builder().id(co.getId()).itemId(co.getItemId()).count(co.getCount()).build());
                }
            }
            levelDTOs.add(LevelDTO.builder()
                    .id(lv.getId()).enhanceLevel(lv.getEnhanceLevel())
                    .successRate(lv.getSuccessRate()).destroyOnFail(lv.getDestroyOnFail())
                    .mesoCost(lv.getMesoCost())
                    .strAdd(lv.getStrAdd()).dexAdd(lv.getDexAdd()).intAdd(lv.getIntAdd()).lukAdd(lv.getLukAdd())
                    .hpAdd(lv.getHpAdd()).mpAdd(lv.getMpAdd())
                    .watkAdd(lv.getWatkAdd()).matkAdd(lv.getMatkAdd())
                    .wdefAdd(lv.getWdefAdd()).mdefAdd(lv.getMdefAdd())
                    .accAdd(lv.getAccAdd()).avoidAdd(lv.getAvoidAdd())
                    .speedAdd(lv.getSpeedAdd()).jumpAdd(lv.getJumpAdd())
                    .costs(costDTOs).build());
        }
        levelDTOs.sort(java.util.Comparator.comparingInt(LevelDTO::getEnhanceLevel));
        return EquipEnhanceSaveDTO.builder()
                .id(config.getId()).itemId(config.getItemId()).itemName(config.getItemName())
                .uniquePerChar(config.getUniquePerChar()).maxEnhance(config.getMaxEnhance())
                .enabled(config.getEnabled()).levels(levelDTOs).build();
    }
}
