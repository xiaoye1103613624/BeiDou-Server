package org.gms.config;

import com.mybatisflex.core.query.QueryWrapper;
import org.gms.dao.entity.ToyCollectionCategoryDO;
import org.gms.dao.entity.ToyCollectionItemDO;
import org.gms.dao.entity.ToyCollectionProgressDO;
import org.gms.dao.mapper.ToyCollectionCategoryMapper;
import org.gms.dao.mapper.ToyCollectionItemMapper;
import org.gms.dao.mapper.ToyCollectionProgressMapper;
import org.gms.manager.ServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 玩具收集配置管理器（静态缓存 + 直查DB）
 * 注意：GraalJS ClassLoader隔离，JS脚本通过 query* 方法直查DB
 */
public class ToyCollectionConfigManager {

    private static final Logger log = LoggerFactory.getLogger(ToyCollectionConfigManager.class);

    /** 分类缓存（key=categoryId） */
    private static final Map<Long, ToyCollectionCategoryDO> categoryMap = new ConcurrentHashMap<>();

    /** 物品缓存（key=categoryId, value=该分类下的物品列表） */
    private static final Map<Long, List<ToyCollectionItemDO>> itemMap = new ConcurrentHashMap<>();

    /** 已启用的分类列表（按sortOrder排序） */
    private static final List<ToyCollectionCategoryDO> enabledCategories = new ArrayList<>();

    private ToyCollectionConfigManager() {
    }

    /**
     * 从 Service 层加载缓存（启动时 + 配置变更后调用）
     */
    public static synchronized void load(List<ToyCollectionCategoryDO> categories,
                                         List<ToyCollectionItemDO> items) {
        categoryMap.clear();
        itemMap.clear();
        enabledCategories.clear();

        // 按categoryId分组物品
        Map<Long, List<ToyCollectionItemDO>> grouped = new HashMap<>();
        for (ToyCollectionItemDO item : items) {
            grouped.computeIfAbsent(item.getCategoryId(), k -> new ArrayList<>()).add(item);
        }

        // 填入缓存
        for (ToyCollectionCategoryDO cat : categories) {
            categoryMap.put(cat.getId(), cat);
            List<ToyCollectionItemDO> catItems = grouped.getOrDefault(cat.getId(), new ArrayList<>());
            catItems.sort(Comparator.comparingInt(ToyCollectionItemDO::getSortOrder));
            itemMap.put(cat.getId(), catItems);

            if (cat.getEnabled() != null && cat.getEnabled() == 1) {
                enabledCategories.add(cat);
            }
        }
        enabledCategories.sort(Comparator.comparingInt(ToyCollectionCategoryDO::getSortOrder));

        log.info("[玩具收集] 缓存刷新完成：{}个分类，{}个物品，{}个启用的分类",
                categoryMap.size(), items.size(), enabledCategories.size());
    }

    // ==================== JS脚本可调用的直查DB方法 ====================

    /**
     * 查询所有启用的分类（按排序升序）
     */
    public static List<ToyCollectionCategoryDO> queryEnabledCategories() {
        try {
            ToyCollectionCategoryMapper mapper = ServerManager.getApplicationContext()
                    .getBean(ToyCollectionCategoryMapper.class);
            return mapper.selectListByQuery(
                    QueryWrapper.create().where("enabled = ?", 1).orderBy("sort_order", true));
        } catch (Exception e) {
            log.error("[玩具收集] 查询启用分类失败", e);
            return Collections.emptyList();
        }
    }

    /**
     * 查询指定分类下的启用物品列表（按排序升序）
     */
    public static List<ToyCollectionItemDO> queryItemsByCategory(Long categoryId) {
        try {
            ToyCollectionItemMapper mapper = ServerManager.getApplicationContext()
                    .getBean(ToyCollectionItemMapper.class);
            return mapper.selectListByQuery(
                    QueryWrapper.create()
                            .where("category_id = ?", categoryId)
                            .and("enabled = ?", 1)
                            .orderBy("sort_order", true));
        } catch (Exception e) {
            log.error("[玩具收集] 查询分类物品失败，categoryId={}", categoryId, e);
            return Collections.emptyList();
        }
    }

    /**
     * 根据ID查询单个物品配置
     */
    public static ToyCollectionItemDO queryItemById(Long itemConfigId) {
        try {
            ToyCollectionItemMapper mapper = ServerManager.getApplicationContext()
                    .getBean(ToyCollectionItemMapper.class);
            return mapper.selectOneById(itemConfigId);
        } catch (Exception e) {
            log.error("[玩具收集] 查询物品配置失败，id={}", itemConfigId, e);
            return null;
        }
    }

    /**
     * 查询指定角色的收集进度
     */
    public static List<ToyCollectionProgressDO> queryProgressByCharacter(Integer characterId) {
        try {
            ToyCollectionProgressMapper mapper = ServerManager.getApplicationContext()
                    .getBean(ToyCollectionProgressMapper.class);
            return mapper.selectListByQuery(
                    QueryWrapper.create().where("character_id = ?", characterId));
        } catch (Exception e) {
            log.error("[玩具收集] 查询进度失败，characterId={}", characterId, e);
            return Collections.emptyList();
        }
    }
}
