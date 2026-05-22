package org.gms.model.dto;

import com.mybatisflex.annotation.Column;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.gms.dao.entity.InventoryequipmentDO;


/**
 * 装备表查询请求DTO
 * <p>用于查询装备详情表的筛选条件</p>
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class InventoryequipmentReqDTO  extends BasePageDTO{

    /** 装备记录ID */
    private Long inventoryequipmentid;

    /** 关联的物品记录ID */
    private Long inventoryitemid;



}