package org.gms.model.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 物品详情查询请求参数
 * 根据物品ID和类型查询物品的详细信息
 */
@Setter
@Getter
public class ItemDetailReqDTO {
    /** 物品ID */
    private Integer itemId;
    /** 物品类型（cash/consume/eqp/etc/ins/pet） */
    private String type;
}
