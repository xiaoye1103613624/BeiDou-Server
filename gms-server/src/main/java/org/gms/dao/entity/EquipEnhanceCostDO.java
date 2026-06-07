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
 * 装备强化消耗物品实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_equip_enhance_cost")
public class EquipEnhanceCostDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 关联 xy_equip_enhance_level.id */
    private Long levelId;

    /** 消耗道具ID */
    private Integer itemId;

    /** 消耗数量 */
    private Integer count;

    private Date createTime;

    private Date updateTime;
}
