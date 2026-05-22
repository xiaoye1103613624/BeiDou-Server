package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用请求体封装
 * <p>包含请求ID和业务数据，用于前端提交操作的统一格式</p>
 *
 * @param <T> 请求数据类型
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubmitBody<T> {
    /** 请求唯一标识，用于响应关联 */
    private String requestId;
    /** 请求业务数据 */
    private T data;
}
