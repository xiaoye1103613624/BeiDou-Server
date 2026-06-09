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

    /**
     * 装备强化配置数据访问对象
     */
    private final EquipEnhanceConfigMapper configMapper;
    /**
     * 装备强化等级数据访问对象
     */
    private final EquipEnhanceLevelMapper levelMapper;
    /**
     * 装备强化消耗物品数据访问对象
     */
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

    /**
     * 获取所有装备强化配置列表
     * 加载全部配置、等级和消耗物品数据，组装为DTO列表
     *
     * @return 装备强化配置DTO列表
     */
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

    /**
     * 根据ID获取装备强化配置
     *
     * @param id 配置ID
     * @return 装备强化配置DTO，不存在返回null
     */
    public EquipEnhanceSaveDTO getConfigById(Long id) {
        EquipEnhanceConfigDO config = configMapper.selectOneById(id);
        if (config == null) return null;
        return toDTO(config, levelMapper.selectAll(), costMapper.selectAll());
    }

    /**
     * 保存装备强化配置（新增或更新）。
     * 存在ID时更新并删除旧等级重新创建，不存在ID时插入新配置。
     * 所有字段支持空值默认值处理。
     *
     * @param dto 装备强化保存DTO
     * @return 保存后的装备强化配置DTO
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
            // 更新配置并清除旧等级数据
            configMapper.update(config);
            deleteLevelsByConfigId(config.getId());
        } else {
            // 新增配置
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

    /**
     * 切换装备强化配置的启用/禁用状态
     * 仅更新 enabled 字段并刷新缓存
     *
     * @param id      配置ID
     * @param enabled 启用状态（1启用，0禁用）
     */
    @Transactional
    public void toggleEnabled(Long id, Integer enabled) {
        EquipEnhanceConfigDO config = EquipEnhanceConfigDO.builder()
                .id(id)
                .enabled(enabled)
                .build();
        configMapper.update(config);
        refreshCache();
    }

    /**
     * 删除装备强化配置（级联删除等级和消耗物品）
     *
     * @param id 配置ID
     */
    @Transactional
    public void deleteConfig(Long id) {
        deleteLevelsByConfigId(id);
        configMapper.deleteById(id);
        refreshCache();
    }

    /**
     * 根据配置ID删除所有关联的等级和消耗物品
     * 先删除每个等级的消耗物品，再删除等级记录
     *
     * @param configId 配置ID
     */
    private void deleteLevelsByConfigId(Long configId) {
        List<EquipEnhanceLevelDO> existingLevels = levelMapper.selectListByQuery(
                QueryWrapper.create().where("config_id = ?", configId));
        for (EquipEnhanceLevelDO lv : existingLevels) {
            costMapper.deleteByQuery(
                    QueryWrapper.create().where("level_id = ?", lv.getId()));
        }
        levelMapper.deleteByQuery(QueryWrapper.create().where("config_id = ?", configId));
    }

    /**
     * 刷新内存缓存
     * 从数据库重新加载所有配置到 EquipEnhanceManager
     */
    private void refreshCache() {
        EquipEnhanceManager.load(configMapper.selectAll(), levelMapper.selectAll(), costMapper.selectAll());
    }

    /**
     * DO转DTO
     * 将数据库实体转换为前端使用的DTO对象
     *
     * @param config    配置实体
     * @param allLevels 所有等级列表
     * @param allCosts  所有消耗物品列表
     * @return 装备强化配置DTO
     */
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
        // 按强化等级排序
        levelDTOs.sort(java.util.Comparator.comparingInt(LevelDTO::getEnhanceLevel));
        return EquipEnhanceSaveDTO.builder()
                .id(config.getId()).itemId(config.getItemId()).itemName(config.getItemName())
                .uniquePerChar(config.getUniquePerChar()).maxEnhance(config.getMaxEnhance())
                .enabled(config.getEnabled()).levels(levelDTOs).build();
    }
}