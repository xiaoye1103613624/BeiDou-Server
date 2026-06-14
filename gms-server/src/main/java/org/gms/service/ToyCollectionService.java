package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.client.Character;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.gms.net.server.world.World;
import org.gms.config.ToyCollectionConfigManager;
import org.gms.constants.inventory.ItemConstants;
import org.gms.dao.entity.ToyCollectionCategoryDO;
import org.gms.dao.entity.ToyCollectionItemDO;
import org.gms.dao.entity.ToyCollectionProgressDO;
import org.gms.dao.mapper.ToyCollectionCategoryMapper;
import org.gms.dao.mapper.ToyCollectionItemMapper;
import org.gms.dao.mapper.ToyCollectionProgressMapper;
import org.gms.model.dto.ToyCollectionDTO;
import org.gms.model.dto.ToyCollectionDTO.CategoryDTO;
import org.gms.model.dto.ToyCollectionDTO.ItemDTO;
import org.gms.model.dto.ToyCollectionDTO.ProgressDTO;
import org.gms.server.ItemInformationProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 玩具收集服务 —— 分类/物品CRUD + 收集逻辑 + 缓存刷新
 */
@Slf4j
@Service
@AllArgsConstructor
public class ToyCollectionService {

    private final ToyCollectionCategoryMapper categoryMapper;
    private final ToyCollectionItemMapper itemMapper;
    private final ToyCollectionProgressMapper progressMapper;

    @PostConstruct
    public void init() {
        refreshCache();
        log.info("玩具收集配置加载完成");
    }

    // ==================== 分类 CRUD ====================

    /** 获取所有分类列表（含物品列表） */
    public List<CategoryDTO> getCategoryList() {
        List<ToyCollectionCategoryDO> categories = categoryMapper.selectAll();
        List<ToyCollectionItemDO> allItems = itemMapper.selectAll();
        List<CategoryDTO> result = new ArrayList<>();
        for (ToyCollectionCategoryDO cat : categories) {
            result.add(toCategoryDTO(cat, allItems));
        }
        result.sort(Comparator.comparingInt(c -> c.getSortOrder() != null ? c.getSortOrder() : 0));
        return result;
    }

    /** 根据ID获取单个分类（含物品列表） */
    public CategoryDTO getCategoryById(Long id) {
        ToyCollectionCategoryDO cat = categoryMapper.selectOneById(id);
        if (cat == null) return null;
        return toCategoryDTO(cat, itemMapper.selectAll());
    }

    /** 保存分类（新增或更新，级联保存物品列表） */
    @Transactional
    public CategoryDTO saveCategory(CategoryDTO dto) {
        ToyCollectionCategoryDO cat = ToyCollectionCategoryDO.builder()
                .id(dto.getId())
                .name(dto.getName())
                .icon(dto.getIcon() != null ? dto.getIcon() : "")
                .sortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0)
                .enabled(dto.getEnabled() != null ? dto.getEnabled() : 1)
                .build();
        if (cat.getId() != null) {
            categoryMapper.update(cat);
            // 删除旧物品，重新插入
            deleteItemsByCategoryId(cat.getId());
        } else {
            categoryMapper.insert(cat);
        }

        // 级联保存物品列表
        if (dto.getItems() != null) {
            for (ItemDTO itemDTO : dto.getItems()) {
                itemMapper.insert(ToyCollectionItemDO.builder()
                        .categoryId(cat.getId())
                        .itemId(itemDTO.getItemId())
                        .requiredQuantity(itemDTO.getRequiredQuantity() != null ? itemDTO.getRequiredQuantity() : 1)
                        .rewardItemId(itemDTO.getRewardItemId() != null ? itemDTO.getRewardItemId() : 0)
                        .rewardQuantity(itemDTO.getRewardQuantity() != null ? itemDTO.getRewardQuantity() : 1)
                        .sortOrder(itemDTO.getSortOrder() != null ? itemDTO.getSortOrder() : 0)
                        .enabled(itemDTO.getEnabled() != null ? itemDTO.getEnabled() : 1)
                        .build());
            }
        }

