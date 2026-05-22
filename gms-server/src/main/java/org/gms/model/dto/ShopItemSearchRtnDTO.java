package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商店物品查询返回DTO
 * <p>包含NPC商店中单个商品的详细信息</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopItemSearchRtnDTO {
    /** 商品记录ID */
    private Long id;
    /** 商店ID */
    private Long shopId;
    /** 物品ID */
    private Integer itemId;
    /** 价格 */
    private Integer price;
    /** 价格波动值 */
    private Integer pitch;
    /** 商品在商店中的位置 */
    private Integer position;
    /** 物品名称 */
    private String itemName;
    /** 物品描述 */
    private String itemDesc;
}
