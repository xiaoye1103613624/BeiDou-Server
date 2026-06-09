package org.gms.model.dto;

import com.mybatisflex.annotation.Column;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.gms.dao.entity.InventoryequipmentDO;


/**
 * 装备查询请求参数
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class InventoryequipmentReqDTO extends BasePageDTO {
    /** 装备记录ID */
    private Long inventoryequipmentid;
    /** 物品记录ID */
    private Long inventoryitemid;
}