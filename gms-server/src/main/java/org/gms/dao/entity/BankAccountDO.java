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
 * 银行账户实体（角色隔离，每个角色独立开户）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_bank_account")
public class BankAccountDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 所属角色ID */
    private Integer characterId;

    /** 账户密码（BCrypt加密存储） */
    private String password;

    /** 账户余额（金币） */
    private Long balance;

    /** 开户时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;
}
