package org.gms.model.pojo;

import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 【类型】RateLimitContext（class），包 `org.gms.model.pojo`。
 */
@Data
public class RateLimitContext {
    private AtomicInteger curr;
    private Long expire;
}
