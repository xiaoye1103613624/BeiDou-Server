package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 掉落搜索结果返回参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DropSearchRtnDTO {
    /** 记录ID */
    private Long id;
    /** 掉落者ID */
    private Integer dropperId;
    /** 掉落者名称 */
    private String dropperName;
    /** 大洲 */
    private Integer continent;
    /** 物品ID */
    private Integer itemId;
    /** 物品名称 */
    private String itemName;
    /** 最小数量 */
    private Integer minimumQuantity;
    /** 最大数量 */
    private Integer maximumQuantity;
    /** 任务ID */
    private Integer questId;
    /** 任务名称 */
    private String questName;
    /** 掉落概率 */
    private Integer chance;
    /** 备注 */
    private String comments;
}