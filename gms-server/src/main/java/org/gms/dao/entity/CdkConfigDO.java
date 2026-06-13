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
 * CDK兑换码配置实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_cdk_config")
public class CdkConfigDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /** CDK兑换码（唯一） */
    private String code;

    /** 批次号（批量生成时共用） */
    private String batchNo;

    /** CDK类型（1=普通 2=批量生成） */
    private Integer type;

    /** 点券数量 */
    private Integer nxCredit;

    /** 抵用券数量 */
    private Integer nxPrepaid;

    /** 金币数量 */
    private Integer meso;

    /** 赞助金额（预留） */
    private Integer sponsor;

    /** 最大使用次数 */
    private Integer maxUseCount;

    /** 已使用次数 */
    private Integer usedCount;

    /** 过期时间（NULL=永不过期） */
    private Date expireTime;

    /** 是否启用（0=禁用 1=启用） */
    private Integer enabled;

    /** 备注说明 */
    private String comment;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;
}
