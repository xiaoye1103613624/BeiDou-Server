package org.gms.util;

import lombok.extern.slf4j.Slf4j;
import org.gms.manager.ServerManager;
import org.gms.model.pojo.RateLimitContext;
import org.gms.property.ServiceProperty;
import org.gms.service.AccountService;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 限流工具类（单例模式）
 * 基于IP地址进行请求频率限制，防止接口被恶意调用
 */
@Slf4j
public class RateLimitUtil {
    /** 单例实例 */
    private static RateLimitUtil instance;
    /** 限流配置属性 */
    private final ServiceProperty.RateLimitProperty rateLimitProperty;
    /** 限流上下文映射，key为IP地址 */
    private final Map<String, RateLimitContext> contextMap;

    /**
     * 私有构造器，从Spring容器中获取限流配置并初始化上下文映射
     */
    private RateLimitUtil() {
        rateLimitProperty = ServerManager.getApplicationContext().getBean(ServiceProperty.class).getRateLimit();
        contextMap = new HashMap<>();
    }

    /**
     * 获取单例实例
     *
     * @return RateLimitUtil实例
     */
    public static RateLimitUtil getInstance() {
        if (instance == null) {
            instance = new RateLimitUtil();
        }
        return instance;
    }

    /**
     * 检查IP是否超过限流阈值
     * 在配置的时间窗口内，请求次数超过限制则返回false，若开启自动封禁还会自动封禁IP
     *
     * @param ip 客户端IP地址
     * @return true表示允许请求，false表示被限流
     */
    public boolean check(String ip) {
        // 限流功能关闭时直接放行
        if (!rateLimitProperty.isEnabled()) {
            return true;
        }
        try {
            RateLimitContext rateLimitContext = contextMap.get(ip);
            if (rateLimitContext == null) {
                // 首次请求，初始化计数器并设置过期时间窗口
                rateLimitContext = new RateLimitContext();
                rateLimitContext.setCurr(new AtomicInteger(1));
                rateLimitContext.setExpire(System.currentTimeMillis() + rateLimitProperty.getDuration());
                contextMap.put(ip, rateLimitContext);
                return true;
            }
            if (rateLimitContext.getExpire() < System.currentTimeMillis()) {
                // 时间窗口已过期，重置计数器并开启新窗口
                rateLimitContext.setExpire(System.currentTimeMillis() + rateLimitProperty.getDuration());
                rateLimitContext.getCurr().set(1);
                contextMap.put(ip, rateLimitContext);
                return true;
            }
            // 时间窗口内请求次数超过阈值，触发限流
            if (rateLimitContext.getCurr().incrementAndGet() > rateLimitProperty.getLimit()) {
                // 开启自动封禁时对IP进行封禁处理
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
        // 异常时默认拦截，保证系统安全
        return false;
    }
}