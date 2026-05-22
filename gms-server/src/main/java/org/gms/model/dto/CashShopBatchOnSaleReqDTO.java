package org.gms.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.gms.dao.entity.ModifiedCashItemDO;

/**
 * 现金商城批量上架请求DTO
 * <p>用于批量设置现金商城商品的上架/下架状态及属性修改</p>
 */
@Getter
@Setter
public class CashShopBatchOnSaleReqDTO {
    /** 待操作的现金商城商品数组 */
    private ModifiedCashItemDO[] data;
    /** 批量操作类型（如价格、上架、下架等） */
    private String type;
    /** 操作值（根据type不同代表不同含义） */
    private Integer value;
}
