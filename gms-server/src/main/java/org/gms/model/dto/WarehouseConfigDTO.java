package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 仓库物品配置 DTO（允许存入仓库的物品白名单）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WarehouseConfigDTO {

    /** 配置ID（更新时必填） */
    private Long id;

    /** 物品ID */
    private Integer itemId;

    /** 物品名称（可为空，管理员手动输入便于识别） */
    private String itemName;

    /** 物品掉落地图ID（0=未知，用于脚本传送） */
    private Integer dropMapId;

    /** 物品栏类型(1=装备 2=消耗 3=设置 4=其他 5=现金) */
    private Integer inventoryType;

    /** 是否启用(0=禁用 1=启用) */
    private Integer enabled;

    /** 排序号（升序，数字越小越靠前，默认200） */
    private Integer sortOrder;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;
}
