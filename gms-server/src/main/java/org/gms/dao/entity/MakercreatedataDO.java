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
 * 【实体】MakercreatedataDO（class），包 `org.gms.dao.entity`。
 *
 * 对应数据库表 makercreatedata，存储制作系统创建数据。
 *
 * @author 萧曵
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("makercreatedata")
public class MakercreatedataDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Integer id;

    @Id
    private Integer itemid;

    private Integer reqLevel;

    private Integer reqMakerLevel;

    private Integer reqMeso;

    private Integer reqItem;

    private Integer reqEquip;

    private Integer catalyst;

    private Integer quantity;

    private Integer tuc;

}
