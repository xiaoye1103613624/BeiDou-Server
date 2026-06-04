package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.config.MedalEnhanceManager;
import org.gms.dao.entity.MedalEnhanceConfigDO;
import org.gms.dao.entity.MedalEnhanceCostDO;
import org.gms.dao.entity.MedalEnhanceLevelDO;
import org.gms.dao.mapper.MedalEnhanceConfigMapper;
import org.gms.dao.mapper.MedalEnhanceCostMapper;
import org.gms.dao.mapper.MedalEnhanceLevelMapper;
import org.gms.model.dto.MedalEnhanceSaveDTO;
import org.gms.model.dto.MedalEnhanceSaveDTO.CostDTO;
import org.gms.model.dto.MedalEnhanceSaveDTO.LevelDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 【业务服务】MedalEnhanceService：勋章强化服务类，负责勋章强化系统的配置管理。
 * 
 * <p>提供勋章强化配置的增删改查操作，包括强化等级属性、成功率、消耗物品等配置。
 * 在配置变更时同步更新 {@link MedalEnhanceManager} 的内存缓存，确保游戏运行时
 * 能实时获取最新的勋章强化配置。</p>
 */
@Slf4j
@Service
@AllArgsConstructor
public class MedalEnhanceService {

    /** 强化配置数据访问接口 */
    private final MedalEnhanceConfigMapper configMapper;
    /** 强化等级数据访问接口 */
    private final MedalEnhanceLevelMapper levelMapper;
    /** 强化消耗数据访问接口 */
    private final MedalEnhanceCostMapper costMapper;

    /**
     * 初始化方法，服务启动时加载配置到缓存。
     * 
     * <p>使用 {@link @PostConstruct} 注解，在Spring容器初始化完成后自动调用，
     * 将数据库中的勋章强化配置加载到内存缓存中。</p>
     */
    @PostConstruct
    public void init() {
        refreshCache();
        log.info("勋章强化配置加载完成");
    }

    /**
     * 获取所有勋章强化配置列表。
     * 
     * <p>查询所有配置，并关联其等级和消耗物品信息，转换为DTO返回。</p>
     * 
     * @return 强化配置DTO列表
     */
    public List<MedalEnhanceSaveDTO> getConfigList() {
        List<MedalEnhanceConfigDO> configs = configMapper.selectAll();
        List<MedalEnhanceLevelDO> allLevels = levelMapper.selectAll();
        List<MedalEnhanceCostDO> allCosts = costMapper.selectAll();

        List<MedalEnhanceSaveDTO> result = new ArrayList<>();
        for (MedalEnhanceConfigDO config : configs) {
            result.add(toDTO(config, allLevels, allCosts));
        }
        return result;
    }

    /**
     * 根据ID获取勋章强化配置。
     * 
     * @param id 配置ID
     * @return 强化配置DTO，不存在返回null
     */
    public MedalEnhanceSaveDTO getConfigById(Long id) {
        MedalEnhanceConfigDO config = configMapper.selectOneById(id);
        if (config == null) return null;
        List<MedalEnhanceLevelDO> allLevels = levelMapper.selectAll();
        List<MedalEnhanceCostDO> allCosts = costMapper.selectAll();
        return toDTO(config, allLevels, allCosts);
    }

    /**
     * 保存勋章强化配置（新增或更新）。
     * 
     * <p>支持新增和更新操作：
     * <ul>
     *   <li>新增：直接插入配置，然后保存等级和消耗物品</li>
     *   <li>更新：先更新配置，删除原有等级，再保存新的等级和消耗物品</li>
     * </ul>
     * 所有字段支持空值默认值处理。保存成功后刷新内存缓存。</p>
     * 
     * @param dto 强化配置DTO
     * @return 保存后的配置DTO
     */
    @Transactional
    public MedalEnhanceSaveDTO saveConfig(MedalEnhanceSaveDTO dto) {
        // 构建配置实体，处理空值默认值
        MedalEnhanceConfigDO config = MedalEnhanceConfigDO.builder()
                .id(dto.getId())
                .maxEnhance(dto.getMaxEnhance() != null ? dto.getMaxEnhance() : 10)
                .enabled(dto.getEnabled() != null ? dto.getEnabled() : 1)
                .build();
        
        if (config.getId() != null) {
            // 更新操作：先更新配置，再删除原有等级
            configMapper.update(config);
            deleteLevelsByConfigId(config.getId());
        } else {
            // 新增操作：直接插入配置
            configMapper.insert(config);
        }

        // 保存等级及消耗物品
        if (dto.getLevels() != null) {
            for (LevelDTO lv : dto.getLevels()) {
                MedalEnhanceLevelDO level = MedalEnhanceLevelDO.builder()
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

                // 保存消耗物品
                if (lv.getCosts() != null) {
                    for (CostDTO co : lv.getCosts()) {
                        MedalEnhanceCostDO cost = MedalEnhanceCostDO.builder()
                                .levelId(level.getId())
                                .itemId(co.getItemId())
                                .count(co.getCount() != null ? co.getCount() : 1)
                                .build();
                        costMapper.insert(cost);
                    }
                }
            }
        }

        // 刷新内存缓存
        refreshCache();
        return getConfigById(config.getId());
    }

