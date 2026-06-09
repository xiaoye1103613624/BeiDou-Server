package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 掉落搜索请求参数
 * 支持按掉落者、物品、任务和大洲进行搜索
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class DropSearchReqDTO extends BasePageDTO {
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
    /** 任务ID */
    private Integer questId;
}