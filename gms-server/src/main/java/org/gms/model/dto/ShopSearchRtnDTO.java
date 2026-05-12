package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 【类型】ShopSearchRtnDTO（class），包 `org.gms.model.dto`。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopSearchRtnDTO {
    private Long shopId;
    private Integer npcId;
    private String npcName;
}
