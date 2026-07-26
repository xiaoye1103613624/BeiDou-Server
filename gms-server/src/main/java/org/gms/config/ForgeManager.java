package org.gms.config;

import org.gms.dao.entity.ForgeDO;
import org.gms.manager.ServerManager;
import org.gms.service.ForgeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 锻造师副职业静态门面，供 GraalVM JS 脚本通过 {@code Java.type()} 调用。
 */
public class ForgeManager {

    private static final Logger log = LoggerFactory.getLogger(ForgeManager.class);

    private ForgeManager() {}

    private static ForgeService getService() {
        var context = ServerManager.getApplicationContext();
        if (context == null) {
            throw new IllegalStateException("Spring 上下文不可用");
        }
        return context.getBean(ForgeService.class);
    }

    /**
     * 查询锻造师品级总数（入门/普通/职业/大师/宗师 共5级）。
     */
    public static int getTierCount() {
        return ForgeService.TIERS.length;
    }

    /**
     * 查询指定品级名称。
     */
    public static String getTierName(int tierIndex) {
        return ForgeService.TIERS[tierIndex].name();
    }

    /**
     * 查询角色当前锻造师信息。
     *
     * @return {success, message, exp(累计经验), tierIndex(当前品级下标), tierName(当前品级名称),
     *          progress(当前品级内进度), tierSize(当前品级所需经验跨度，宗师为-1表示无上限), isMax(是否已是最高等级)}
     */
    public static Map<String, Object> getInfo(Integer characterId) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            ForgeDO forge = getService().getOrCreate(characterId);
            long exp = forge.getExp();
            int tierIndex = getService().getTierIndex(exp);
            ForgeService.Tier tier = ForgeService.TIERS[tierIndex];
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
}
