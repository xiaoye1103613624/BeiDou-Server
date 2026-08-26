package org.gms.dao.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_lottery_item")
public class XyLotteryItemDO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;
    private Integer npcId;
    private Integer itemId;
    private Integer quantity;
    private Integer weight;
    private Integer announce;
    private Integer announceChannel;
    private Integer announceBanner;
    private String announceLabel;
    private Integer randomStats;
    private Integer untradeable;
    private Integer accountBound;
    private Integer uniqueEquip;
    private Integer enabled;
    private Integer fromComment;
    private Integer itemValid;
    /** 1特殊 2装备 3消耗 4其它 */
    private Integer itemType;
    private Integer sortOrder;

    @Column(ignore = true)
    private String itemName;
}
