package org.gms.model.dto;

import com.mybatisflex.annotation.Column;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.gms.dao.entity.InventoryequipmentDO;


/**
 * 【类型】InventoryequipmentReqDTO（class），包 `org.gms.model.dto`。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class InventoryequipmentReqDTO  extends BasePageDTO{

    private Long inventoryequipmentid;

    private Long inventoryitemid;



}