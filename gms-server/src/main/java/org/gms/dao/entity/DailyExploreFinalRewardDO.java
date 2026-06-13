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
 * 每日探索完成奖励实体（里程碑式，完成指定次数触发）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_daily_explore_final_reward")
public class DailyExploreFinalRewardDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 完成第几次探索时触发奖励 */
    private Integer exploreCount;

    /** 奖励描述文案 */
    private String rewardDesc;

    /** 奖励道具ID(0=金币) */
    private Integer itemId;

    /** 奖励数量 */
    private Integer quantity;

    /** 排序顺序（升序） */
    private Integer sortOrder;
}
