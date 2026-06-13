package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 仓库物品 DTO（仓库中存放的物品信息）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WarehouseItemDTO {

    /** 仓库物品ID */
    private Long id;

    /** 账号ID */
    private Integer accountId;

    /** 存入角色ID */
    private Integer characterId;

    /** 物品ID */
    private Integer itemId;

    /** 物品名称（WZ解析） */
    private String itemName;

    /** 物品栏类型(1=装备 2=消耗 3=设置 4=其他 5=现金) */
    private Integer inventoryType;

    /** 存放数量 */
    private Integer quantity;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;
}
