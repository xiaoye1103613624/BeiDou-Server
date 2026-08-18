package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DropSearchRtnDTO {
    private Long id;
    private Integer dropperId;
    private String dropperName;
    /** 已持久化的怪物图标地址 */
    private String mobIconUrl;
    private Integer continent;
    private Integer itemId;
    private String itemName;
    /** 已持久化的物品图标地址 */
    private String itemIconUrl;
    private Integer minimumQuantity;
    private Integer maximumQuantity;
    private Integer questId;
    private String questName;
    private Integer chance;
    private String comments;
    /** 1启用 0停用（全局掉落） */
    private Integer enabled;
}
