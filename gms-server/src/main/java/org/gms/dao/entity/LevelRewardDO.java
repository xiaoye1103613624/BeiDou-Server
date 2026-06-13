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
 * 等级奖励配置实体（主表）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_level_reward")
public class LevelRewardDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 要求等级 */
    private Integer level;

    /** 金币奖励（默认为0） */
    private Integer meso;

    /** 点卷（NX_CREDIT=1 默认为0） */
    private Integer nxCredit;

    /** 抵用券（MAPLE_POINT=2 默认为0） */
    private Integer maplePoint;

    /** 信用券（NX_PREPAID=4 默认为0） */
    private Integer nxPrepaid;

    /** 是否启用（0=禁用 1=启用） */
    private Integer enabled;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;
}
