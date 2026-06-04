package org.gms.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.config.BossConfigManager;
import org.gms.dao.entity.BossConfigDO;
import org.gms.dao.mapper.BossConfigMapper;
import org.gms.model.dto.BossConfigReqDTO;
import org.gms.model.dto.BossConfigRtnDTO;
import org.gms.server.life.LifeFactory;
import org.gms.server.life.Monster;
import org.gms.server.life.MonsterStats;
import org.gms.util.I18nUtil;
import org.gms.util.RequireUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

import static org.gms.dao.entity.table.BossConfigDOTableDef.BOSS_CONFIG_D_O;

/**
 * 【业务服务】BossConfigService：Boss配置服务类，负责Boss配置的增删改查及缓存管理。
 * 
 * <p>提供Boss属性配置（HP倍率、经验倍率、伤害倍率等）的管理，支持配置的动态更新和缓存同步。
 * 配置修改后会自动同步到 {@link BossConfigManager} 缓存。</p>
 */
@Service
@AllArgsConstructor
@Slf4j
public class BossConfigService {

    /** Boss配置数据访问接口 */
    private final BossConfigMapper bossConfigMapper;

    /**
     * 初始化方法：加载启用的Boss配置到缓存。
     * 
     * <p>服务启动时调用，将所有enabled=1的配置加载到 {@link BossConfigManager}。</p>
     */
    @PostConstruct
    public void init() {
        List<BossConfigDO> configs = bossConfigMapper.selectListByQuery(
                QueryWrapper.create().where(BOSS_CONFIG_D_O.ENABLED.eq(1))
        );
        BossConfigManager.load(configs);
    }

    /**
     * 分页查询Boss配置列表。
     * 
     * <p>支持按怪物ID、Boss名称筛选，返回结果包含WZ文件中的原始属性作为对比。</p>
     * 
     * @param condition 查询条件（包含怪物ID、Boss名称、分页信息）
     * @return 分页后的Boss配置列表
     */
    public Page<BossConfigRtnDTO> getBossConfigList(BossConfigReqDTO condition) {
        QueryWrapper queryWrapper = QueryWrapper.create()
                .select(BOSS_CONFIG_D_O.ALL_COLUMNS)
                .from(BOSS_CONFIG_D_O);
        
        // 按怪物ID筛选
        if (condition.getMobId() != null) {
            queryWrapper.and(BOSS_CONFIG_D_O.MOB_ID.eq(condition.getMobId()));
        }
        // 按Boss名称模糊搜索
        if (!RequireUtil.isEmpty(condition.getBossName())) {
            queryWrapper.and(BOSS_CONFIG_D_O.BOSS_NAME.like(condition.getBossName()));
        }
        
        queryWrapper.orderBy(BOSS_CONFIG_D_O.MOB_ID, true);
        Page<BossConfigDO> paginate = bossConfigMapper.paginate(condition.getPageNo(), condition.getPageSize(), queryWrapper);

        // 转换为包含WZ对比数据的DTO
        List<BossConfigRtnDTO> records = paginate.getRecords().stream()
                .map(this::toRtnDTO)
                .toList();

        return new Page<>(records, paginate.getPageNumber(), paginate.getPageSize(), paginate.getTotalRow());
    }

    /**
     * 将Boss配置DO转换为返回DTO（包含WZ原始数据对比）。
     * 
     * <p>从WZ文件获取怪物的原始属性，与配置中的自定义属性进行对比展示。</p>
     * 
     * @param config Boss配置实体
     * @return 返回DTO（包含配置值和WZ原始值）
     */
    private BossConfigRtnDTO toRtnDTO(BossConfigDO config) {
        Monster mob = LifeFactory.getMonster(config.getMobId());
        BossConfigRtnDTO.BossConfigRtnDTOBuilder builder = BossConfigRtnDTO.builder()
                .id(config.getId())
                .mobId(config.getMobId())
                .bossName(config.getBossName())
                .hpMultiplier(config.getHpMultiplier())
                .expMultiplier(config.getExpMultiplier())
                .damageMultiplier(config.getDamageMultiplier())
                .level(config.getLevel())
                .hp(config.getHp())
                .mp(config.getMp())
                .exp(config.getExp())
                .pdd(config.getPdd())
                .mdd(config.getMdd())
                .acc(config.getAcc())
                .eva(config.getEva())
                .enabled(config.getEnabled())
                .updateTime(config.getUpdateTime());

        // 从WZ文件获取原始属性
        if (mob != null && !"MISSINGNO".equals(mob.getName())) {
            MonsterStats stats = mob.getStats();
            builder.wzLevel(stats.getLevel())
                    .wzHp(stats.getHp())
                    .wzMp(stats.getMp())
                    .wzExp(stats.getExp())
                    .wzPdd(stats.getPDDamage())
                    .wzMdd(stats.getMDDamage())
                    .wzAcc(stats.acc)
                    .wzEva(stats.eva)
                    .wzPadamage(stats.getPADamage())
                    .wzMadamage(stats.getMADamage())
                    .wzBoss(stats.isBoss());
        }

        return builder.build();
    }

    /**
     * 添加Boss配置。
     * 
     * <p>检查怪物ID唯一性后插入数据库，并同步到缓存。</p>
     * 
     * @param data Boss配置实体
     */
    @Transactional(rollbackFor = Exception.class)
    public void addBossConfig(BossConfigDO data) {
        RequireUtil.requireNotNull(data.getMobId(), I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "mobId"));
        
        // 检查怪物ID唯一性
        List<BossConfigDO> existList = bossConfigMapper.selectListByQuery(
                QueryWrapper.create().where(BOSS_CONFIG_D_O.MOB_ID.eq(data.getMobId()))
        );
        RequireUtil.requireTrue(existList.isEmpty(), "该怪物ID已存在配置");

        data.setId(null);
        data.setCreateTime(new Date());
        data.setUpdateTime(new Date());
        bossConfigMapper.insertSelective(data);
        
        // 同步到缓存
        BossConfigManager.addConfig(data);
    }

    /**
     * 更新Boss配置。
     * 
     * <p>更新数据库后重新查询完整记录，同步到缓存。</p>
     * 
     * @param data Boss配置实体
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateBossConfig(BossConfigDO data) {
        RequireUtil.requireNotNull(data.getId(), I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "id"));
        
        // 验证配置存在
        BossConfigDO old = bossConfigMapper.selectOneById(data.getId());
        RequireUtil.requireNotNull(old, "配置不存在");

        data.setUpdateTime(new Date());
        bossConfigMapper.update(data);

        // 重新查询更新后的完整记录刷新缓存
        BossConfigDO updated = bossConfigMapper.selectOneById(data.getId());
        BossConfigManager.updateConfig(updated);
    }

    /**
     * 删除Boss配置。
     * 
     * <p>从缓存和数据库中移除配置。</p>
     * 
     * @param id 配置ID
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteBossConfig(Long id) {
        RequireUtil.requireNotNull(id, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_NULL", "id"));
        
        BossConfigDO old = bossConfigMapper.selectOneById(id);
        if (old != null) {
            // 从缓存移除
            BossConfigManager.removeConfig(old.getMobId());
        }
        // 从数据库删除
        bossConfigMapper.deleteById(id);
    }

    /**
     * 批量删除Boss配置。
     * 
     * @param ids 配置ID列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void deleteBossConfigList(List<Long> ids) {
        RequireUtil.requireNotEmpty(ids, I18nUtil.getExceptionMessage("PARAMETER_SHOULD_NOT_EMPTY", "ids"));
        for (Long id : ids) {
            deleteBossConfig(id);
        }
    }
}