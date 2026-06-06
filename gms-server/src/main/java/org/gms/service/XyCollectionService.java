package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.config.XyCollectionManager;
import org.gms.dao.entity.XyCollectionStageDO;
import org.gms.dao.entity.XyCollectionStageItemDO;
import org.gms.dao.entity.XyCollectionTypeDO;
import org.gms.dao.mapper.XyCollectionStageItemMapper;
import org.gms.dao.mapper.XyCollectionStageMapper;
import org.gms.dao.mapper.XyCollectionTypeMapper;
import org.gms.model.dto.XyCollectionSaveDTO;
import org.gms.model.dto.XyCollectionSaveDTO.ItemDTO;
import org.gms.model.dto.XyCollectionSaveDTO.StageDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 【业务服务】XyCollectionService：XY收集服务类，负责XY收集系统的配置管理。
 * 
 * <p>提供XY收集配置的增删改查操作，包括收集类型、阶段、物品的管理。
 * 在配置变更时同步更新 {@link XyCollectionManager} 的内存缓存，确保游戏运行时
 * 能实时获取最新的XY收集配置。</p>
 * 
 * <p>XY收集系统结构：
 * <ul>
 *   <li>类型(Type)：收集活动的分类（如节日收集、成就收集等）</li>
 *   <li>阶段(Stage)：每个类型下的不同阶段，完成阶段可获得奖励</li>
 *   <li>物品(Item)：每个阶段需要收集的物品列表</li>
 * </ul></p>
 */
@Slf4j
@Service
@AllArgsConstructor
public class XyCollectionService {

    /** 收集类型数据访问接口 */
    private final XyCollectionTypeMapper typeMapper;
    /** 收集阶段数据访问接口 */
    private final XyCollectionStageMapper stageMapper;
    /** 阶段物品数据访问接口 */
    private final XyCollectionStageItemMapper itemMapper;

    /**
     * 初始化方法，服务启动时加载配置到缓存。
     * 
     * <p>使用 {@link @PostConstruct} 注解，在Spring容器初始化完成后自动调用，
     * 将数据库中的XY收集配置加载到内存缓存中。</p>
     */
    @PostConstruct
    public void init() {
        refreshCache();
        log.info("XY收集配置加载完成");
    }

    /**
     * 获取所有收集配置列表。
     * 
     * <p>查询所有收集类型，并关联其阶段和物品信息，转换为DTO返回。</p>
     * 
     * @return 收集配置DTO列表
     */
    public List<XyCollectionSaveDTO> getConfigList() {
        List<XyCollectionTypeDO> types = typeMapper.selectAll();
        List<XyCollectionStageDO> allStages = stageMapper.selectAll();
        List<XyCollectionStageItemDO> allItems = itemMapper.selectAll();

        List<XyCollectionSaveDTO> result = new ArrayList<>();
        for (XyCollectionTypeDO type : types) {
            result.add(toDTO(type, allStages, allItems));
        }
        return result;
    }

    /**
     * 根据ID获取收集配置。
     * 
     * @param id 配置ID
     * @return 收集配置DTO，不存在返回null
     */
    public XyCollectionSaveDTO getConfigById(Long id) {
        XyCollectionTypeDO type = typeMapper.selectOneById(id);
        if (type == null) return null;
        List<XyCollectionStageDO> allStages = stageMapper.selectAll();
        List<XyCollectionStageItemDO> allItems = itemMapper.selectAll();
        return toDTO(type, allStages, allItems);
    }

    /**
     * 保存收集配置（新增或更新）。
     * 
     * <p>支持新增和更新操作：
     * <ul>
     *   <li>新增：直接插入类型，然后保存阶段和物品</li>
     *   <li>更新：先更新类型，删除原有阶段，再保存新的阶段和物品</li>
     * </ul>
     * 所有字段支持空值默认值处理。保存成功后刷新内存缓存。</p>
     * 
     * @param dto 收集配置DTO
     * @return 保存后的配置DTO
     */
    @Transactional
    public XyCollectionSaveDTO saveConfig(XyCollectionSaveDTO dto) {
        // 构建类型实体，处理空值默认值
        XyCollectionTypeDO type = XyCollectionTypeDO.builder()
                .id(dto.getId())
                .typeName(dto.getTypeName())
                .description(dto.getDescription())
                .sortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0)
                .enabled(dto.getEnabled() != null ? dto.getEnabled() : 1)
                .rewardType(dto.getRewardType())
                .rewardAmount(dto.getRewardAmount() != null ? dto.getRewardAmount() : 0)
                .build();
        
        if (type.getId() != null) {
            // 更新操作：先更新类型，再删除原有阶段
            typeMapper.update(type);
            deleteStagesByTypeId(type.getId());
        } else {
            // 新增操作：直接插入类型
            typeMapper.insert(type);
        }

