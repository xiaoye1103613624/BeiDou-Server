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
 * CDK兑换日志实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_cdk_log")
public class CdkLogDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 关联CDK配置ID（码不存在时为NULL） */
    private Long cdkId;

    /** CDK兑换码（冗余存储） */
    private String code;

    /** 兑换玩家名称 */
    private String playerName;

    /** 兑换玩家ID */
    private Integer playerId;

    /** 兑换账号名称 */
    private String accountName;

    /** 兑换账号ID */
    private Integer accountId;

    /** 兑换时客户端IP地址 */
    private String ip;

    /** 兑换结果（0=成功 1=码不存在 2=已过期 3=已达上限 4=已禁用 5=背包已满 6=系统错误） */
    private Integer result;

    /** 结果描述 */
    private String resultMsg;

    /** 兑换明细（JSON格式） */
    private String detail;

    /** 兑换时间 */
    private Date createTime;
}
