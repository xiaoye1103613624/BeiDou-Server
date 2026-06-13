package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;

/**
 * 出师奖励道具实体类
 * <p>
 * 记录出师奖励配置所关联的具体道具及数量。
 * 通过 reward_id 关联到出师奖励主表。
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_mentor_graduation_reward_item")
public class MentorGraduationRewardItemDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 关联出师奖励ID（FK → mentor_graduation_reward.id） */
    private Long rewardId;

    /** 道具ID */
    private Integer itemId;

    /** 发放数量 */
    private Integer quantity;
}
