package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.config.ScrollDecomposeManager;
import org.gms.dao.entity.ScrollDecomposeConfigDO;
import org.gms.dao.entity.ScrollExchangeConfigDO;
import org.gms.dao.mapper.ScrollDecomposeConfigMapper;
import org.gms.dao.mapper.ScrollExchangeConfigMapper;
import org.gms.model.dto.ScrollDecomposeConfigDTO;
import org.gms.model.dto.ScrollExchangeConfigDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 卷轴分解/兑换服务类，负责分解和兑换配置的增删改查。
 * <p>
 * 提供卷轴分解白名单配置和兑换价格配置的管理，
 * 并在配置变更时同步更新 {@link ScrollDecomposeManager} 的内存缓存。
 * </p>
 */
@Slf4j
@Service
@AllArgsConstructor
public class ScrollDecomposeService {

    private final ScrollDecomposeConfigMapper decomposeConfigMapper;
    private final ScrollExchangeConfigMapper exchangeConfigMapper;

    /**
     * 初始化方法，在 Spring 容器启动时执行。
     * 从数据库加载所有配置并初始化到 ScrollDecomposeManager。
     */
    @PostConstruct
    public void init() {
        refreshConfigCache();
        log.info("卷轴分解配置加载完成");
    }

    // ==================== 缓存刷新 ====================

    /**
     * 刷新配置缓存，从数据库重新加载所有配置到 ScrollDecomposeManager。
     */
    private void refreshConfigCache() {
        ScrollDecomposeManager.load(decomposeConfigMapper.selectAll(), exchangeConfigMapper.selectAll());
    }

    // ==================== 分解配置 CRUD ====================

