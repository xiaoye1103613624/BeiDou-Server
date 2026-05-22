package org.gms.model.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * NPC商店查询请求DTO
 * <p>用于查询NPC商店列表的筛选条件</p>
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
