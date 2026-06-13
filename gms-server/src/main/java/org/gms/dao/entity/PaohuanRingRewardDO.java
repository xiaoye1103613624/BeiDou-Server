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
 * 跑环每环随机奖励池实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_paohuan_ring_reward")
public class PaohuanRingRewardDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 奖励道具ID(0=金币) */
    private Integer itemId;

    /** 最小随机数量 */
    private Integer minQuantity;

    /** 最大随机数量 */
    private Integer maxQuantity;

    /** 权重 */
    private Integer weight;

    /** 排序顺序 */
    private Integer sortOrder;

    /** 是否启用 */
    private Integer enabled;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;
}
