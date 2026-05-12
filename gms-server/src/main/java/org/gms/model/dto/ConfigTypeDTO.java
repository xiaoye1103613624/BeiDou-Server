package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 【类型】ConfigTypeDTO（class），包 `org.gms.model.dto`。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ConfigTypeDTO {
    private List<String> types;
    private List<String> subTypes;
}
