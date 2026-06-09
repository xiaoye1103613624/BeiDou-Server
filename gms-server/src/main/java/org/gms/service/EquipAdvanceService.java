package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.config.EquipAdvanceManager;
import org.gms.dao.entity.EquipAdvanceCostDO;
import org.gms.dao.entity.EquipAdvanceRouteDO;
import org.gms.dao.entity.EquipAdvanceStageDO;
import org.gms.dao.mapper.EquipAdvanceCostMapper;
import org.gms.dao.mapper.EquipAdvanceRouteMapper;
import org.gms.dao.mapper.EquipAdvanceStageMapper;
import org.gms.model.dto.EquipAdvanceSaveDTO;
import org.gms.model.dto.EquipAdvanceSaveDTO.CostDTO;
import org.gms.model.dto.EquipAdvanceSaveDTO.StageDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 装备进阶服务类，负责装备进阶系统的配置管理。
 * <p>
 * 提供装备进阶路线的增删改查操作，包括路线配置、阶段属性、消耗材料等，
 * 并在配置变更时同步更新 {@link EquipAdvanceManager} 的内存缓存。
 * </p>
 */
@Slf4j
@Service
@AllArgsConstructor
public class EquipAdvanceService {

    /**
     * 装备进阶路线数据访问对象
     */
    private final EquipAdvanceRouteMapper routeMapper;
    /**
     * 装备进阶阶段数据访问对象
     */
    private final EquipAdvanceStageMapper stageMapper;
    /**
     * 装备进阶消耗材料数据访问对象
     */
    private final EquipAdvanceCostMapper costMapper;

    /**
     * 初始化方法，在 Spring 容器启动时执行。
     * 从数据库加载所有装备进阶配置并初始化到 EquipAdvanceManager。
     */
    @PostConstruct
    public void init() {
        refreshCache();
        log.info("装备进阶配置加载完成");
    }

    /**
     * 获取所有装备进阶路线列表
     * 加载全部路线、阶段和消耗材料数据，组装为DTO列表
     *
     * @return 装备进阶路线DTO列表
     */
    public List<EquipAdvanceSaveDTO> getRouteList() {
        List<EquipAdvanceRouteDO> routes = routeMapper.selectAll();
        List<EquipAdvanceStageDO> allStages = stageMapper.selectAll();
        List<EquipAdvanceCostDO> allCosts = costMapper.selectAll();

        List<EquipAdvanceSaveDTO> result = new ArrayList<>();
        for (EquipAdvanceRouteDO route : routes) {
            result.add(toDTO(route, allStages, allCosts));
        }
        return result;
    }

    /**
     * 根据ID获取装备进阶路线
     *
     * @param id 路线ID
     * @return 装备进阶路线DTO，不存在返回null
     */
    public EquipAdvanceSaveDTO getRouteById(Long id) {
        EquipAdvanceRouteDO route = routeMapper.selectOneById(id);
        if (route == null) return null;
        return toDTO(route, stageMapper.selectAll(), costMapper.selectAll());
    }

    /**
     * 保存装备进阶路线（新增或更新）
     * 存在ID时更新并删除旧阶段重新创建，不存在ID时插入新路线
     *
     * @param dto 装备进阶保存DTO
     * @return 保存后的装备进阶路线DTO
     */
    @Transactional
    public EquipAdvanceSaveDTO saveRoute(EquipAdvanceSaveDTO dto) {
        EquipAdvanceRouteDO route = EquipAdvanceRouteDO.builder()
                .id(dto.getId())
                .jobGroup(dto.getJobGroup())
                .routeName(dto.getRouteName())
                .enabled(dto.getEnabled() != null ? dto.getEnabled() : 1)
                .build();
        if (route.getId() != null) {
            // 更新路线并清除旧阶段数据
            routeMapper.update(route);
            deleteStagesByRouteId(route.getId());
        } else {
            // 新增路线
            routeMapper.insert(route);
        }

        if (dto.getStages() != null) {
            for (StageDTO st : dto.getStages()) {
                EquipAdvanceStageDO stage = EquipAdvanceStageDO.builder()
                        .routeId(route.getId())
                        .stageOrder(st.getStageOrder())
                        .targetItemId(st.getTargetItemId())
                        .targetItemName(st.getTargetItemName())
                        .mesoCost(st.getMesoCost() != null ? st.getMesoCost() : 0)
                        .cashCost(st.getCashCost() != null ? st.getCashCost() : 0)
                        .creditCost(st.getCreditCost() != null ? st.getCreditCost() : 0)
                        .strAdd(st.getStrAdd() != null ? st.getStrAdd() : 0)
                        .dexAdd(st.getDexAdd() != null ? st.getDexAdd() : 0)
                        .intAdd(st.getIntAdd() != null ? st.getIntAdd() : 0)
                        .lukAdd(st.getLukAdd() != null ? st.getLukAdd() : 0)
                        .hpAdd(st.getHpAdd() != null ? st.getHpAdd() : 0)
                        .mpAdd(st.getMpAdd() != null ? st.getMpAdd() : 0)
                        .watkAdd(st.getWatkAdd() != null ? st.getWatkAdd() : 0)
                        .matkAdd(st.getMatkAdd() != null ? st.getMatkAdd() : 0)
                        .wdefAdd(st.getWdefAdd() != null ? st.getWdefAdd() : 0)
                        .mdefAdd(st.getMdefAdd() != null ? st.getMdefAdd() : 0)
                        .accAdd(st.getAccAdd() != null ? st.getAccAdd() : 0)
                        .avoidAdd(st.getAvoidAdd() != null ? st.getAvoidAdd() : 0)
                        .speedAdd(st.getSpeedAdd() != null ? st.getSpeedAdd() : 0)
                        .jumpAdd(st.getJumpAdd() != null ? st.getJumpAdd() : 0)
                        .build();
                stageMapper.insert(stage);

                if (st.getCosts() != null) {
                    for (CostDTO co : st.getCosts()) {
                        EquipAdvanceCostDO cost = EquipAdvanceCostDO.builder()
                                .stageId(stage.getId())
                                .itemId(co.getItemId())
                                .count(co.getCount() != null ? co.getCount() : 1)
                                .build();
                        costMapper.insert(cost);
                    }
                }
            }
        }

        refreshCache();
        return getRouteById(route.getId());
    }

