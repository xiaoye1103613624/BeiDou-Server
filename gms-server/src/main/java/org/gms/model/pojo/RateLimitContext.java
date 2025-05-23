package org.gms.model.pojo;

import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 限流上下文
 */
@Data
public class RateLimitContext {
    private AtomicInteger curr;
    private Long expire;
}
