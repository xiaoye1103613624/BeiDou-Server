package org.gms.model.pojo;

import lombok.Data;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 限流上下文
 * 记录当前请求计数和过期时间，用于IP限流控制
 */
@Data
public class RateLimitContext {
    /** 当前请求计数（原子操作） */
    private AtomicInteger curr;
    /** 过期时间戳 */
    private Long expire;
}