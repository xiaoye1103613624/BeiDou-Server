package org.gms.config;

import org.gms.manager.ServerManager;
import org.gms.service.ChallengeFatigueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 挑战副本次数静态门面，供 GraalVM JS / UseItemHandler 调用。
 */
public class ChallengeFatigueManager {

    private static final Logger log = LoggerFactory.getLogger(ChallengeFatigueManager.class);

    private ChallengeFatigueManager() {}

    private static ChallengeFatigueService getService() {
        var context = ServerManager.getApplicationContext();
        if (context == null) {
            throw new IllegalStateException("Spring 上下文不可用");
        }
        return context.getBean(ChallengeFatigueService.class);
    }

    public static int typeNormal() {
        return ChallengeFatigueService.TYPE_NORMAL;
    }

    public static int typeAdvanced() {
        return ChallengeFatigueService.TYPE_ADVANCED;
    }

    public static int typeTeam() {
        return ChallengeFatigueService.TYPE_TEAM;
    }

    public static int getRemaining(Integer characterId, int challengeType) {
        try {
            return getService().getRemaining(characterId, challengeType);
        } catch (Exception e) {
            log.error("查询挑战次数失败", e);
            return 0;
        }
    }

    /**
     * @return {success, message, remaining, typeName}
     */
    public static Map<String, Object> getInfo(Integer characterId, int challengeType) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            int remaining = getService().getRemaining(characterId, challengeType);
            result.put("success", true);
            result.put("message", "ok");
            result.put("remaining", remaining);
            result.put("dailyBase", ChallengeFatigueService.DAILY_BASE);
            result.put("typeName", ChallengeFatigueService.typeName(challengeType));
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            result.put("remaining", 0);
        }
        return result;
    }

    /**
     * 进入挑战消耗 1 次并写日志。
     *
     * @return {success, message, remaining}
     */
    public static Map<String, Object> consumeEnter(Integer characterId, Integer accountId, int challengeType,
                                                   String bossName, Integer mapId, String mobIds) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            int remaining = getService().consumeEnter(characterId, accountId, challengeType, bossName, mapId, mobIds);
            result.put("success", true);
            result.put("message", "进入成功");
            result.put("remaining", remaining);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage() != null ? e.getMessage() : "进入失败");
            result.put("remaining", getRemaining(characterId, challengeType));
        }
        return result;
    }

    /**
     * 使用恢复剂 +1。
     *
     * @return {success, message, remaining, typeName}
     */
    public static Map<String, Object> restoreByItem(Integer characterId, Integer accountId, int itemId) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            Integer type = ChallengeFatigueService.typeByItemId(itemId);
            if (type == null) {
                result.put("success", false);
                result.put("message", "无效的恢复剂物品");
                return result;
            }
            int remaining = getService().restoreOne(characterId, accountId, type, itemId);
            result.put("success", true);
            result.put("message", ChallengeFatigueService.typeName(type) + "次数+1");
            result.put("remaining", remaining);
            result.put("typeName", ChallengeFatigueService.typeName(type));
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage() != null ? e.getMessage() : "恢复失败");
        }
        return result;
    }
}
