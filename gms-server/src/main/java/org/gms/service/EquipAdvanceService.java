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

    private final EquipAdvanceRouteMapper routeMapper;
    private final EquipAdvanceStageMapper stageMapper;
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

    /** 获取所有装备进阶路线列表 */
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

    /** 根据ID获取装备进阶路线 */
    public EquipAdvanceSaveDTO getRouteById(Long id) {
        EquipAdvanceRouteDO route = routeMapper.selectOneById(id);
        if (route == null) return null;
        return toDTO(route, stageMapper.selectAll(), costMapper.selectAll());
    }

    /** 保存装备进阶路线（新增或更新） */
    @Transactional
    public EquipAdvanceSaveDTO saveRoute(EquipAdvanceSaveDTO dto) {
        EquipAdvanceRouteDO route = EquipAdvanceRouteDO.builder()
                .id(dto.getId())
                .jobGroup(dto.getJobGroup())
                .routeName(dto.getRouteName())
                .enabled(dto.getEnabled() != null ? dto.getEnabled() : 1)
                .build();
        if (route.getId() != null) {
            routeMapper.update(route);
            deleteStagesByRouteId(route.getId());
        } else {
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

    /** 删除装备进阶路线（级联删除阶段和消耗材料） */
    @Transactional
    public void deleteRoute(Long id) {
        deleteStagesByRouteId(id);
        routeMapper.deleteById(id);
        refreshCache();
    }

    /** 根据路线ID删除所有关联的阶段和消耗材料 */
    private void deleteStagesByRouteId(Long routeId) {
        List<EquipAdvanceStageDO> existingStages = stageMapper.selectListByQuery(
                QueryWrapper.create().where("route_id = ?", routeId));
        for (EquipAdvanceStageDO st : existingStages) {
            costMapper.deleteByQuery(
                    QueryWrapper.create().where("stage_id = ?", st.getId()));
        }
        stageMapper.deleteByQuery(QueryWrapper.create().where("route_id = ?", routeId));
    }

    /** 刷新缓存 */
    private void refreshCache() {
        EquipAdvanceManager.load(routeMapper.selectAll(), stageMapper.selectAll(), costMapper.selectAll());
    }

    /** DO转DTO */
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
        stageDTOs.sort(java.util.Comparator.comparingInt(StageDTO::getStageOrder));
        return EquipAdvanceSaveDTO.builder()
                .id(route.getId()).jobGroup(route.getJobGroup()).routeName(route.getRouteName())
                .enabled(route.getEnabled()).stages(stageDTOs).build();
    }
}
