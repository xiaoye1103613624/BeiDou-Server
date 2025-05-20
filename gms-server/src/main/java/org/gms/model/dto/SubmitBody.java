package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 请求体 封装类
 * @author XiaoYe
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubmitBody<T> {
    /**
     * 请求ID
     */
    private String requestId;
    /**
     * 请求数据
     */
    private T data;
}
