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
 * 等级奖励道具实体（子表）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_level_reward_item")
public class LevelRewardItemDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 关联的奖励配置ID（FK → xy_level_reward.id） */
    private Long rewardId;

    /** 道具ID */
    private Integer itemId;

    /** 发放数量（默认为1） */
    private Integer quantity;
}
