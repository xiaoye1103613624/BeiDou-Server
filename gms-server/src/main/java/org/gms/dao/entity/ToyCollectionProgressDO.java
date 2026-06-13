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
 * 玩具收集进度实体（角色隔离）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_toy_collection_progress")
public class ToyCollectionProgressDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 角色ID */
    private Integer characterId;

    /** 关联收集物品配置ID（FK → xy_toy_collection_item.id） */
    private Long itemConfigId;

    /** 已提交数量 */
    private Integer submittedQuantity;

    /** 奖励是否已领取（0=未领 1=已领） */
    private Integer rewardClaimed;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;
}
