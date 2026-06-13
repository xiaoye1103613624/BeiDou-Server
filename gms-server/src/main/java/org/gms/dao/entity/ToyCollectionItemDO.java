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
 * 玩具收集物品配置实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_toy_collection_item")
public class ToyCollectionItemDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 所属分类ID（FK → xy_toy_collection_category.id） */
    private Long categoryId;

    /** 收集物品ID */
    private Integer itemId;

    /** 需要收集的数量 */
    private Integer requiredQuantity;

    /** 奖励物品ID（0=无奖励） */
    private Integer rewardItemId;

    /** 奖励物品数量 */
    private Integer rewardQuantity;

    /** 排序序号（升序） */
    private Integer sortOrder;

    /** 是否启用（0=禁用 1=启用） */
    private Integer enabled;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;
}
