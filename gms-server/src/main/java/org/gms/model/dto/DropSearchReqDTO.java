package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 怪物掉落物查询请求DTO
 * <p>用于查询怪物掉落物品的筛选条件</p>
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class DropSearchReqDTO extends BasePageDTO {
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
    /** 关联任务ID */
    private Integer questId;
}
