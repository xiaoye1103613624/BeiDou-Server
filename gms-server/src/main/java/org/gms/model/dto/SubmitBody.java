package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 统一请求体
 * 封装API请求，包含请求ID和请求数据
 *
 * @param <T> 请求数据类型
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubmitBody<T> {
    /** 请求ID，用于追踪请求链路 */
    private String requestId;
    /** 请求数据 */
    private T data;
}