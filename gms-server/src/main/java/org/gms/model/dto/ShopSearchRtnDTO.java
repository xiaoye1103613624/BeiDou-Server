package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * NPC商店查询返回DTO
 * <p>包含NPC商店的基本信息摘要</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShopSearchRtnDTO {
    /** 商店ID */
    private Long shopId;
    /** NPC ID */
    private Integer npcId;
    /** NPC名称 */
    private String npcName;
}
