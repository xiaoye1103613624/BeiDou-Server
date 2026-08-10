package org.gms.config;

import org.gms.dao.entity.AlchemyDO;
import org.gms.manager.ServerManager;
import org.gms.service.AlchemyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 炼金师副职业静态门面，供 GraalVM JS 脚本通过 {@code Java.type()} 调用。
 */
public class AlchemyManager {

    private static final Logger log = LoggerFactory.getLogger(AlchemyManager.class);

    private AlchemyManager() {}

    private static AlchemyService getService() {
        var context = ServerManager.getApplicationContext();
        if (context == null) {
            throw new IllegalStateException("Spring 上下文不可用");
        }
        return context.getBean(AlchemyService.class);
    }

    /**
     * 查询炼金师品级总数（品级数据由 xy_alchemy_tier 配置，默认入门/普通/职业/大师/宗师 共5级）。
     */
    public static int getTierCount() {
        return AlchemyTierManager.getTierCount();
    }

    /**
     * 查询指定品级名称。
     */
    public static String getTierName(int tierIndex) {
        return AlchemyTierManager.getTierName(tierIndex);
    }

    /**
     * 查询角色当前炼金师信息。
     *
     * @return {@code {success, message, exp(累计经验), tierIndex(当前品级下标), tierName(当前品级名称),
     *          progress(当前品级内进度), tierSize(当前品级所需经验跨度，最高品级为 -1 表示无上限), isMax(是否已是最高等级)}}
     */
    public static Map<String, Object> getInfo(Integer characterId) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            AlchemyDO alchemy = getService().getOrCreate(characterId);
            long exp = alchemy.getExp();
            int tierIndex = AlchemyTierManager.getTierIndex(exp);
            result.put("success", true);
            result.put("exp", exp);
            result.put("tierIndex", tierIndex);
            result.put("tierName", AlchemyTierManager.getTierName(tierIndex));
            result.put("progress", exp - AlchemyTierManager.getExpStart(tierIndex));
            result.put("tierSize", AlchemyTierManager.getExpSize(tierIndex));
            result.put("isMax", AlchemyTierManager.isMaxTier(tierIndex));
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
