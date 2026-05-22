package org.gms.model.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * 背包物品查询请求DTO
 * <p>用于查询玩家背包物品的筛选条件</p>
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class InventorySearchReqDTO extends BasePageDTO {
    /** 背包类型 */
    private Byte inventoryType;
    /** 角色ID */
    private Integer characterId;
    /** 角色名称 */
    private String characterName;
    /** 是否仅查询在线玩家 */
    private boolean onlineStatus;
}
