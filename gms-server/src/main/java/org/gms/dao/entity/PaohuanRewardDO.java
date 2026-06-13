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

/**
 * 跑环里程碑奖励实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_paohuan_reward")
public class PaohuanRewardDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 完成第几环时触发奖励 */
    private Integer ringCount;

    /** 奖励描述 */
    private String rewardDesc;

    /** 奖励道具ID(0=金币) */
    private Integer itemId;

    /** 奖励数量 */
    private Integer quantity;

    /** 同环内排序 */
    private Integer sortOrder;
}
