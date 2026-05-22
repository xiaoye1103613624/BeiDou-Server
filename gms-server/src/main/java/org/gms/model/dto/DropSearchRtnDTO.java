package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 怪物掉落物查询返回DTO
 * <p>包含怪物掉落物品的完整信息</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DropSearchRtnDTO {
    /** 掉落记录ID */
    private Long id;
    /** 掉落怪物ID */
    private Integer dropperId;
    /** 掉落怪物名称 */
    private String dropperName;
    /** 所属大陆 */
    private Integer continent;
    /** 掉落物品ID */
    private Integer itemId;
    /** 掉落物品名称 */
    private String itemName;
    /** 最小掉落数量 */
    private Integer minimumQuantity;
    /** 最大掉落数量 */
    private Integer maximumQuantity;
    /** 关联任务ID */
    private Integer questId;
    /** 关联任务名称 */
    private String questName;
    /** 掉落概率 */
    private Integer chance;
    /** 备注 */
    private String comments;
}
