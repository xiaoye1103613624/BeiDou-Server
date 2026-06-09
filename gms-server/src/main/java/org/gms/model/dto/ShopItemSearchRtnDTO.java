package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 商店物品搜索结果返回参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopItemSearchRtnDTO {
    /** 记录ID */
    private Long id;
    /** 商店ID */
    private Long shopId;
    /** 物品ID */
    private Integer itemId;
    /** 价格 */
    private Integer price;
    /** 折扣 */
    private Integer pitch;
    /** 位置 */
    private Integer position;
    /** 物品名称 */
    private String itemName;
    /** 物品描述 */
    private String itemDesc;
}