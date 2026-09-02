package org.gms.config;

import org.gms.dao.entity.ForgeRecipeDO;
import org.gms.manager.ServerManager;
import org.gms.service.ForgeRecipeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 打造配方静态门面，供 GraalVM JS 脚本通过 {@code Java.type()} 调用。
 * <p>
 * 金币/材料物品的校验与扣除、产出装备的发放均由脚本侧完成（与炼金系统一致的约定），
 * 本门面只负责品级校验与锻造师经验增加。装备的随机属性区间(神铸石/混沌石加成等)
 * 也由脚本侧根据配方的 *_min/*_max 字段自行计算，本门面只转发原始数值。
 * </p>
 * <p>
 * 配方数据为静态配置，参照 {@link ScrollDecomposeManager} 的约定加静态缓存，
 * 避免列表/详情查询每次都直连数据库；{@link #craft} 内部仍由
 * {@link ForgeRecipeService#craft} 实时查库校验，不受缓存影响。
 * 后台修改配方后需调用 {@link #reload()} 刷新缓存（供脚本/Controller调用）。
 * </p>
 */
public class ForgeRecipeManager {

    private static final Logger log = LoggerFactory.getLogger(ForgeRecipeManager.class);

    /** 配方ID → 配方实体（仅缓存已启用的配方） */
    private static final Map<Long, ForgeRecipeDO> recipeCache = new ConcurrentHashMap<>();
    /** 已启用配方列表缓存（按品级、排序号升序，与 {@link #recipeCache} 同步刷新） */
    private static volatile List<ForgeRecipeDO> enabledRecipeListCache = null;

    private ForgeRecipeManager() {}

    private static ForgeRecipeService getService() {
        var context = ServerManager.getApplicationContext();
        if (context == null) {
            throw new IllegalStateException("Spring 上下文不可用");
        }
        return context.getBean(ForgeRecipeService.class);
    }

    /**
     * 手动强制刷新配方缓存（从数据库重新加载）。
     * 脚本中可调用: {@code ForgeRecipeManager.reload()}
     */
    public static synchronized void reload() {
        try {
            List<ForgeRecipeDO> recipes = getService().listEnabledRecipes();
            Map<Long, ForgeRecipeDO> newCache = new ConcurrentHashMap<>();
            for (ForgeRecipeDO recipe : recipes) {
                newCache.put(recipe.getId(), recipe);
            }
            recipeCache.clear();
            recipeCache.putAll(newCache);
            enabledRecipeListCache = recipes;
            log.info("ForgeRecipeManager 缓存已刷新：共 {} 条已启用配方", recipes.size());
        } catch (Exception e) {
            log.error("刷新打造配方缓存失败", e);
        }
    }

    /** 获取缓存中的配方列表，缓存为空时触发一次加载 */
    private static List<ForgeRecipeDO> getCachedList() {
        List<ForgeRecipeDO> cached = enabledRecipeListCache;
        if (cached == null) {
            reload();
            cached = enabledRecipeListCache;
        }
        return cached != null ? cached : new ArrayList<>();
    }

    private static Map<String, Object> toMap(ForgeRecipeDO recipe) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", recipe.getId());
        map.put("name", recipe.getName());
        map.put("tierRequired", recipe.getTierRequired());
        map.put("resultItemId", recipe.getResultItemId());
        map.put("expGain", recipe.getExpGain());
        map.put("mesoCost", recipe.getMesoCost());
        map.put("staminaCost", recipe.getStaminaCost());
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
        map.put("material6ItemId", recipe.getMaterial6ItemId());
        map.put("material6Count", recipe.getMaterial6Count());
        map.put("material7ItemId", recipe.getMaterial7ItemId());
        map.put("material7Count", recipe.getMaterial7Count());
        map.put("material8ItemId", recipe.getMaterial8ItemId());
        map.put("material8Count", recipe.getMaterial8Count());
        map.put("strMin", recipe.getStrMin());
        map.put("strMax", recipe.getStrMax());
        map.put("dexMin", recipe.getDexMin());
        map.put("dexMax", recipe.getDexMax());
        map.put("intMin", recipe.getIntMin());
        map.put("intMax", recipe.getIntMax());
        map.put("lukMin", recipe.getLukMin());
        map.put("lukMax", recipe.getLukMax());
        map.put("watkMin", recipe.getWatkMin());
        map.put("watkMax", recipe.getWatkMax());
        map.put("matkMin", recipe.getMatkMin());
        map.put("matkMax", recipe.getMatkMax());
        map.put("pddMin", recipe.getPddMin());
        map.put("pddMax", recipe.getPddMax());
        map.put("mddMin", recipe.getMddMin());
        map.put("mddMax", recipe.getMddMax());
        map.put("hpMin", recipe.getHpMin());
        map.put("hpMax", recipe.getHpMax());
        map.put("mpMin", recipe.getMpMin());
        map.put("mpMax", recipe.getMpMax());
        return map;
    }

    /**
     * 查询所有已启用配方（按品级、排序号升序）。
     */
    public static List<Map<String, Object>> listEnabledRecipes() {
        try {
            List<Map<String, Object>> list = new ArrayList<>();
            for (ForgeRecipeDO recipe : getCachedList()) {
                list.add(toMap(recipe));
            }
            return list;
        } catch (Exception e) {
            log.error("查询打造配方失败", e);
            return new ArrayList<>();
        }
    }

    /**
     * 查询单个配方详情（命中缓存），不存在返回 null。
     */
    public static Map<String, Object> getRecipe(Long recipeId) {
        try {
            if (enabledRecipeListCache == null) reload();
            ForgeRecipeDO recipe = recipeCache.get(recipeId);
            return recipe != null ? toMap(recipe) : null;
        } catch (Exception e) {
            log.error("查询打造配方失败", e);
            return null;
        }
    }

    /**
     * 打造：校验品级要求、扣除体力、增加锻造师经验。
     * 金币/材料的校验与扣除、产出装备的发放需脚本在调用前后自行处理。
     *
     * @return {success, message, exp(打造后的累计经验)}
     */
    public static Map<String, Object> craft(Integer characterId, Integer accountId, Long recipeId) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            long newExp = getService().craft(characterId, accountId, recipeId);
            result.put("success", true);
            result.put("message", "打造成功");
            result.put("exp", newExp);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
