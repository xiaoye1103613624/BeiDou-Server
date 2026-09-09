package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DropMobRtnDTO {
    private Integer dropperId;
    private String dropperName;
    /** 该怪物在当前筛选条件下的掉落条目数 */
    private Long dropCount;
}
