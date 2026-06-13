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
 * 新手礼包物品奖励实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_newbie_gift_item")
public class NewbieGiftItemDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 关联 xy_newbie_gift_config.id */
    private Long giftId;

    /** 奖励物品ID */
    private Integer itemId;

    /** 奖励数量 */
    private Integer quantity;

    private Date createTime;
}
