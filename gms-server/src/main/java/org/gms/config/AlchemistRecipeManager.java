package org.gms.config;

import org.gms.dao.entity.AlchemistRecipeDO;
import org.gms.manager.ServerManager;
import org.gms.service.AlchemistRecipeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 炼药师配方静态门面，供 GraalVM JS 脚本通过 {@code Java.type()} 调用。
 * <p>
 * 金币/材料的校验与扣除、产出物品的发放均由脚本侧完成（与炼金系统一致的约定），
 * 本门面只负责品级校验、体力扣除与炼药师经验增加。
 * </p>
 * <p>
 * 配方数据为静态缓存，{@link #craft} 内部仍由 {@link AlchemistRecipeService#craft}
 * 实时查库校验；后台修改配方后需调用 {@link #reload()} 刷新缓存。
 * </p>
 */
public class AlchemistRecipeManager {

    private static final Logger log = LoggerFactory.getLogger(AlchemistRecipeManager.class);

    /** 配方ID → 配方实体（仅缓存已启用的配方） */
    private static final Map<Long, AlchemistRecipeDO> recipeCache = new ConcurrentHashMap<>();
    /** 已启用配方列表缓存（按品级、排序号升序，与 {@link #recipeCache} 同步刷新） */
    private static volatile List<AlchemistRecipeDO> enabledRecipeListCache = null;

    private AlchemistRecipeManager() {}

    private static AlchemistRecipeService getService() {
        var context = ServerManager.getApplicationContext();
        if (context == null) {
            throw new IllegalStateException("Spring 上下文不可用");
        }
        return context.getBean(AlchemistRecipeService.class);
    }

    /**
     * 手动强制刷新配方缓存。脚本中可调用: {@code AlchemistRecipeManager.reload()}
     */
    public static synchronized void reload() {
        try {
            List<AlchemistRecipeDO> recipes = getService().listEnabledRecipes();
            Map<Long, AlchemistRecipeDO> newCache = new ConcurrentHashMap<>();
            for (AlchemistRecipeDO recipe : recipes) {
                newCache.put(recipe.getId(), recipe);
            }
            recipeCache.clear();
            recipeCache.putAll(newCache);
            enabledRecipeListCache = recipes;
            log.info("AlchemistRecipeManager 缓存已刷新：共 {} 条已启用配方", recipes.size());
        } catch (Exception e) {
            log.error("刷新炼药配方缓存失败", e);
        }
    }

    /** 获取缓存中的配方列表，缓存为空时触发一次加载 */
    private static List<AlchemistRecipeDO> getCachedList() {
        List<AlchemistRecipeDO> cached = enabledRecipeListCache;
        if (cached == null) {
            reload();
            cached = enabledRecipeListCache;
        }
        return cached != null ? cached : new ArrayList<>();
    }

    private static Map<String, Object> toMap(AlchemistRecipeDO recipe) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", recipe.getId());
        map.put("tierRequired", recipe.getTierRequired());
        map.put("resultItemId", recipe.getResultItemId());
        map.put("resultCount", recipe.getResultCount());
        map.put("expGain", recipe.getExpGain());
        map.put("staminaCost", recipe.getStaminaCost());
        map.put("mesoCost", recipe.getMesoCost());
        map.put("material1ItemId", recipe.getMaterial1ItemId());
        map.put("material1Count", recipe.getMaterial1Count());
        map.put("material2ItemId", recipe.getMaterial2ItemId());
        map.put("material2Count", recipe.getMaterial2Count());
        map.put("material3ItemId", recipe.getMaterial3ItemId());
        map.put("material3Count", recipe.getMaterial3Count());
        map.put("material4ItemId", recipe.getMaterial4ItemId());
        map.put("material4Count", recipe.getMaterial4Count());
        map.put("material5ItemId", recipe.getMaterial5ItemId());
        map.put("material5Count", recipe.getMaterial5Count());
        return map;
    }

    /**
     * 查询所有已启用配方（按品级、排序号升序）。
     */
    public static List<Map<String, Object>> listEnabledRecipes() {
        try {
            List<Map<String, Object>> list = new ArrayList<>();
            for (AlchemistRecipeDO recipe : getCachedList()) {
                list.add(toMap(recipe));
            }
            return list;
        } catch (Exception e) {
            log.error("查询炼药配方失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 查询单个配方详情（命中缓存），不存在返回 null。
     */
    public static Map<String, Object> getRecipe(Long recipeId) {
        try {
            if (enabledRecipeListCache == null) reload();
            AlchemistRecipeDO recipe = recipeCache.get(recipeId);
            return recipe != null ? toMap(recipe) : null;
        } catch (Exception e) {
            log.error("查询炼药配方失败", e);
            return null;
        }
    }

    /**
     * 炼制：校验品级要求、扣除体力、增加炼药师经验。
     * 金币/材料的校验与扣除、产出物品的发放需脚本在调用前后自行处理。
     *
     * @return {success, message, exp(炼制后的累计经验)}
     */
    public static Map<String, Object> craft(Integer characterId, Integer accountId, Long recipeId) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            long newExp = getService().craft(characterId, accountId, recipeId);
            result.put("success", true);
            result.put("message", "炼制成功");
            result.put("exp", newExp);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
}