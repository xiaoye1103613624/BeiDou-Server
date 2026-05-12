package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 【类型】ShopItemSearchRtnDTO（class），包 `org.gms.model.dto`。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopItemSearchRtnDTO {
    private Long id;
    private Long shopId;
    private Integer itemId;
    private Integer price;
    private Integer pitch;
    private Integer position;
    private String itemName;
    private String itemDesc;
}
