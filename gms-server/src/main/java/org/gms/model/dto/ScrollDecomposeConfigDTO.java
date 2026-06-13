package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 卷轴分解配置 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ScrollDecomposeConfigDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 配置ID（更新时必填） */
    private Long id;

    /** 卷轴物品ID */
    private Integer scrollId;

    /** 卷轴名称（可为空，WZ自动识别） */
    private String scrollName;

    /** 是否启用(0=禁用 1=启用) */
    private Integer enabled;

    /** 排序号（升序，数字越小越靠前，默认200） */
    private Integer sortOrder;
}
