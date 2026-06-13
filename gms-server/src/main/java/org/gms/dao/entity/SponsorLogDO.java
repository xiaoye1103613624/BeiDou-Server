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
 * 赞助日志实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_sponsor_log")
public class SponsorLogDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 角色ID */
    private Integer playerId;

    /** 角色名称 */
    private String playerName;

    /** 账号ID */
    private Integer accountId;

    /** 赞助类型（1=CDK兑换 2=管理员添加） */
    private Integer type;

    /** 变动金额 */
    private Integer amount;

    /** 变动详情 */
    private String detail;

    /** 操作时间 */
    private Date createTime;
}
