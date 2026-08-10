package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.config.AlchemyTierManager;
import org.gms.dao.entity.AlchemyTierDO;
import org.gms.dao.mapper.AlchemyTierMapper;
import org.gms.model.dto.AlchemyTierDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 炼金师品级配置服务：管理后台对品级名称/经验阈值的增删改查，
 * 变更后自动刷新 {@link AlchemyTierManager} 缓存，供脚本（品级判定）使用。
 */
@Slf4j
@Service
@AllArgsConstructor
public class AlchemyTierService {

    private final AlchemyTierMapper tierMapper;

    /** 查询某副职业已启用品级，按显示顺序升序。 */
    public List<AlchemyTierDO> listEnabledTiers(int type) {
        return tierMapper.selectListByQuery(
                QueryWrapper.create()
                        .where("type = ?", type)
                        .and("enabled = 1")
                        .orderBy("sort_order", true));
    }

    /** 查询某副职业所有品级（含已禁用），按显示顺序升序。 */
    public List<AlchemyTierDO> listAllTiers(int type) {
        return tierMapper.selectListByQuery(
                QueryWrapper.create()
                        .where("type = ?", type)
                        .orderBy("sort_order", true));
    }

    /**
     * 保存品级（新增或更新），校验经验阈值连续性后刷新缓存。
     */
    @Transactional
    public AlchemyTierDTO saveTier(AlchemyTierDTO dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("品级名称不能为空");
        }
        if (dto.getExpStart() != null && dto.getExpStart() < 0) {
            throw new IllegalArgumentException("经验阈值不能为负数");
        }
        AlchemyTierDO entity = AlchemyTierDO.builder()
                .id(dto.getId())
                .type(dto.getType() != null ? dto.getType() : AlchemyTierManager.TYPE_ALCHEMY)
                .name(dto.getName())
                .expStart(dto.getExpStart() != null ? dto.getExpStart() : 0L)
                .isMax(dto.getIsMax() != null ? dto.getIsMax() : 0)
                .sortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0)
                .enabled(dto.getEnabled() != null ? dto.getEnabled() : 1)
                .build();
        if (entity.getId() != null && entity.getId() > 0) {
            AlchemyTierDO existing = tierMapper.selectOneById(entity.getId());
            if (existing != null) {
                entity.setCreateTime(existing.getCreateTime());
            }
            entity.setUpdateTime(new Date());
            tierMapper.update(entity);
        } else {
            entity.setId(null);
            Date now = new Date();
            entity.setCreateTime(now);
            entity.setUpdateTime(now);
            tierMapper.insert(entity);
        }
        AlchemyTierManager.reload(dto.getType() != null ? dto.getType() : AlchemyTierManager.TYPE_ALCHEMY);
        AlchemyTierDO saved = tierMapper.selectOneById(entity.getId());
        return saved == null ? null : toDTO(saved);
    }

    /** 切换某副职业品级启用状态，切换后刷新缓存。 */
    @Transactional
    public void toggleEnabled(Long id) {
        AlchemyTierDO d = tierMapper.selectOneById(id);
        if (d == null) {
            return;
        }
        d.setEnabled(d.getEnabled() != null && d.getEnabled() == 1 ? 0 : 1);
        tierMapper.update(d);
        AlchemyTierManager.reload(d.getType() != null ? d.getType() : AlchemyTierManager.TYPE_ALCHEMY);
    }

    /** 删除某副职业品级，删除后刷新缓存。 */
    @Transactional
    public void deleteTier(Long id) {
        AlchemyTierDO d = tierMapper.selectOneById(id);
        if (d == null) {
            tierMapper.deleteById(id);
            return;
        }
        tierMapper.deleteById(id);
        AlchemyTierManager.reload(d.getType() != null ? d.getType() : AlchemyTierManager.TYPE_ALCHEMY);
    }

    private AlchemyTierDTO toDTO(AlchemyTierDO d) {
        return AlchemyTierDTO.builder()
                .id(d.getId())
                .type(d.getType())
                .name(d.getName())
                .expStart(d.getExpStart())
                .isMax(d.getIsMax())
                .sortOrder(d.getSortOrder())
                .enabled(d.getEnabled())
                .build();
    }
}