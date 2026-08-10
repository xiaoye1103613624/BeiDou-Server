package org.gms.config;

import org.gms.client.Character;
import org.gms.client.inventory.Pet;
import org.gms.dao.entity.PetGrowthStageDO;
import org.gms.manager.ServerManager;
import org.gms.service.PetGrowthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 宠物成长配置缓存与召唤倍率查询。
 * <p>
 * 倍率只在 {@link Character#getExpRate()}/{@link Character#getDropRate()}/{@link Character#getMesoRate()}
 * 中按「当前召唤中的成长宠」乘算，不改写角色基础 rate 字段，避免换线/收回宠物时倍率漂移。
 * </p>
 */
public final class PetGrowthManager {

    private static final Logger log = LoggerFactory.getLogger(PetGrowthManager.class);

    /** petId → 已启用阶段 */
    private static final Map<Integer, PetGrowthStageDO> byPetId = new ConcurrentHashMap<>();
    private static volatile boolean loaded = false;

    private PetGrowthManager() {
    }

    private static PetGrowthService getService() {
        var context = ServerManager.getApplicationContext();
        if (context == null) {
            throw new IllegalStateException("Spring 上下文不可用");
        }
        return context.getBean(PetGrowthService.class);
    }

    public static synchronized void reload() {
        try {
            Map<Integer, PetGrowthStageDO> next = new ConcurrentHashMap<>();
            for (PetGrowthStageDO stage : getService().listEnabledStages()) {
                if (stage.getPetId() != null) {
                    next.put(stage.getPetId(), stage);
                }
            }
            byPetId.clear();
            byPetId.putAll(next);
            loaded = true;
            log.info("PetGrowthManager 缓存已刷新：{} 条已启用阶段", next.size());
        } catch (Exception e) {
            log.error("刷新宠物成长缓存失败", e);
        }
    }

    private static void ensureLoaded() {
        if (!loaded) {
            reload();
        }
    }

    public static boolean isSystemEnabled() {
        try {
            return GameConfig.getServerBoolean("use_pet_growth_system");
        } catch (Exception e) {
            return false;
        }
    }

    public static PetGrowthStageDO getStageByPetId(int petItemId) {
        if (!isSystemEnabled()) {
            return null;
        }
        ensureLoaded();
        return byPetId.get(petItemId);
    }

    public static float getSummonedExpBonus(Character chr) {
        return productBonus(chr, true, false, false);
    }

    public static float getSummonedDropBonus(Character chr) {
        return productBonus(chr, false, true, false);
    }

    public static float getSummonedMesoBonus(Character chr) {
        return productBonus(chr, false, false, true);
    }

    /** 初级宠物精华 */
    public static final int JUNIOR_ESSENCE = 4310337;
    /** 高级宠物精华 */
    public static final int SENIOR_ESSENCE = 4310338;

    /**
     * 脚本入口：用宠物精华喂养指定召唤槽位的成长宠（消耗其他栏精华）。
     *
     * @return 空字符串表示成功；否则为失败原因
     */
    public static String feedEssence(Character chr, int foodItemId, int petSlot) {
        try {
            return getService().feedWithEssence(chr, foodItemId, petSlot);
        } catch (Exception e) {
            log.error("feedEssence 失败", e);
            return "喂养失败，请稍后再试";
        }
    }

    public static int getGrowthExp(long petUniqueId) {
        try {
            return getService().getGrowthExp(petUniqueId);
        } catch (Exception e) {
            return 0;
        }
    }

    private static float productBonus(Character chr, boolean exp, boolean drop, boolean meso) {
        if (chr == null || !isSystemEnabled()) {
            return 1f;
        }
        ensureLoaded();
        float mul = 1f;
        Pet[] pets = chr.getPets();
        if (pets == null) {
            return 1f;
        }
        for (Pet pet : pets) {
            if (pet == null) {
                continue;
            }
            PetGrowthStageDO stage = byPetId.get(pet.getItemId());
            if (stage == null) {
                continue;
            }
            Double rate = exp ? stage.getExpRate() : (drop ? stage.getDropRate() : stage.getMesoRate());
            if (rate != null && rate > 0 && Math.abs(rate - 1.0) > 1e-9) {
                mul *= rate.floatValue();
            }
        }
        return mul;
    }
}