    /**
     * 删除装备进阶路线（级联删除阶段和消耗材料）
     *
     * @param id 路线ID
     */
    @Transactional
    public void deleteRoute(Long id) {
        deleteStagesByRouteId(id);
        routeMapper.deleteById(id);
        refreshCache();
    }

    /**
     * 根据路线ID删除所有关联的阶段和消耗材料
     * 先删除每个阶段的消耗材料，再删除阶段记录
     *
     * @param routeId 路线ID
     */
    private void deleteStagesByRouteId(Long routeId) {
        List<EquipAdvanceStageDO> existingStages = stageMapper.selectListByQuery(
                QueryWrapper.create().where("route_id = ?", routeId));
        for (EquipAdvanceStageDO st : existingStages) {
            costMapper.deleteByQuery(
                    QueryWrapper.create().where("stage_id = ?", st.getId()));
        }
        stageMapper.deleteByQuery(QueryWrapper.create().where("route_id = ?", routeId));
    }

    /**
     * 刷新内存缓存
     * 从数据库重新加载所有配置到 EquipAdvanceManager
     */
    private void refreshCache() {
        EquipAdvanceManager.load(routeMapper.selectAll(), stageMapper.selectAll(), costMapper.selectAll());
    }

    /**
     * DO转DTO
     * 将数据库实体转换为前端使用的DTO对象
     *
     * @param route     路线实体
     * @param allStages 所有阶段列表
     * @param allCosts  所有消耗材料列表
     * @return 装备进阶路线DTO
     */
    private EquipAdvanceSaveDTO toDTO(EquipAdvanceRouteDO route,
                                      List<EquipAdvanceStageDO> allStages,
                                      List<EquipAdvanceCostDO> allCosts) {
        List<StageDTO> stageDTOs = new ArrayList<>();
        for (EquipAdvanceStageDO st : allStages) {
            if (!st.getRouteId().equals(route.getId())) continue;
            List<CostDTO> costDTOs = new ArrayList<>();
            for (EquipAdvanceCostDO co : allCosts) {
                if (co.getStageId().equals(st.getId())) {
                    costDTOs.add(CostDTO.builder().id(co.getId()).itemId(co.getItemId()).count(co.getCount()).build());
                }
            }
            stageDTOs.add(StageDTO.builder()
                    .id(st.getId()).stageOrder(st.getStageOrder())
                    .targetItemId(st.getTargetItemId()).targetItemName(st.getTargetItemName())
                    .mesoCost(st.getMesoCost()).cashCost(st.getCashCost()).creditCost(st.getCreditCost())
                    .strAdd(st.getStrAdd()).dexAdd(st.getDexAdd()).intAdd(st.getIntAdd()).lukAdd(st.getLukAdd())
                    .hpAdd(st.getHpAdd()).mpAdd(st.getMpAdd())
                    .watkAdd(st.getWatkAdd()).matkAdd(st.getMatkAdd())
                    .wdefAdd(st.getWdefAdd()).mdefAdd(st.getMdefAdd())
                    .accAdd(st.getAccAdd()).avoidAdd(st.getAvoidAdd())
                    .speedAdd(st.getSpeedAdd()).jumpAdd(st.getJumpAdd())
                    .costs(costDTOs).build());
        }
        // 按阶段顺序排序
        stageDTOs.sort(java.util.Comparator.comparingInt(StageDTO::getStageOrder));
        return EquipAdvanceSaveDTO.builder()
                .id(route.getId()).jobGroup(route.getJobGroup()).routeName(route.getRouteName())
                .enabled(route.getEnabled()).stages(stageDTOs).build();
    }
}