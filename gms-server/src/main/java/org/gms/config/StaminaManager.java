package org.gms.config;

import org.gms.manager.ServerManager;
import org.gms.service.StaminaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 账号级体力静态门面，供 GraalVM JS 脚本通过 {@code Java.type()} 调用。
 */
public class StaminaManager {

    private static final Logger log = LoggerFactory.getLogger(StaminaManager.class);

    private StaminaManager() {}

    private static StaminaService getService() {
        var context = ServerManager.getApplicationContext();
        if (context == null) {
            throw new IllegalStateException("Spring 上下文不可用");
        }
        return context.getBean(StaminaService.class);
    }

    /**
     * 查询当前体力（含每日发放结算），异常时返回0。
     */
    public static int getStamina(Integer accountId) {
        try {
            return getService().getStamina(accountId);
        } catch (Exception e) {
            log.error("查询体力失败", e);
            return 0;
        }
    }

    /**
     * 增加体力（如使用体力药水），不超过上限1000。
     *
     * @return {success, message, stamina(增加后的体力值)}
     */
    public static Map<String, Object> addStamina(Integer accountId, int amount) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            int stamina = getService().addStamina(accountId, amount);
            result.put("success", true);
            result.put("message", "体力增加成功");
            result.put("stamina", stamina);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", e.getMessage());
        }
        return result;
    }
}
