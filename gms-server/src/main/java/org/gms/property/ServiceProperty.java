package org.gms.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 服务配置属性
 * 绑定gms.service前缀的配置项，用于加载服务端动态配置参数
 */
@ConfigurationProperties(prefix = "gms.service")
@Component
@Data
public class ServiceProperty {
    /** 语言设置 */
    private String language;
    /** 数据目录（WZ/脚本等资源根目录），为空时自动检测 */
    private String dataHome;
    /** 接口限流配置 */
    private RateLimitProperty rateLimit;
    /** 外网地址 */
    private String wanHost;
    /** 内网地址 */
    private String lanHost;
    /** 本地地址 */
    private String localhost;
    /** 登录端口 */
    private int loginPort;

    /**
     * 接口限流配置属性
     */
    @Data
    public static class RateLimitProperty {
        /** 是否启用限流 */
        private boolean enabled;
        /** 时间窗口内最大请求次数 */
        private int limit;
        /** 时间窗口（毫秒） */
        private long duration;
        /** 是否自动封禁 */
        private boolean autoBan;
    }
}