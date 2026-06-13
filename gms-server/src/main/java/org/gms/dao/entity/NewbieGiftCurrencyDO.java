package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 新手礼包货币奖励实体（金币/点卷/抵用券）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_newbie_gift_currency")
public class NewbieGiftCurrencyDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 关联 xy_newbie_gift_config.id */
    private Long giftId;

    /** 货币类型（meso/cash/credit） */
    private String currencyType;

    /** 数量 */
    private Integer amount;

    private Date createTime;
}
