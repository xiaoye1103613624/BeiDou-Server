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
 * 【实体】TrocklocationsDO（class），包 `org.gms.dao.entity`。
 *
 * 对应数据库表 trocklocations，存储瞬移岩石位置数据。
 *
 * @author 萧曵
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("trocklocations")
public class TrocklocationsDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Integer trockid;

    private Integer characterid;

    private Integer mapid;

    private Integer vip;

}
