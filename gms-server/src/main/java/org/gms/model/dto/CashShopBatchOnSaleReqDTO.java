package org.gms.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.gms.dao.entity.ModifiedCashItemDO;

/**
 * 现金商城批量上下架请求参数
 * 批量修改商城商品的上架状态
 */
@Getter
@Setter
public class CashShopBatchOnSaleReqDTO {
    /** 修改后的商品数据数组 */
    private ModifiedCashItemDO[] data;
    /** 修改类型 */
    private String type;
    /** 修改值（是否上架） */
    private Integer value;
}