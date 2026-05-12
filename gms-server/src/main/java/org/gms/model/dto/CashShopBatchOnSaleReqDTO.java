package org.gms.model.dto;

import lombok.Getter;
import lombok.Setter;
import org.gms.dao.entity.ModifiedCashItemDO;

/**
 * 【类型】CashShopBatchOnSaleReqDTO（class），包 `org.gms.model.dto`。
 */
@Getter
@Setter
public class CashShopBatchOnSaleReqDTO {
    private ModifiedCashItemDO[] data;
    private String type;
    private Integer value;
}
