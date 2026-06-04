package org.gms.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.config.CardCollectionManager;
import org.gms.dao.entity.CardCollectionConfigDO;
import org.gms.dao.mapper.CardCollectionConfigMapper;
import org.gms.model.dto.BasePageDTO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 【业务服务】CardCollectionService：卡片收集服务类，负责卡片收集系统的配置管理。
 * 
 * <p>提供卡片收集配置的增删改查操作，并在配置变更时同步更新 {@link CardCollectionManager} 的内存缓存，
 * 确保游戏运行时能实时获取最新的卡片收集配置。</p>
 */
@Slf4j
@Service
@AllArgsConstructor
public class CardCollectionService {

    /** 卡片配置数据访问接口 */
    private final CardCollectionConfigMapper mapper;

    /**
     * 初始化方法，在 Spring 容器启动时执行。
     * 从数据库加载所有卡片收集配置并初始化到 {@link CardCollectionManager}。
     */
    @PostConstruct
    public void init() {
        List<CardCollectionConfigDO> configs = mapper.selectAll();
        CardCollectionManager.load(configs);
        log.info("卡片收集配置加载完成，共 {} 条记录", configs.size());
    }

    /**
     * 分页获取卡片收集配置列表。
     * 
     * <p>默认按地区名称（region_name）和排序字段（sort_order）升序排列。</p>
     * 
     * @param condition 分页条件（支持空值防护，默认页码1，每页200条）
     * @return 分页后的卡片收集配置列表
     */
    public Page<CardCollectionConfigDO> getConfigList(BasePageDTO condition) {
        QueryWrapper queryWrapper = QueryWrapper.create(new CardCollectionConfigDO());
        queryWrapper.orderBy("region_name", true).orderBy("sort_order", true);
        int pageNo = condition.getPageNo() != null ? condition.getPageNo() : 1;
        int pageSize = condition.getPageSize() != null ? condition.getPageSize() : 200;
        if (condition.isNotPage()) {
            pageSize = 500;
        }
        return mapper.paginate(pageNo, pageSize, queryWrapper);
    }

    /**
     * 添加卡片收集配置。
     * 
     * <p>插入数据库后自动刷新缓存。</p>
     * 
     * @param config 卡片收集配置实体
     * @return 插入后的配置实体（含自增ID）
     */
    public CardCollectionConfigDO addConfig(CardCollectionConfigDO config) {
        mapper.insert(config);
        refreshCache();
        return config;
    }

    /**
     * 更新卡片收集配置。
     * 
     * <p>更新数据库后自动刷新缓存。</p>
     * 
     * @param config 卡片收集配置实体（须包含ID）
     */
    public void updateConfig(CardCollectionConfigDO config) {
        mapper.update(config);
        refreshCache();
    }

    /**
     * 删除单个卡片收集配置。
     * 
     * <p>删除数据库记录后自动刷新缓存。</p>
     * 
     * @param id 配置ID
     */
    public void deleteConfig(Long id) {
        mapper.deleteById(id);
        refreshCache();
    }

    /**
     * 批量删除卡片收集配置。
     * 
     * <p>批量删除数据库记录后自动刷新缓存。</p>
     * 
     * @param ids 配置ID列表
     */
    public void deleteConfigList(List<Long> ids) {
        mapper.deleteBatchByIds(ids);
        refreshCache();
    }

    /**
     * 刷新缓存，将数据库最新配置加载到 {@link CardCollectionManager} 内存中。
     */
    private void refreshCache() {
        CardCollectionManager.load(mapper.selectAll());
    }
}