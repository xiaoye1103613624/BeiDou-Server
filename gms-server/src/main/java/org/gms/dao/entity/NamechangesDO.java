package org.gms.dao.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;

/**
 * 【实体】NamechangesDO（class），包 `org.gms.dao.entity`。
 *
 * 对应数据库表 namechanges，存储改名记录数据。
 *
 * @author 萧曵
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("namechanges")
public class NamechangesDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Integer id;

    private Integer characterid;

    @Column("old")
    private String older;

    @Column("new")
    private String newer;

    @Column("requestTime")
    private Timestamp requestTime;

    @Column("completionTime")
    private Timestamp completionTime;

}
