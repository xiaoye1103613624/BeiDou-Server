package org.gms.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.gms.manager.ServerManager;
import org.gms.model.pojo.RateLimitContext;
import org.gms.property.ServiceProperty;
import org.gms.service.AccountService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 限流工具类
 *
 * @author 昨日小睡
 */
@Slf4j
public class RateLimitUtil {
    /**
     * 限流工具类实例
     */
    @Getter
    private static RateLimitUtil instance = new RateLimitUtil();
    /**
     * 限流配置
     */
    private final ServiceProperty.RateLimitProperty rateLimitProperty;
    /**
     * ip限流信息映射
     * key: ip, value: RateLimitContext
     */
    private final Map<String, RateLimitContext> contextMap;

    private RateLimitUtil() {
        rateLimitProperty = ServerManager.getApplicationContext().getBean(ServiceProperty.class).getRateLimit();
        contextMap = new HashMap<>();
    }

    /**
     * IP 限流检测
     *
     * @param ip 需要检查的IP地址
     * @return 如果未超过速率限制则返回true，否则返回false
     */
    public boolean check(String ip) {
        if (!rateLimitProperty.isEnabled()) {
            return true;
        }
        try {
            RateLimitContext rateLimitContext = contextMap.get(ip);
            if (rateLimitContext == null) {
                rateLimitContext = new RateLimitContext();
                rateLimitContext.setCurr(new AtomicInteger(1));
                rateLimitContext.setExpire(System.currentTimeMillis() + rateLimitProperty.getDuration());
                contextMap.put(ip, rateLimitContext);
                return true;
            }
            if (rateLimitContext.getExpire() < System.currentTimeMillis()) {
                rateLimitContext.setExpire(System.currentTimeMillis() + rateLimitProperty.getDuration());
                rateLimitContext.getCurr().set(1);
                contextMap.put(ip, rateLimitContext);
                return true;
            }
            if (rateLimitContext.getCurr().incrementAndGet() > rateLimitProperty.getLimit()) {
                if (rateLimitProperty.isAutoBan()) {
                    AccountService accountService = ServerManager.getApplicationContext().getBean(AccountService.class);
                    accountService.ban(ip, "Auto banned by rate limit", true);
                }
                return false;
            }
            return true;
        } catch (Exception e) {
            log.error("Rate limit check error", e);
        }
        return false;
    }
}
