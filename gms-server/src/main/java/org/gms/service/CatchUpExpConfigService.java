package org.gms.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.config.CatchUpExpConfigManager;
import org.gms.dao.entity.CatchUpExpConfigDO;
import org.gms.dao.mapper.CatchUpExpConfigMapper;
import org.gms.model.dto.CatchUpExpConfigReqDTO;
import org.gms.util.I18nUtil;
import org.gms.util.RequireUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static org.gms.dao.entity.table.CatchUpExpConfigDOTableDef.CATCH_UP_EXP_CONFIG_D_O;

/**
 * 【业务服务】CatchUpExpConfigService：追赶经验配置服务类，负责追赶经验配置的增删改查及缓存管理。
 * 
 * <p>追赶经验系统允许低等级玩家获得额外经验加成以快速追赶等级。配置按等级区间划分，
 * 每个区间定义一个经验倍率。配置变更时同步更新 {@link CatchUpExpConfigManager} 的内存缓存。</p>
 * 
 * <p>关键约束：等级区间不允许重叠，确保每个等级只有一个生效的经验倍率。</p>
 */
@Service
@AllArgsConstructor
@Slf4j
public class CatchUpExpConfigService {

    /** 追赶经验配置数据访问接口 */
    private final CatchUpExpConfigMapper catchUpExpConfigMapper;

    /**
     * 初始化方法，在 Spring 容器启动时执行。
     * 从数据库加载所有启用的追赶经验配置并初始化到 {@link CatchUpExpConfigManager}。
     */
    @PostConstruct
    public void init() {
        List<CatchUpExpConfigDO> configs = catchUpExpConfigMapper.selectListByQuery(
                QueryWrapper.create().where(CATCH_UP_EXP_CONFIG_D_O.ENABLED.eq(1))
        );
        CatchUpExpConfigManager.load(configs);
    }

    /**
     * 获取追赶经验配置列表（分页）。
     * 
     * <p>支持按等级区间筛选，默认按最低等级升序排列。</p>
     * 
     * @param condition 查询条件（支持 levelMin、levelMax 筛选）
     * @return 分页结果
     */
    public Page<CatchUpExpConfigDO> getConfigList(CatchUpExpConfigReqDTO condition) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(CATCH_UP_EXP_CONFIG_D_O.ALL_COLUMNS)
                .from(CATCH_UP_EXP_CONFIG_D_O);
        if (condition.getLevelMin() != null) {
            queryWrapper.and(CATCH_UP_EXP_CONFIG_D_O.LEVEL_MIN.eq(condition.getLevelMin()));
        }
        if (condition.getLevelMax() != null) {
            queryWrapper.and(CATCH_UP_EXP_CONFIG_D_O.LEVEL_MAX.eq(condition.getLevelMax()));
        }
        queryWrapper.orderBy(CATCH_UP_EXP_CONFIG_D_O.LEVEL_MIN, true);
        return catchUpExpConfigMapper.paginate(condition.getPageNo(), condition.getPageSize(), queryWrapper);
    }

    /**
     * 添加追赶经验配置。
     * 
     * <p>校验规则：
     * <ul>
     *   <li>levelMin、levelMax、expMultiplier 不能为空</li>
     *   <li>等级区间不能与已有配置重叠</li>
     * </ul></p>
     * 
     * @param data 配置数据
     * @throws BizException 当参数为空或等级区间重叠时抛出异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void addConfig(CatchUpExpConfigDO data) {
        // 参数校验
        RequireUtil.requireNotNull(data.getLevelMin(), 
                I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "levelMin"));
        RequireUtil.requireNotNull(data.getLevelMax(), 
                I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "levelMax"));
        RequireUtil.requireNotNull(data.getExpMultiplier(), 
                I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "expMultiplier"));
        
        // 检查等级区间不重叠
        List<CatchUpExpConfigDO> existList = catchUpExpConfigMapper.selectListByQuery(
                QueryWrapper.create().where(CATCH_UP_EXP_CONFIG_D_O.LEVEL_MIN.le(data.getLevelMax()))
                        .and(CATCH_UP_EXP_CONFIG_D_O.LEVEL_MAX.ge(data.getLevelMin()))
        );
        RequireUtil.requireTrue(existList.isEmpty(), "等级区间与已有配置重叠");

        // 设置创建时间和更新时间
        data.setId(null);
        data.setCreateTime(new Date());
        data.setUpdateTime(new Date());
        
        // 插入数据库并更新缓存
        catchUpExpConfigMapper.insertSelective(data);
        CatchUpExpConfigManager.addConfig(data);
    }

    /**
     * 更新追赶经验配置。
     * 
     * <p>校验规则：
     * <ul>
     *   <li>id 不能为空且配置必须存在</li>
     *   <li>新等级区间不能与其他配置重叠（排除自身）</li>
     * </ul></p>
     * 
     * @param data 配置数据（须包含ID）
     * @throws BizException 当参数无效或等级区间重叠时抛出异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateConfig(CatchUpExpConfigDO data) {
        // 参数校验：ID必须存在
        RequireUtil.requireNotNull(data.getId(), 
                I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "id"));
        
        // 查询原有配置
        CatchUpExpConfigDO old = catchUpExpConfigMapper.selectOneById(data.getId());
        RequireUtil.requireNotNull(old, "配置不存在");

        // 检查等级区间不重叠（排除自身）
        if (data.getLevelMin() != null && data.getLevelMax() != null) {
            List<CatchUpExpConfigDO> existList = catchUpExpConfigMapper.selectListByQuery(
                    QueryWrapper.create().where(CATCH_UP_EXP_CONFIG_D_O.LEVEL_MIN.le(data.getLevelMax()))
                            .and(CATCH_UP_EXP_CONFIG_D_O.LEVEL_MAX.ge(data.getLevelMin()))
                            .and(CATCH_UP_EXP_CONFIG_D_O.ID.ne(data.getId()))
            );
            RequireUtil.requireTrue(existList.isEmpty(), "等级区间与已有配置重叠");
        }

        // 更新时间戳
        data.setUpdateTime(new Date());
        
        // 更新数据库
        catchUpExpConfigMapper.update(data);

        // 查询更新后的完整配置并更新缓存
        CatchUpExpConfigDO updated = catchUpExpConfigMapper.selectOneById(data.getId());
        CatchUpExpConfigManager.updateConfig(old, updated);
    }

    /**
     * 删除追赶经验配置。
     * 
     * @param id 配置ID
     * @throws BizException 当ID为空时抛出异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteConfig(Long id) {
        RequireUtil.requireNotNull(id, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "id"));
        
        // 删除数据库记录并更新缓存
        catchUpExpConfigMapper.deleteById(id);
        CatchUpExpConfigManager.removeConfig(id);
    }

    /**
     * 批量删除追赶经验配置。
     * 
     * @param ids 配置ID列表
     * @throws BizException 当ID列表为空时抛出异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteConfigList(List<Long> ids) {
        RequireUtil.requireNotEmpty(ids, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_EMPTY", "ids"));
        for (Long id : ids) {
            deleteConfig(id);
        }
    }
}