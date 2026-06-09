package org.gms.model.pojo;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.gms.model.dto.BasePageDTO;

/**
 * 现金商城分类查询条件
 * 继承BasePageDTO支持分页查询
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CashCategory extends BasePageDTO {
    /** 分类ID */
    private Integer id;
    /** 分类名称 */
    private String name;
    /** 子分类ID */
    private Integer subId;
    /** 子分类名称 */
    private String subName;
    /** 是否上架 */
    private Boolean onSale;
    /** 物品ID */
    private Integer itemId;
}