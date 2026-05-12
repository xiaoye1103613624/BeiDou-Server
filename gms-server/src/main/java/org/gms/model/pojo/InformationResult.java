package org.gms.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 【类型】InformationResult（class），包 `org.gms.model.pojo`。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InformationResult {
    private String type;
    private Integer id;
    private String name;
    private String desc;
}
