package org.gms.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 【类型】ServiceProperty（class），包 `org.gms.property`。
 */
@ConfigurationProperties(prefix = "gms.service")
@Component
@Data
public class ServiceProperty {
    private String language;
    private String scriptOverridePath;
    private RateLimitProperty rateLimit;
    private String wanHost;
    private String lanHost;
    private String localhost;
    private int loginPort;

    @Data
    public static class RateLimitProperty {
        private boolean enabled;
        private int limit;
        private long duration;
        private boolean autoBan;
    }
}
