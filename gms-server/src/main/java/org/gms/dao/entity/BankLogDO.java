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
 * 银行操作日志实体（存入/取出/转账记录）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_bank_log")
public class BankLogDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 银行账户ID（关联 xy_bank_account.id） */
    private Long bankId;

    /** 操作类型(1=存入 2=取出 3=转账) */
    private Integer type;

    /** 操作发起角色ID（转账时为转出方） */
    private Integer fromCharacterId;

    /** 对方角色ID（仅转账类型有值） */
    private Integer toCharacterId;

    /** 操作金额（不含手续费的本金） */
    private Long amount;

    /** 手续费（存入5%，转账10%） */
    private Long fee;

    /** 操作时间 */
    private Date createTime;
}
