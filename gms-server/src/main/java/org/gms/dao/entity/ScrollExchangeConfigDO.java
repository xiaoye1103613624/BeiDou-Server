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
 * 卷轴兑换配置实体（碎片→卷轴，配置每个卷轴的碎片价格）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_scroll_exchange_config")
public class ScrollExchangeConfigDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 卷轴物品ID */
    private Integer scrollId;

    /** 卷轴名称（可为空，WZ自动识别） */
    private String scrollName;

    /** 兑换所需碎片数量 */
    private Integer cost;

    /** 是否启用(0=禁用 1=启用) */
    private Integer enabled;

    /** 排序号（升序，数字越小越靠前，默认200） */
    private Integer sortOrder;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;
}
