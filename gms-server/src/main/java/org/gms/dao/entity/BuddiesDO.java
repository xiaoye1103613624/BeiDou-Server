package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;

/**
 * 【实体】BuddiesDO（class），包 `org.gms.dao.entity`。
 *
 * 对应数据库表 buddies，存储好友关系数据。
 *
 * @author 萧曵
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("buddies")
public class BuddiesDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Integer id;

    private Integer characterid;

    private Integer buddyid;

    private Integer pending;

    private String group;

}