    /**
     * 获取卷轴分解配置列表（支持筛选），按 sortOrder 和 scrollId 排序。
     *
     * @param scrollId 卷轴ID筛选（可选）
     * @param enabled  启用状态筛选（可选）
     * @return 分解配置 DTO 列表
     */
    public List<ScrollDecomposeConfigDTO> getDecomposeConfigList(Integer scrollId, Integer enabled) {
        QueryWrapper qw = QueryWrapper.create();
        boolean hasCondition = false;
        if (scrollId != null) {
            qw.where("scroll_id = ?", scrollId);
            hasCondition = true;
        }
        if (enabled != null) {
            if (hasCondition) {
                qw.and("enabled = ?", enabled);
            } else {
                qw.where("enabled = ?", enabled);
                hasCondition = true;
            }
        }
        List<ScrollDecomposeConfigDO> configs;
        if (hasCondition) {
            configs = decomposeConfigMapper.selectListByQuery(qw);
        } else {
            configs = decomposeConfigMapper.selectAll();
        }
        return configs.stream()
                .sorted(Comparator.comparingInt((ScrollDecomposeConfigDO c) ->
                        c.getSortOrder() != null ? c.getSortOrder() : 200)
                        .thenComparingInt(ScrollDecomposeConfigDO::getScrollId))
                .map(this::toDecomposeConfigDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取单个分解配置。
     *
     * @param id 配置ID
     * @return 配置 DTO，不存在返回 null
     */
    public ScrollDecomposeConfigDTO getDecomposeConfigById(Long id) {
        ScrollDecomposeConfigDO config = decomposeConfigMapper.selectOneById(id);
        return config != null ? toDecomposeConfigDTO(config) : null;
    }

    /**
     * 保存卷轴分解配置（新增或更新）。
     *
     * @param dto 配置 DTO
     * @return 保存后的配置 DTO
     */
    @Transactional
    public ScrollDecomposeConfigDTO saveDecomposeConfig(ScrollDecomposeConfigDTO dto) {
        ScrollDecomposeConfigDO config = ScrollDecomposeConfigDO.builder()
                .id(dto.getId())
                .scrollId(dto.getScrollId())
                .scrollName(dto.getScrollName())
                .enabled(dto.getEnabled() != null ? dto.getEnabled() : 1)
                .sortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 200)
                .build();
        if (config.getId() != null) {
            decomposeConfigMapper.update(config);
        } else {
            decomposeConfigMapper.insert(config);
        }
        refreshConfigCache();
        return getDecomposeConfigById(config.getId());
    }

    /**
     * 删除卷轴分解配置。
     *
     * @param id 配置ID
     */
    @Transactional
    public void deleteDecomposeConfig(Long id) {
        decomposeConfigMapper.deleteById(id);
        refreshConfigCache();
    }

    /**
     * 批量删除卷轴分解配置。
     *
     * @param ids 配置ID列表
     */
    @Transactional
    public void deleteDecomposeConfigBatch(List<Long> ids) {
        decomposeConfigMapper.deleteBatchByIds(ids);
        refreshConfigCache();
    }

    // ==================== 兑换配置 CRUD ====================

    /**
     * 获取卷轴兑换配置列表（支持筛选），按 cost 和 sortOrder 排序。
     *
     * @param scrollId 卷轴ID筛选（可选）
     * @param enabled  启用状态筛选（可选）
     * @return 兑换配置 DTO 列表
     */
    public List<ScrollExchangeConfigDTO> getExchangeConfigList(Integer scrollId, Integer enabled) {
        QueryWrapper qw = QueryWrapper.create();
        boolean hasCondition = false;
        if (scrollId != null) {
            qw.where("scroll_id = ?", scrollId);
            hasCondition = true;
        }
        if (enabled != null) {
            if (hasCondition) {
                qw.and("enabled = ?", enabled);
            } else {
                qw.where("enabled = ?", enabled);
                hasCondition = true;
            }
        }
        List<ScrollExchangeConfigDO> configs;
        if (hasCondition) {
            configs = exchangeConfigMapper.selectListByQuery(qw);
        } else {
            configs = exchangeConfigMapper.selectAll();
        }
        return configs.stream()
                .sorted(Comparator.comparingInt((ScrollExchangeConfigDO c) ->
                        c.getCost() != null ? c.getCost() : 0)
                        .thenComparingInt(c -> c.getSortOrder() != null ? c.getSortOrder() : 200)
                        .thenComparingInt(ScrollExchangeConfigDO::getScrollId))
                .map(this::toExchangeConfigDTO)
                .collect(Collectors.toList());
    }

    /**
     * 根据ID获取单个兑换配置。
     *
     * @param id 配置ID
     * @return 配置 DTO，不存在返回 null
     */
    public ScrollExchangeConfigDTO getExchangeConfigById(Long id) {
        ScrollExchangeConfigDO config = exchangeConfigMapper.selectOneById(id);
        return config != null ? toExchangeConfigDTO(config) : null;
    }

    /**
     * 保存卷轴兑换配置（新增或更新）。
     *
     * @param dto 配置 DTO
     * @return 保存后的配置 DTO
     */
    @Transactional
    public ScrollExchangeConfigDTO saveExchangeConfig(ScrollExchangeConfigDTO dto) {
        ScrollExchangeConfigDO config = ScrollExchangeConfigDO.builder()
                .id(dto.getId())
                .scrollId(dto.getScrollId())
                .scrollName(dto.getScrollName())
                .cost(dto.getCost() != null ? dto.getCost() : 100)
                .enabled(dto.getEnabled() != null ? dto.getEnabled() : 1)
                .sortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 200)
                .build();
        if (config.getId() != null) {
            exchangeConfigMapper.update(config);
        } else {
            exchangeConfigMapper.insert(config);
        }
        refreshConfigCache();
        return getExchangeConfigById(config.getId());
    }

    /**
     * 删除卷轴兑换配置。
     *
     * @param id 配置ID
     */
    @Transactional
    public void deleteExchangeConfig(Long id) {
        exchangeConfigMapper.deleteById(id);
        refreshConfigCache();
    }

    /**
     * 批量删除卷轴兑换配置。
     *
     * @param ids 配置ID列表
     */
    @Transactional
    public void deleteExchangeConfigBatch(List<Long> ids) {
        exchangeConfigMapper.deleteBatchByIds(ids);
        refreshConfigCache();
    }

    // ==================== 数据转换 ====================

    /**
     * 将 DO 转换为分解配置 DTO。
     */
    private ScrollDecomposeConfigDTO toDecomposeConfigDTO(ScrollDecomposeConfigDO config) {
        return ScrollDecomposeConfigDTO.builder()
                .id(config.getId())
                .scrollId(config.getScrollId())
                .scrollName(config.getScrollName())
                .enabled(config.getEnabled())
                .sortOrder(config.getSortOrder() != null ? config.getSortOrder() : 200)
                .build();
    }

    /**
     * 将 DO 转换为兑换配置 DTO。
     */
    private ScrollExchangeConfigDTO toExchangeConfigDTO(ScrollExchangeConfigDO config) {
        return ScrollExchangeConfigDTO.builder()
                .id(config.getId())
                .scrollId(config.getScrollId())
                .scrollName(config.getScrollName())
                .cost(config.getCost())
                .enabled(config.getEnabled())
                .sortOrder(config.getSortOrder() != null ? config.getSortOrder() : 200)
                .build();
    }
}
