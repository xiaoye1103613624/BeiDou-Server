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
 * 每日副本里程碑奖励实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_daily_dungeon_reward")
public class DailyDungeonRewardDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 关联副本配置ID */
    private Long configId;

    /** 需完成次数 */
    private Integer completeCount;

    /** 里程碑描述 */
    private String rewardDesc;

    /** 奖励道具ID */
    private Integer itemId;

    /** 发放数量 */
    private Integer quantity;

    /** 同里程碑内排序 */
    private Integer sortOrder;
}
