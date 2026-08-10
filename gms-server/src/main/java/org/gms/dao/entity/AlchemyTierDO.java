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
 * 炼金师品级配置实体（品级名称/经验阈值在管理后台可配置，取代硬编码 TIERS）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_alchemy_tier")
public class AlchemyTierDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 副职业类型：1=炼金 2=炼药 3=锻造 */
    private Integer type;

    /** 品级名称（如：入门、普通、职业、大师、宗师） */
    private String name;

    /** 达到该品级所需的最低累计经验（经验阈值） */
    private Long expStart;

    /** 是否为最高品级：0=否 1=是（最高品级无上限） */
    private Integer isMax;

    /** 品级显示顺序，越小品级越低 */
    private Integer sortOrder;

    /** 是否启用：0=禁用 1=启用 */
    private Integer enabled;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;
}