        refreshCache();
        return getCategoryById(cat.getId());
    }

    /** 删除分类（级联删除物品） */
    @Transactional
    public void deleteCategory(Long id) {
        deleteItemsByCategoryId(id);
        categoryMapper.deleteById(id);
        refreshCache();
    }

    private void deleteItemsByCategoryId(Long categoryId) {
        itemMapper.deleteByQuery(
                QueryWrapper.create().where("category_id = ?", categoryId));
    }

    // ==================== 物品 CRUD ====================

    /** 获取指定分类的物品列表 */
    public List<ItemDTO> getItemList(Long categoryId) {
        List<ToyCollectionItemDO> items = itemMapper.selectListByQuery(
                QueryWrapper.create().where("category_id = ?", categoryId).orderBy("sort_order", true));
        List<ItemDTO> result = new ArrayList<>();
        for (ToyCollectionItemDO item : items) {
            result.add(toItemDTO(item));
        }
        return result;
    }

    /** 保存单个物品 */
    @Transactional
    public ItemDTO saveItem(ItemDTO dto) {
        ToyCollectionItemDO item = ToyCollectionItemDO.builder()
                .id(dto.getId())
                .categoryId(dto.getCategoryId())
                .itemId(dto.getItemId())
                .requiredQuantity(dto.getRequiredQuantity() != null ? dto.getRequiredQuantity() : 1)
                .rewardItemId(dto.getRewardItemId() != null ? dto.getRewardItemId() : 0)
                .rewardQuantity(dto.getRewardQuantity() != null ? dto.getRewardQuantity() : 1)
                .sortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0)
                .enabled(dto.getEnabled() != null ? dto.getEnabled() : 1)
                .build();
        if (item.getId() != null) {
            itemMapper.update(item);
        } else {
            itemMapper.insert(item);
        }
        refreshCache();
        return toItemDTO(itemMapper.selectOneById(item.getId()));
    }

    /** 删除物品 */
    @Transactional
    public void deleteItem(Long id) {
        itemMapper.deleteById(id);
        refreshCache();
    }

    // ==================== 收集逻辑 ====================

    /**
     * 单个提交：从角色背包扣除1个指定物品，更新收集进度
     * @return {success, message, itemConfigId, submitted, required, rewardGiven}
     */
    @Transactional
    public Map<String, Object> submitItem(Integer characterId, Long itemConfigId) {
        Map<String, Object> result = new LinkedHashMap<>();
        ToyCollectionItemDO itemConfig = itemMapper.selectOneById(itemConfigId);
        if (itemConfig == null) {
            result.put("success", false);
            result.put("message", "物品配置不存在");
            return result;
        }

        Character chr = getCharacterById(characterId);
        if (chr == null) {
            result.put("success", false);
            result.put("message", "角色不在线");
            return result;
        }

        // 检查背包中是否有该物品
        int holding = countItemInInventory(chr, itemConfig.getItemId());
        if (holding <= 0) {
            result.put("success", false);
            result.put("message", "背包中没有该物品");
            return result;
        }

        // 从背包扣除1个
        boolean deducted = deductFromInventory(chr, itemConfig.getItemId(), 1);
        if (!deducted) {
            result.put("success", false);
            result.put("message", "扣除物品失败");
            return result;
        }

        // 更新进度
        ToyCollectionProgressDO progress = getOrCreateProgress(characterId, itemConfigId);
        int newSubmitted = (progress.getSubmittedQuantity() != null ? progress.getSubmittedQuantity() : 0) + 1;
        progress.setSubmittedQuantity(newSubmitted);
        progressMapper.update(progress);

        result.put("success", true);
        result.put("itemConfigId", itemConfigId);
        result.put("submitted", newSubmitted);
        result.put("required", itemConfig.getRequiredQuantity());

        // 检查是否完成并发奖励
        if (newSubmitted >= itemConfig.getRequiredQuantity()
                && itemConfig.getRewardItemId() != null && itemConfig.getRewardItemId() > 0) {
            InventoryManipulator.addById(chr.getClient(), itemConfig.getRewardItemId(), (short) itemConfig.getRewardQuantity().intValue());
            result.put("rewardGiven", true);
            result.put("rewardItemId", itemConfig.getRewardItemId());
            result.put("rewardQuantity", itemConfig.getRewardQuantity());
        } else {
            result.put("rewardGiven", false);
        }
        return result;
    }

    /**
     * 一键提交：批量提交指定分类下所有匹配的背包物品
     * @return {success, summary, details: [{itemConfigId, itemId, submitted, totalSubmitted, required, rewardGiven}]}
     */
    @Transactional
    public Map<String, Object> submitAllInCategory(Integer characterId, Long categoryId) {
        Map<String, Object> result = new LinkedHashMap<>();
        Character chr = getCharacterById(characterId);
        if (chr == null) {
            result.put("success", false);
            result.put("message", "角色不在线");
            return result;
        }

        List<ToyCollectionItemDO> items = itemMapper.selectListByQuery(
                QueryWrapper.create()
                        .where("category_id = ?", categoryId)
                        .and("enabled = ?", 1));
        if (items.isEmpty()) {
            result.put("success", false);
            result.put("message", "该分类没有可收集的物品");
            return result;
        }

        List<Map<String, Object>> details = new ArrayList<>();
        boolean anySubmitted = false;

        for (ToyCollectionItemDO cfg : items) {
            ToyCollectionProgressDO progress = getOrCreateProgress(characterId, cfg.getId());
            int currentSubmitted = progress.getSubmittedQuantity() != null ? progress.getSubmittedQuantity() : 0;
            int needed = cfg.getRequiredQuantity() - currentSubmitted;
            if (needed <= 0) continue;

            int holding = countItemInInventory(chr, cfg.getItemId());
            int toSubmit = Math.min(needed, holding);
            if (toSubmit <= 0) continue;

            boolean deducted = deductFromInventory(chr, cfg.getItemId(), toSubmit);
            if (!deducted) continue;

            int newSubmitted = currentSubmitted + toSubmit;
            progress.setSubmittedQuantity(newSubmitted);
            progressMapper.update(progress);
            anySubmitted = true;

            Map<String, Object> detail = new LinkedHashMap<>();
            detail.put("itemConfigId", cfg.getId());
            detail.put("itemId", cfg.getItemId());
            detail.put("submitted", toSubmit);
            detail.put("totalSubmitted", newSubmitted);
            detail.put("required", cfg.getRequiredQuantity());
            detail.put("rewardGiven", false);

            if (newSubmitted >= cfg.getRequiredQuantity()
                    && cfg.getRewardItemId() != null && cfg.getRewardItemId() > 0) {
                InventoryManipulator.addById(chr.getClient(), cfg.getRewardItemId(), (short) cfg.getRewardQuantity().intValue());
                detail.put("rewardGiven", true);
                detail.put("rewardItemId", cfg.getRewardItemId());
                detail.put("rewardQuantity", cfg.getRewardQuantity());
            }
            details.add(detail);
        }

        result.put("success", true);
        result.put("anySubmitted", anySubmitted);
        result.put("details", details);
        return result;
    }

    /** 获取角色收集进度 */
    public List<ProgressDTO> getProgress(Integer characterId, Long categoryId) {
        List<ToyCollectionItemDO> items;
        if (categoryId != null) {
            items = itemMapper.selectListByQuery(
                    QueryWrapper.create().where("category_id = ?", categoryId));
        } else {
            items = itemMapper.selectAll();
        }

        List<ToyCollectionProgressDO> progressList = progressMapper.selectListByQuery(
                QueryWrapper.create().where("character_id = ?", characterId));
        Map<Long, ToyCollectionProgressDO> progressMap = new HashMap<>();
        for (ToyCollectionProgressDO p : progressList) {
            progressMap.put(p.getItemConfigId(), p);
        }

        List<ProgressDTO> result = new ArrayList<>();
        for (ToyCollectionItemDO item : items) {
            ToyCollectionProgressDO p = progressMap.get(item.getId());
            result.add(ProgressDTO.builder()
                    .itemConfigId(item.getId())
                    .characterId(characterId)
                    .itemId(item.getItemId())
                    .requiredQuantity(item.getRequiredQuantity())
                    .submittedQuantity(p != null ? p.getSubmittedQuantity() : 0)
                    .build());
        }
        return result;
    }

    /** 获取或创建进度记录 */
    private ToyCollectionProgressDO getOrCreateProgress(Integer characterId, Long itemConfigId) {
        List<ToyCollectionProgressDO> list = progressMapper.selectListByQuery(
                QueryWrapper.create()
                        .where("character_id = ?", characterId)
                        .and("item_config_id = ?", itemConfigId));
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        ToyCollectionProgressDO progress = ToyCollectionProgressDO.builder()
                .characterId(characterId)
                .itemConfigId(itemConfigId)
                .submittedQuantity(0)
                .rewardClaimed(0)
                .build();
        progressMapper.insert(progress);
        return progress;
    }

    // ==================== 背包操作 ====================

    /** 计算角色背包中指定物品的总数量（getItemQuantity已统计所有栏位） */
    private int countItemInInventory(Character chr, int itemId) {
        return chr.getItemQuantity(itemId, false);
    }

    /** 从角色背包扣除指定数量的物品（removeById自动处理所有栏位） */
    private boolean deductFromInventory(Character chr, int itemId, int quantity) {
        int held = chr.getItemQuantity(itemId, false);
        if (held < quantity) return false;
        InventoryManipulator.removeById(chr.getClient(),
                ItemConstants.getInventoryType(itemId), itemId, quantity, true, false);
        return true;
    }

    // ==================== 工具方法 ====================

    /** 根据角色ID获取在线角色 */
    private Character getCharacterById(Integer characterId) {
        for (World world :
                org.gms.net.server.Server.getInstance().getWorlds()) {
            Character chr = world.getPlayerStorage().getCharacterById(characterId);
            if (chr != null) return chr;
        }
        return null;
    }

    /** 转换分类DO为DTO */
    private CategoryDTO toCategoryDTO(ToyCollectionCategoryDO cat, List<ToyCollectionItemDO> allItems) {
        List<ItemDTO> items = new ArrayList<>();
        for (ToyCollectionItemDO item : allItems) {
            if (item.getCategoryId().equals(cat.getId())) {
                items.add(toItemDTO(item));
            }
        }
        items.sort(Comparator.comparingInt(i -> i.getSortOrder() != null ? i.getSortOrder() : 0));
        return CategoryDTO.builder()
                .id(cat.getId())
                .name(cat.getName())
                .icon(cat.getIcon())
                .sortOrder(cat.getSortOrder())
                .enabled(cat.getEnabled())
                .items(items)
                .build();
    }

    /** 转换物品DO为DTO */
    private ItemDTO toItemDTO(ToyCollectionItemDO item) {
        return ItemDTO.builder()
                .id(item.getId())
                .categoryId(item.getCategoryId())
                .itemId(item.getItemId())
                .itemName(item.getItemId() != null ? resolveItemName(item.getItemId()) : "")
                .requiredQuantity(item.getRequiredQuantity())
                .rewardItemId(item.getRewardItemId())
                .rewardItemName(item.getRewardItemId() != null && item.getRewardItemId() > 0
                        ? resolveItemName(item.getRewardItemId()) : "")
                .rewardQuantity(item.getRewardQuantity())
                .sortOrder(item.getSortOrder())
                .enabled(item.getEnabled())
                .build();
    }

    /** 解析物品名称 */
    private String resolveItemName(int itemId) {
        try {
            return ItemInformationProvider.getInstance().getName(itemId);
        } catch (Exception e) {
            return "未知物品(" + itemId + ")";
        }
    }

    /** 刷新配置缓存 */
    private void refreshCache() {
        ToyCollectionConfigManager.load(categoryMapper.selectAll(), itemMapper.selectAll());
    }
}
