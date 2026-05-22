package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;

/**
 * 【实体】SpecialcashitemsDO（class），包 `org.gms.dao.entity`。
 *
 * 对应数据库表 specialcashitems，存储特殊商城物品数据。
 *
 * @author 萧曵
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("specialcashitems")
public class SpecialcashitemsDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    private Integer sn;

    /**
     * 1024 is add/remove
     */
    private Integer modifier;

    private Integer info;

}
