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
 * 仓库物品存储实体（实际存放的物品记录）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_warehouse_items")
public class WarehouseItemDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 账号ID */
    private Integer accountId;

    /** 存入角色ID */
    private Integer characterId;

    /** 物品ID */
    private Integer itemId;

    /** 物品栏类型(1=装备 2=消耗 3=设置 4=其他 5=现金) */
    private Integer inventoryType;

    /** 存放数量 */
    private Integer quantity;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;
}