        // 保存阶段及物品
        if (dto.getStages() != null) {
            for (StageDTO stageDTO : dto.getStages()) {
                XyCollectionStageDO stage = XyCollectionStageDO.builder()
                        .typeId(type.getId())
                        .stageName(stageDTO.getStageName())
                        .sortOrder(stageDTO.getSortOrder() != null ? stageDTO.getSortOrder() : 0)
                        .rewardType(stageDTO.getRewardType())
                        .rewardAmount(stageDTO.getRewardAmount() != null ? stageDTO.getRewardAmount() : 0)
                        .build();
                stageMapper.insert(stage);

                // 保存阶段物品
                if (stageDTO.getItems() != null) {
                    for (ItemDTO itemDTO : stageDTO.getItems()) {
                        XyCollectionStageItemDO item = XyCollectionStageItemDO.builder()
                                .stageId(stage.getId())
                                .itemId(itemDTO.getItemId())
                                .quantity(itemDTO.getQuantity() != null ? itemDTO.getQuantity() : 1)
                                .sortOrder(itemDTO.getSortOrder() != null ? itemDTO.getSortOrder() : 0)
                                .build();
                        itemMapper.insert(item);
                    }
                }
            }
        }

        // 刷新内存缓存
        refreshCache();
        return getConfigById(type.getId());
    }

    /**
     * 删除收集配置。
     * 
     * <p>级联删除关联的阶段和物品，然后删除类型本身。</p>
     * 
     * @param id 配置ID
     */
    @Transactional
    public void deleteConfig(Long id) {
        deleteStagesByTypeId(id);
        typeMapper.deleteById(id);
        refreshCache();
    }

    /**
     * 根据类型ID删除所有关联的阶段和物品。
     * 
     * <p>先删除阶段关联的物品，再删除阶段记录。</p>
     * 
     * @param typeId 类型ID
     */
    private void deleteStagesByTypeId(Long typeId) {
        // 查询该类型下所有阶段
        List<XyCollectionStageDO> existingStages = stageMapper.selectListByQuery(
                QueryWrapper.create().where("type_id = ?", typeId));
        // 先删除阶段下的物品，再删除阶段
        for (XyCollectionStageDO stage : existingStages) {
            itemMapper.deleteByQuery(
                    QueryWrapper.create().where("stage_id = ?", stage.getId()));
        }
        stageMapper.deleteByQuery(
                QueryWrapper.create().where("type_id = ?", typeId));
    }

    /**
     * 刷新缓存，将数据库配置加载到内存。
     * 
     * <p>调用 {@link XyCollectionManager#load(List, List, List)} 方法，
     * 将数据库中的配置重新加载到内存缓存中。</p>
     */
    private void refreshCache() {
        XyCollectionManager.load(typeMapper.selectAll(), stageMapper.selectAll(), itemMapper.selectAll());
    }

    /**
     * 将DO转换为DTO。
     * 
     * <p>将类型实体、阶段实体和物品实体转换为DTO格式，便于前端展示。
     * 阶段和物品按sortOrder字段排序。</p>
     * 
     * @param type 类型DO
     * @param allStages 所有阶段DO列表
     * @param allItems 所有物品DO列表
     * @return 收集配置DTO
     */
    private XyCollectionSaveDTO toDTO(XyCollectionTypeDO type,
                                       List<XyCollectionStageDO> allStages,
                                       List<XyCollectionStageItemDO> allItems) {
        List<StageDTO> stageDTOs = new ArrayList<>();
        // 筛选当前类型的阶段并转换
        for (XyCollectionStageDO stage : allStages) {
            if (!stage.getTypeId().equals(type.getId())) continue;
            List<ItemDTO> itemDTOs = new ArrayList<>();
            // 筛选当前阶段的物品并转换
            for (XyCollectionStageItemDO item : allItems) {
                if (item.getStageId().equals(stage.getId())) {
                    itemDTOs.add(ItemDTO.builder()
                            .id(item.getId()).itemId(item.getItemId())
                            .quantity(item.getQuantity()).sortOrder(item.getSortOrder()).build());
                }
            }
            // 按排序字段排序物品
            itemDTOs.sort(java.util.Comparator.comparingInt(
                    i -> i.getSortOrder() != null ? i.getSortOrder() : 0));
            stageDTOs.add(StageDTO.builder()
                    .id(stage.getId()).stageName(stage.getStageName())
                    .sortOrder(stage.getSortOrder())
                    .rewardType(stage.getRewardType()).rewardAmount(stage.getRewardAmount())
                    .items(itemDTOs).build());
        }
        // 按排序字段排序阶段
        stageDTOs.sort(java.util.Comparator.comparingInt(
                s -> s.getSortOrder() != null ? s.getSortOrder() : 0));
        return XyCollectionSaveDTO.builder()
                .id(type.getId()).typeName(type.getTypeName()).description(type.getDescription())
                .sortOrder(type.getSortOrder()).enabled(type.getEnabled())
                .rewardType(type.getRewardType()).rewardAmount(type.getRewardAmount())
                .stages(stageDTOs).build();
    }
}