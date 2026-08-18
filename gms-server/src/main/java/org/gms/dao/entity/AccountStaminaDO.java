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
 * 账号级体力实体（同账号下所有角色共享同一份体力）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_account_stamina")
public class AccountStaminaDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 所属账号ID */
    private Integer accountId;

    /** 当前体力值，上限1000 */
    private Integer stamina;

    /** 上次每日体力发放日期 */
    private Date lastRefillDate;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;
}
