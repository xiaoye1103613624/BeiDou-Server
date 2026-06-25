package org.gms.config;

import org.gms.dao.entity.AlchemistDO;
import org.gms.manager.ServerManager;
import org.gms.service.AlchemistService;
import org.gms.service.StaminaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 炼药师（药剂师副职业）静态门面，供 GraalVM JS 脚本通过 {@code Java.type()} 调用。
 */
public class AlchemistManager {

    private static final Logger log = LoggerFactory.getLogger(AlchemistManager.class);

    private AlchemistManager() {}

    private static AlchemistService getService() {
        var context = ServerManager.getApplicationContext();
        if (context == null) {
            throw new IllegalStateException("Spring 上下文不可用");
        }
        return context.getBean(AlchemistService.class);
    }

    private static StaminaService getStaminaService() {
        var context = ServerManager.getApplicationContext();
        if (context == null) {
            throw new IllegalStateException("Spring 上下文不可用");
        }
        return context.getBean(StaminaService.class);
    }

    /**
     * 查询炼药师品级总数（入门/普通/职业/大师/宗师 共5级）。
     */
    public static int getTierCount() {
        return AlchemistService.TIERS.length;
    }

    /**
     * 查询指定品级名称。
     */
    public static String getTierName(int tierIndex) {
        return AlchemistService.TIERS[tierIndex].name();
    }

    /**
     * 查询角色当前炼药师信息。
     *
     * @return {success, message, exp(累计经验), tierIndex(当前品级下标), tierName(当前品级名称),
     *          progress(当前品级内进度), tierSize(当前品级所需经验跨度，宗师为-1表示无上限), isMax(是否已是最高等级)}
     */
    public static Map<String, Object> getInfo(Integer characterId) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            AlchemistDO alchemist = getService().getOrCreate(characterId);
            long exp = alchemist.getExp();
            int tierIndex = getService().getTierIndex(exp);
            AlchemistService.Tier tier = AlchemistService.TIERS[tierIndex];
            result.put("success", true);
            result.put("exp", exp);
            result.put("tierIndex", tierIndex);
            result.put("tierName", tier.name());
            result.put("progress", exp - tier.expStart());
            result.put("tierSize", tier.isMax() ? -1L : tier.expSize());
            result.put("isMax", tier.isMax());
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }

    /**
     * 炼制指定品级的药水：校验炼药师等级是否达到该品级、扣除对应体力，并增加固定经验。
     * 物品（材料消耗/产出药水）由脚本侧自行处理，本方法只处理体力与经验。
     *
     * @param tierIndex 品级下标（0=入门 1=普通 2=职业 3=大师 4=宗师）
     * @return {success, message, exp(炼制后的累计经验), staminaLeft(炼制后剩余体力), brewExp(本次获得经验)}
     */
    public static Map<String, Object> brew(Integer characterId, Integer accountId, int tierIndex) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            if (tierIndex < 0 || tierIndex >= AlchemistService.TIERS.length) {
                throw new IllegalArgumentException("药水品级不合法");
            }
            AlchemistDO alchemist = getService().getOrCreate(characterId);
            int currentTierIndex = getService().getTierIndex(alchemist.getExp());
            if (currentTierIndex < tierIndex) {
                throw new IllegalArgumentException("炼药师等级不足，无法炼制该品级的药水");
            }
            AlchemistService.Tier tier = AlchemistService.TIERS[tierIndex];
            getStaminaService().consumeStamina(accountId, tier.staminaCost());
            long newExp = getService().addExp(characterId, tier.brewExp());
            result.put("success", true);
            result.put("message", "炼制成功");
            result.put("exp", newExp);
            result.put("brewExp", tier.brewExp());
            result.put("staminaLeft", getStaminaService().getStamina(accountId));
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
