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
 * 每日探索每轮随机奖励实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_daily_explore_reward")
public class DailyExploreRewardDO implements Serializable {

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

    /** 选中权重（越高越容易抽中，0=禁用） */
    private Integer weight;

    /** 排序顺序（升序） */
    private Integer sortOrder;

    /** 是否启用(0=禁用 1=启用) */
    private Integer enabled;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;
}
