package org.gms.dao.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_cashshop_item")
public class XyCashShopItemDO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Integer itemId;
    private Integer price;
    private Integer count;
    private Integer period;
    private Integer gender;
    private String name;
    private String iconUrl;
    private Integer enabled;
    private String remark;
    /** insertBatch binds all columns; null would bypass MySQL DEFAULT. */
    @Column(onInsertValue = "now()", onUpdateValue = "now()")
    private Date updatedAt;
}
