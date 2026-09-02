package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 炼金师品级配置DTO（管理后台增删改查用）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlchemyTierDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    /** 副职业类型：1=炼金 2=炼药 3=锻造 */
    private Integer type;

    /** 品级名称（如：入门、普通、职业、大师、宗师） */
    private String name;

    /** 达到该品级所需的最低累计经验（经验阈值） */
    private Long expStart;

    /** 是否为最高品级：0=否 1=是 */
    private Integer isMax;

    /** 品级显示顺序，越小品级越低 */
    private Integer sortOrder;

    /** 是否启用：0=禁用 1=启用 */
    private Integer enabled;
}