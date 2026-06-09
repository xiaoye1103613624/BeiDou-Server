package org.gms.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 商店搜索请求参数
 * 支持按商店ID、NPC ID、NPC名称、物品ID、物品名称进行搜索
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ShopSearchReqDTO extends BasePageDTO {
    /** 商店ID */
    private Long shopId;
    /** NPC ID */
    private Integer npcId;
    /** NPC名称 */
    private String npcName;
    /** 物品ID */
    private Integer itemId;
    /** 物品名称 */
    private String itemName;
}