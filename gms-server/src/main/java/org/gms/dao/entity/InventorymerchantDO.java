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
 * 【实体】InventorymerchantDO（class），包 `org.gms.dao.entity`。
 *
 * 对应数据库表 inventorymerchant，存储商人物品栏数据。
 *
 * @author 萧曵
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("inventorymerchant")
public class InventorymerchantDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long inventorymerchantid;

    private Long inventoryitemid;

    private Integer characterid;

    private Integer bundles;

}
