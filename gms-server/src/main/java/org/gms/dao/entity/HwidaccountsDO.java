package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;

/**
 * 【实体】HwidaccountsDO（class），包 `org.gms.dao.entity`。
 *
 * 对应数据库表 hwidaccounts，存储硬件ID与账号关联数据。
 *
 * @author 萧曵
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("hwidaccounts")
public class HwidaccountsDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Integer accountid;

    @Id
    private String hwid;

    private Integer relevance;

    private Timestamp expiresat;

}
