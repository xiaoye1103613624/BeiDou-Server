package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.config.AlchemyRecipeManager;
import org.gms.dao.entity.AlchemyRecipeDO;
import org.gms.dao.mapper.AlchemyRecipeMapper;
import org.gms.model.dto.AlchemyRecipeDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 炼金配方服务：列出已启用配方、校验品级要求并完成炼制（仅处理体力与炼金师经验，
 * 金币/点券/材料物品由脚本侧在调用 {@link #craft} 前自行校验并扣除，成功后再发放产出物品，
 * 与银行系统"调用前自行扣除"的约定一致）。
 */
@Slf4j
@Service
@AllArgsConstructor
public class AlchemyRecipeService {

    private final AlchemyRecipeMapper recipeMapper;
    private final AlchemyService alchemyService;
    private final StaminaService staminaService;

    /**
     * 查询所有已启用配方，按品级、排序号升序。
     */
    public List<AlchemyRecipeDO> listEnabledRecipes() {
        return recipeMapper.selectListByQuery(
                QueryWrapper.create()
                        .where("enabled = 1")
                        .orderBy("tier_required", true)
                        .orderBy("sort_order", true));
    }

    /**
     * 查询单个配方。
     */
    public AlchemyRecipeDO getRecipe(Long recipeId) {
        return recipeMapper.selectOneById(recipeId);
    }

    /**
     * 炼制：校验配方启用、炼金师品级是否达到要求、扣除体力、增加炼金师经验。
     * 金币/点券/材料的校验与扣除、产出物品的发放均由脚本在调用本方法前后自行处理。
     *
     * @return 炼制后的累计经验
     * @throws IllegalArgumentException 配方不存在/已禁用、品级不足
     * @throws IllegalStateException    体力不足
     */
    @Transactional
    public long craft(Integer characterId, Integer accountId, Long recipeId) {
        AlchemyRecipeDO recipe = getRecipe(recipeId);
        if (recipe == null || recipe.getEnabled() == null || recipe.getEnabled() != 1) {
            throw new IllegalArgumentException("配方不存在或已禁用");
        }
        var alchemy = alchemyService.getOrCreate(characterId);
        int currentTierIndex = alchemyService.getTierIndex(alchemy.getExp());
        if (currentTierIndex < recipe.getTierRequired()) {
            throw new IllegalArgumentException("炼金师等级不足，无法炼制该配方");
        }
        staminaService.consumeStamina(accountId, recipe.getStaminaCost());
        long newExp = alchemyService.addExp(characterId, recipe.getExpGain());
        log.info("角色 {} 炼制配方 {} 成功，获得经验 {}", characterId, recipeId, recipe.getExpGain());
        return newExp;
    }

    // ==================== 管理后台：配方增删改查 ====================

    /** 查询所有配方（含已禁用），供后台管理列表使用 */
    public List<AlchemyRecipeDTO> listAll() {
        List<AlchemyRecipeDTO> result = new ArrayList<>();
        for (AlchemyRecipeDO d : recipeMapper.selectListByQuery(
                QueryWrapper.create().orderBy("tier_required", true).orderBy("sort_order", true))) {
            result.add(toDTO(d));
        }
        return result;
    }

    /** 查询单个配方详情（含已禁用） */
    public AlchemyRecipeDTO getRecipeDetail(Long id) {
        AlchemyRecipeDO d = recipeMapper.selectOneById(id);
        return d == null ? null : toDTO(d);
    }

    /** 保存配方（新增或更新），保存后刷新 {@link AlchemyRecipeManager} 缓存 */
    public AlchemyRecipeDTO saveRecipe(AlchemyRecipeDTO dto) {
        AlchemyRecipeDO entity = AlchemyRecipeDO.builder()
                .id(dto.getId())
                .tierRequired(dto.getTierRequired())
                .resultItemId(dto.getResultItemId())
                .resultCount(dto.getResultCount())
                .expGain(dto.getExpGain())
                .staminaCost(dto.getStaminaCost())
                .mesoCost(dto.getMesoCost())
                .cashCost(dto.getCashCost())
                .material1ItemId(dto.getMaterial1ItemId())
                .material1Count(dto.getMaterial1Count())
                .material2ItemId(dto.getMaterial2ItemId())
                .material2Count(dto.getMaterial2Count())
                .material3ItemId(dto.getMaterial3ItemId())
                .material3Count(dto.getMaterial3Count())
                .sortOrder(dto.getSortOrder())
                .enabled(dto.getEnabled() != null ? dto.getEnabled() : 1)
                .build();
        if (entity.getId() != null && entity.getId() > 0) {
            AlchemyRecipeDO existing = recipeMapper.selectOneById(entity.getId());
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
        AlchemyRecipeManager.reload();
        return toDTO(recipeMapper.selectOneById(entity.getId()));
    }

    /** 切换配方启用状态，切换后刷新缓存 */
    public void toggleEnabled(Long id) {
        AlchemyRecipeDO d = recipeMapper.selectOneById(id);
        if (d == null) {
            return;
        }
        d.setEnabled(d.getEnabled() != null && d.getEnabled() == 1 ? 0 : 1);
        recipeMapper.update(d);
        AlchemyRecipeManager.reload();
    }

    /** 删除配方，删除后刷新缓存 */
    public void deleteRecipe(Long id) {
        recipeMapper.deleteById(id);
        AlchemyRecipeManager.reload();
    }

    private AlchemyRecipeDTO toDTO(AlchemyRecipeDO d) {
        return AlchemyRecipeDTO.builder()
                .id(d.getId())
                .tierRequired(d.getTierRequired())
                .resultItemId(d.getResultItemId())
                .resultCount(d.getResultCount())
                .expGain(d.getExpGain())
                .staminaCost(d.getStaminaCost())
                .mesoCost(d.getMesoCost())
                .cashCost(d.getCashCost())
                .material1ItemId(d.getMaterial1ItemId())
                .material1Count(d.getMaterial1Count())
                .material2ItemId(d.getMaterial2ItemId())
                .material2Count(d.getMaterial2Count())
                .material3ItemId(d.getMaterial3ItemId())
                .material3Count(d.getMaterial3Count())
                .sortOrder(d.getSortOrder())
                .enabled(d.getEnabled())
                .build();
    }
}
