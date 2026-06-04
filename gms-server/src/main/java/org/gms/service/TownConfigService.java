package org.gms.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.config.TownConfigManager;
import org.gms.dao.entity.TownConfigDO;
import org.gms.dao.mapper.TownConfigMapper;
import org.gms.model.dto.TownConfigReqDTO;
import org.gms.util.I18nUtil;
import org.gms.util.RequireUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static org.gms.dao.entity.table.TownConfigDOTableDef.TOWN_CONFIG_D_O;

/**
 * 【业务服务】TownConfigService：城镇配置服务类，负责城镇开放状态配置的管理。
 * 
 * <p>提供城镇配置的增删改查操作，并在配置变更时同步更新 {@link TownConfigManager} 的内存缓存，
 * 确保游戏运行时能实时获取最新的城镇开放状态。</p>
 */
@Service
@AllArgsConstructor
@Slf4j
public class TownConfigService {

    /** 城镇配置数据访问接口 */
    private final TownConfigMapper townConfigMapper;

    /**
     * 服务初始化方法，在 Spring 容器启动时执行。
     * 从数据库加载所有城镇配置并初始化到 {@link TownConfigManager}。
     */
    @PostConstruct
    public void init() {
        List<TownConfigDO> configs = townConfigMapper.selectAll();
        TownConfigManager.load(configs);
    }

    /**
     * 分页查询城镇配置列表。
     * 
     * @param condition 查询条件（支持 mapId 精确匹配、townName 模糊匹配）
     * @return 分页后的城镇配置列表
     */
    public Page<TownConfigDO> getTownConfigList(TownConfigReqDTO condition) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(TOWN_CONFIG_D_O.ALL_COLUMNS)
                .from(TOWN_CONFIG_D_O);
        if (condition.getMapId() != null) {
            queryWrapper.and(TOWN_CONFIG_D_O.MAP_ID.eq(condition.getMapId()));
        }
        if (!RequireUtil.isEmpty(condition.getTownName())) {
            queryWrapper.and(TOWN_CONFIG_D_O.TOWN_NAME.like(condition.getTownName()));
        }
        queryWrapper.orderBy(TOWN_CONFIG_D_O.MAP_ID, true);
        // 空值防护：与精英BOSS、卡片收集等服务保持一致
        int pageNo = condition.getPageNo() != null ? condition.getPageNo() : 1;
        int pageSize = condition.getPageSize() != null ? condition.getPageSize() : 20;
        if (condition.isNotPage()) {
            pageSize = 200;
        }
        return townConfigMapper.paginate(pageNo, pageSize, queryWrapper);
    }

    /**
     * 新增城镇配置。
     * 
     * <p>校验逻辑：确保 mapId 唯一；若 enabled=0（关闭状态），同步更新缓存。</p>
     * 
     * @param data 城镇配置数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void addTownConfig(TownConfigDO data) {
        // 校验 mapId 不能为null且必须大于0
        RequireUtil.requireNotNull(data.getMapId(), I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "mapId"));
        RequireUtil.requireTrue(data.getMapId() > 0, "mapId必须大于0");
        List<TownConfigDO> existList = townConfigMapper.selectListByQuery(
                QueryWrapper.create().where(TOWN_CONFIG_D_O.MAP_ID.eq(data.getMapId()))
        );
        RequireUtil.requireTrue(existList.isEmpty(), "该地图ID已存在配置");
        data.setId(null);
        data.setCreateTime(new Date());
        data.setUpdateTime(new Date());
        townConfigMapper.insertSelective(data);
        // 根据enabled状态同步缓存：0=关闭加入缓存，否则从缓存移除（确保状态一致）
        if (data.getEnabled() != null && data.getEnabled() == 0) {
            TownConfigManager.addClosedTown(data.getMapId());
        } else {
            TownConfigManager.removeClosedTown(data.getMapId());
        }
    }

    /**
     * 更新城镇配置。
     * 
     * <p>校验逻辑：确保配置存在；根据 enabled 状态同步更新缓存（启用时从缓存移除，关闭时加入缓存）。</p>
     * 
     * @param data 城镇配置数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateTownConfig(TownConfigDO data) {
        RequireUtil.requireNotNull(data.getId(), I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "id"));
        TownConfigDO old = townConfigMapper.selectOneById(data.getId());
        RequireUtil.requireNotNull(old, "配置不存在");
        data.setUpdateTime(new Date());
        townConfigMapper.update(data);
        if (data.getEnabled() != null && data.getEnabled() == 0) {
            TownConfigManager.addClosedTown(old.getMapId());
        } else {
            TownConfigManager.removeClosedTown(old.getMapId());
        }
    }

    /**
     * 删除单个城镇配置。
     * 
     * <p>删除前先从缓存中移除对应城镇的关闭状态。</p>
     * 
     * @param id 配置ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteTownConfig(Long id) {
        RequireUtil.requireNotNull(id, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "id"));
        TownConfigDO old = townConfigMapper.selectOneById(id);
        if (old != null) {
            TownConfigManager.removeClosedTown(old.getMapId());
        }
        townConfigMapper.deleteById(id);
    }

    /**
     * 批量删除城镇配置。
     * 
     * @param ids 配置ID列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteTownConfigList(List<Long> ids) {
        RequireUtil.requireNotEmpty(ids, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_EMPTY", "ids"));
        for (Long id : ids) {
            deleteTownConfig(id);
        }
    }
}