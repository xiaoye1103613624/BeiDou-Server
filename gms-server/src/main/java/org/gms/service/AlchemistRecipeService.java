package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.config.AlchemistRecipeManager;
import org.gms.dao.entity.AlchemistRecipeDO;
import org.gms.dao.mapper.AlchemistRecipeMapper;
import org.gms.model.dto.AlchemistRecipeDTO;
import org.gms.server.ItemInformationProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 炼药师配方服务：列出已启用配方、校验品级要求并完成炼制（仅处理体力与炼药师经验，
 * 金币/材料物品由脚本侧在调用 {@link #craft} 前自行校验并扣除，成功后再发放产出物品，
 * 与炼金系统"调用前自行扣除"的约定一致）。
 */
@Slf4j
@Service
@AllArgsConstructor
public class AlchemistRecipeService {

    private final AlchemistRecipeMapper recipeMapper;
    private final AlchemistService alchemistService;
    private final StaminaService staminaService;

    /**
     * 查询所有已启用配方，按品级、排序号升序。
     */
    public List<AlchemistRecipeDO> listEnabledRecipes() {
        return recipeMapper.selectListByQuery(
                QueryWrapper.create()
                        .where("enabled = 1")
                        .orderBy("tier_required", true)
                        .orderBy("sort_order", true));
    }

    /**
     * 查询单个配方。
     */
    public AlchemistRecipeDO getRecipe(Long recipeId) {
        return recipeMapper.selectOneById(recipeId);
    }

    /**
     * 炼制：校验配方启用、炼药师品级是否达到要求、扣除体力、增加炼药师经验。
     * 金币/材料的校验与扣除、产出物品的发放均由脚本在调用本方法前后自行处理。
     *
     * @return 炼制后的累计经验
     * @throws IllegalArgumentException 配方不存在/已禁用、品级不足
     * @throws IllegalStateException    体力不足
     */
    @Transactional
    public long craft(Integer characterId, Integer accountId, Long recipeId) {
        AlchemistRecipeDO recipe = getRecipe(recipeId);
        if (recipe == null || recipe.getEnabled() == null || recipe.getEnabled() != 1) {
            throw new IllegalArgumentException("配方不存在或已禁用");
        }
        var alchemist = alchemistService.getOrCreate(characterId);
        int currentTierIndex = alchemistService.getTierIndex(alchemist.getExp());
        if (currentTierIndex < recipe.getTierRequired()) {
            throw new IllegalArgumentException("炼药师等级不足，无法炼制该配方");
        }
        staminaService.consumeStamina(accountId, recipe.getStaminaCost());
        long newExp = alchemistService.addExp(characterId, recipe.getExpGain());
        log.info("角色 {} 炼制炼药配方 {} 成功，获得经验 {}", characterId, recipeId, recipe.getExpGain());
        return newExp;
    }

    // ==================== 管理后台：配方增删改查 ====================

    /** 查询所有配方（含已禁用），供后台管理列表使用 */
    public List<AlchemistRecipeDTO> listAll() {
        List<AlchemistRecipeDTO> result = new ArrayList<>();
        for (AlchemistRecipeDO d : recipeMapper.selectListByQuery(
                QueryWrapper.create().orderBy("tier_required", true).orderBy("sort_order", true))) {
            result.add(toDTO(d));
        }
        return result;
    }

    /** 查询单个配方详情（含已禁用） */
    public AlchemistRecipeDTO getRecipeDetail(Long id) {
        AlchemistRecipeDO d = recipeMapper.selectOneById(id);
        return d == null ? null : toDTO(d);
    }

    /** 保存配方（新增或更新），保存后刷新 {@link AlchemistRecipeManager} 缓存 */
    public AlchemistRecipeDTO saveRecipe(AlchemistRecipeDTO dto) {
        AlchemistRecipeDO entity = AlchemistRecipeDO.builder()
                .id(dto.getId())
                .tierRequired(dto.getTierRequired())
                .resultItemId(dto.getResultItemId())
                .resultCount(dto.getResultCount())
                .expGain(dto.getExpGain())
                .staminaCost(dto.getStaminaCost())
                .mesoCost(dto.getMesoCost())
                .material1ItemId(dto.getMaterial1ItemId())
                .material1Count(dto.getMaterial1Count())
                .material2ItemId(dto.getMaterial2ItemId())
                .material2Count(dto.getMaterial2Count())
                .material3ItemId(dto.getMaterial3ItemId())
                .material3Count(dto.getMaterial3Count())
                .material4ItemId(dto.getMaterial4ItemId())
                .material4Count(dto.getMaterial4Count())
                .material5ItemId(dto.getMaterial5ItemId())
                .material5Count(dto.getMaterial5Count())
                .sortOrder(dto.getSortOrder())
                .enabled(dto.getEnabled() != null ? dto.getEnabled() : 1)
                .build();
        if (entity.getId() != null && entity.getId() > 0) {
            AlchemistRecipeDO existing = recipeMapper.selectOneById(entity.getId());
            if (existing != null) {
                entity.setCreateTime(existing.getCreateTime());
            }
            entity.setUpdateTime(new Date());
            recipeMapper.update(entity);
        } else {
            entity.setId(null);
            Date now = new Date();
            entity.setCreateTime(now);
            entity.setUpdateTime(now);
            recipeMapper.insert(entity);
        }
        AlchemistRecipeManager.reload();
        return toDTO(recipeMapper.selectOneById(entity.getId()));
    }

    /** 切换配方启用状态，切换后刷新缓存 */
    public void toggleEnabled(Long id) {
        AlchemistRecipeDO d = recipeMapper.selectOneById(id);
        if (d == null) {
            return;
        }
        d.setEnabled(d.getEnabled() != null && d.getEnabled() == 1 ? 0 : 1);
        recipeMapper.update(d);
        AlchemistRecipeManager.reload();
    }

    /** 删除配方，删除后刷新缓存 */
    public void deleteRecipe(Long id) {
        recipeMapper.deleteById(id);
        AlchemistRecipeManager.reload();
    }

    private String getItemName(Integer itemId) {
        try {
            return itemId == null ? null : ItemInformationProvider.getInstance().getName(itemId);
        } catch (Exception e) {
            return null;
        }
    }

    private AlchemistRecipeDTO toDTO(AlchemistRecipeDO d) {
        return AlchemistRecipeDTO.builder()
                .id(d.getId())
                .tierRequired(d.getTierRequired())
                .resultItemId(d.getResultItemId())
                .resultItemName(getItemName(d.getResultItemId()))
                .resultCount(d.getResultCount())
                .expGain(d.getExpGain())
                .staminaCost(d.getStaminaCost())
                .mesoCost(d.getMesoCost())
                .material1ItemId(d.getMaterial1ItemId())
                .material1Count(d.getMaterial1Count())
                .material2ItemId(d.getMaterial2ItemId())
                .material2Count(d.getMaterial2Count())
                .material3ItemId(d.getMaterial3ItemId())
                .material3Count(d.getMaterial3Count())
                .material4ItemId(d.getMaterial4ItemId())
                .material4Count(d.getMaterial4Count())
                .material5ItemId(d.getMaterial5ItemId())
                .material5Count(d.getMaterial5Count())
                .sortOrder(d.getSortOrder())
                .enabled(d.getEnabled())
                .build();
    }
}