package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 【类型】SubmitBody（class），包 `org.gms.model.dto`。
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubmitBody<T> {
    private String requestId;
    private T data;
}
