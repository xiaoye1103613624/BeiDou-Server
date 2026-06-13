package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 仓库存取操作请求 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WarehouseOperateDTO {

    /** 账号ID */
    private Integer accountId;

    /** 角色ID */
    private Integer characterId;

    /** 物品ID */
    private Integer itemId;

    /** 物品栏类型(1=装备 2=消耗 3=设置 4=其他 5=现金) */
    private Integer inventoryType;

    /** 操作数量 */
    private Integer quantity;
}
