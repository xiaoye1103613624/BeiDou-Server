package org.gms.service;

import com.mybatisflex.core.query.QueryWrapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.gms.config.ForgeRecipeManager;
import org.gms.dao.entity.ForgeRecipeDO;
import org.gms.dao.mapper.ForgeRecipeMapper;
import org.gms.model.dto.ForgeRecipeDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 打造配方服务：列出已启用配方、校验品级要求并完成打造（仅处理锻造师经验，
 * 金币/材料物品由脚本侧在调用 {@link #craft} 前自行校验并扣除，成功后再发放产出装备，
 * 与炼金系统"调用前自行扣除"的约定一致）。
 */
@Slf4j
@Service
@AllArgsConstructor
public class ForgeRecipeService {

    private final ForgeRecipeMapper recipeMapper;
    private final ForgeService forgeService;

    /**
     * 查询所有已启用配方，按品级、排序号升序。
     */
    public List<ForgeRecipeDO> listEnabledRecipes() {
        return recipeMapper.selectListByQuery(
                QueryWrapper.create()
                        .where("enabled = 1")
                        .orderBy("tier_required", true)
                        .orderBy("sort_order", true));
    }

    /**
     * 查询单个配方。
     */
    public ForgeRecipeDO getRecipe(Long recipeId) {
        return recipeMapper.selectOneById(recipeId);
    }

    /**
     * 打造：校验配方启用、锻造师品级是否达到要求、增加锻造师经验。
     * 金币/材料的校验与扣除、产出装备的发放均由脚本在调用本方法前后自行处理。
     *
     * @return 打造后的累计经验
     * @throws IllegalArgumentException 配方不存在/已禁用、品级不足
     */
    @Transactional
    public long craft(Integer characterId, Long recipeId) {
        ForgeRecipeDO recipe = getRecipe(recipeId);
        if (recipe == null || recipe.getEnabled() == null || recipe.getEnabled() != 1) {
            throw new IllegalArgumentException("配方不存在或已禁用");
        }
        var forge = forgeService.getOrCreate(characterId);
        int currentTierIndex = forgeService.getTierIndex(forge.getExp());
        if (currentTierIndex < recipe.getTierRequired()) {
            throw new IllegalArgumentException("锻造师等级不足，无法打造该配方");
        }
        long newExp = forgeService.addExp(characterId, recipe.getExpGain());
        log.info("角色 {} 打造配方 {} 成功，获得经验 {}", characterId, recipeId, recipe.getExpGain());
        return newExp;
    }

    // ==================== 管理后台：配方增删改查 ====================

    /** 查询所有配方（含已禁用），供后台管理列表使用 */
    public List<ForgeRecipeDTO> listAll() {
        List<ForgeRecipeDTO> result = new ArrayList<>();
        for (ForgeRecipeDO d : recipeMapper.selectListByQuery(
                QueryWrapper.create().orderBy("tier_required", true).orderBy("sort_order", true))) {
            result.add(toDTO(d));
        }
        return result;
    }

    /** 查询单个配方详情（含已禁用） */
    public ForgeRecipeDTO getRecipeDetail(Long id) {
        ForgeRecipeDO d = recipeMapper.selectOneById(id);
        return d == null ? null : toDTO(d);
    }

    /** 保存配方（新增或更新），保存后刷新 {@link ForgeRecipeManager} 缓存 */
    public ForgeRecipeDTO saveRecipe(ForgeRecipeDTO dto) {
        ForgeRecipeDO entity = ForgeRecipeDO.builder()
                .id(dto.getId())
                .name(dto.getName())
                .tierRequired(dto.getTierRequired())
                .resultItemId(dto.getResultItemId())
                .expGain(dto.getExpGain())
                .mesoCost(dto.getMesoCost())
                .material1ItemId(dto.getMaterial1ItemId())
                .material1Count(dto.getMaterial1Count())
                .material2ItemId(dto.getMaterial2ItemId())
                .material2Count(dto.getMaterial2Count())
                .material3ItemId(dto.getMaterial3ItemId())
                .material3Count(dto.getMaterial3Count())
                .strMin(dto.getStrMin())
                .strMax(dto.getStrMax())
                .dexMin(dto.getDexMin())
                .dexMax(dto.getDexMax())
                .intMin(dto.getIntMin())
                .intMax(dto.getIntMax())
                .lukMin(dto.getLukMin())
                .lukMax(dto.getLukMax())
                .watkMin(dto.getWatkMin())
                .watkMax(dto.getWatkMax())
                .matkMin(dto.getMatkMin())
                .matkMax(dto.getMatkMax())
                .sortOrder(dto.getSortOrder())
                .enabled(dto.getEnabled() != null ? dto.getEnabled() : 1)
                .build();
        if (entity.getId() != null) {
            recipeMapper.update(entity);
        } else {
            recipeMapper.insert(entity);
        }
        ForgeRecipeManager.reload();
        return toDTO(recipeMapper.selectOneById(entity.getId()));
    }

    /** 切换配方启用状态，切换后刷新缓存 */
    public void toggleEnabled(Long id) {
        ForgeRecipeDO d = recipeMapper.selectOneById(id);
        if (d == null) {
            return;
        }
        d.setEnabled(d.getEnabled() != null && d.getEnabled() == 1 ? 0 : 1);
        recipeMapper.update(d);
        ForgeRecipeManager.reload();
    }

    /** 删除配方，删除后刷新缓存 */
    public void deleteRecipe(Long id) {
        recipeMapper.deleteById(id);
        ForgeRecipeManager.reload();
    }

    private ForgeRecipeDTO toDTO(ForgeRecipeDO d) {
        return ForgeRecipeDTO.builder()
                .id(d.getId())
                .name(d.getName())
                .tierRequired(d.getTierRequired())
                .resultItemId(d.getResultItemId())
                .expGain(d.getExpGain())
                .mesoCost(d.getMesoCost())
                .material1ItemId(d.getMaterial1ItemId())
                .material1Count(d.getMaterial1Count())
                .material2ItemId(d.getMaterial2ItemId())
                .material2Count(d.getMaterial2Count())
                .material3ItemId(d.getMaterial3ItemId())
                .material3Count(d.getMaterial3Count())
                .strMin(d.getStrMin())
                .strMax(d.getStrMax())
                .dexMin(d.getDexMin())
                .dexMax(d.getDexMax())
                .intMin(d.getIntMin())
                .intMax(d.getIntMax())
                .lukMin(d.getLukMin())
                .lukMax(d.getLukMax())
                .watkMin(d.getWatkMin())
                .watkMax(d.getWatkMax())
                .matkMin(d.getMatkMin())
                .matkMax(d.getMatkMax())
                .sortOrder(d.getSortOrder())
                .enabled(d.getEnabled())
                .build();
    }
}