    /**
     * 删除勋章强化配置。
     * 
     * <p>级联删除关联的等级和消耗物品，然后删除配置本身。</p>
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
     * 根据配置ID删除所有关联的等级和消耗物品。
     * 
     * <p>先删除等级关联的消耗物品，再删除等级记录。</p>
     * 
     * @param configId 配置ID
     */
    private void deleteLevelsByConfigId(Long configId) {
        // 查询该配置下所有等级
        List<MedalEnhanceLevelDO> existingLevels = levelMapper.selectListByQuery(
                QueryWrapper.create().where("config_id", configId));
        // 先删除等级下的消耗物品，再删除等级
        for (MedalEnhanceLevelDO lv : existingLevels) {
            costMapper.deleteByQuery(
                    QueryWrapper.create().where("level_id", lv.getId()));
        }
        levelMapper.deleteByQuery(
                QueryWrapper.create().where("config_id", configId));
    }

    /**
     * 刷新缓存，将数据库配置加载到内存。
     * 
     * <p>调用 {@link MedalEnhanceManager#load(List, List, List)} 方法，
     * 将数据库中的配置重新加载到内存缓存中。</p>
     */
    private void refreshCache() {
        MedalEnhanceManager.load(configMapper.selectAll(), levelMapper.selectAll(), costMapper.selectAll());
    }

    /**
     * 将DO转换为DTO。
     * 
     * <p>将配置实体、等级实体和消耗物品实体转换为DTO格式，便于前端展示。</p>
     * 
     * @param config 配置DO
     * @param allLevels 所有等级DO列表
     * @param allCosts 所有消耗DO列表
     * @return 强化配置DTO
     */
    private MedalEnhanceSaveDTO toDTO(MedalEnhanceConfigDO config,
                                      List<MedalEnhanceLevelDO> allLevels,
                                      List<MedalEnhanceCostDO> allCosts) {
        List<LevelDTO> levelDTOs = new ArrayList<>();
        // 筛选当前配置的等级并转换
        for (MedalEnhanceLevelDO lv : allLevels) {
            if (!lv.getConfigId().equals(config.getId())) continue;
            List<CostDTO> costDTOs = new ArrayList<>();
            // 筛选当前等级的消耗物品并转换
            for (MedalEnhanceCostDO co : allCosts) {
                if (co.getLevelId().equals(lv.getId())) {
                    costDTOs.add(CostDTO.builder()
                            .id(co.getId()).itemId(co.getItemId()).count(co.getCount()).build());
                }
            }
            levelDTOs.add(LevelDTO.builder()
                    .id(lv.getId()).enhanceLevel(lv.getEnhanceLevel())
                    .successRate(lv.getSuccessRate()).destroyOnFail(lv.getDestroyOnFail())
                    .mesoCost(lv.getMesoCost())
                    .strAdd(lv.getStrAdd()).dexAdd(lv.getDexAdd())
                    .intAdd(lv.getIntAdd()).lukAdd(lv.getLukAdd())
                    .hpAdd(lv.getHpAdd()).mpAdd(lv.getMpAdd())
                    .watkAdd(lv.getWatkAdd()).matkAdd(lv.getMatkAdd())
                    .wdefAdd(lv.getWdefAdd()).mdefAdd(lv.getMdefAdd())
                    .accAdd(lv.getAccAdd()).avoidAdd(lv.getAvoidAdd())
                    .speedAdd(lv.getSpeedAdd()).jumpAdd(lv.getJumpAdd())
                    .costs(costDTOs).build());
        }
        // 按强化等级排序
        levelDTOs.sort(java.util.Comparator.comparingInt(LevelDTO::getEnhanceLevel));
        return MedalEnhanceSaveDTO.builder()
                .id(config.getId()).maxEnhance(config.getMaxEnhance())
                .enabled(config.getEnabled()).levels(levelDTOs).build();
    }
}