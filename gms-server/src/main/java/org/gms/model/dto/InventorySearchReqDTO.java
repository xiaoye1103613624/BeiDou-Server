package org.gms.model.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * 背包搜索请求参数
 * 支持按背包类型、角色ID/名称和在线状态进行搜索
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
    /** 是否在线 */
    private boolean onlineStatus;
}