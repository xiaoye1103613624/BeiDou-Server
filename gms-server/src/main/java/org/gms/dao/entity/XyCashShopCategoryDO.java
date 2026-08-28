package org.gms.dao.entity;

import com.mybatisflex.annotation.Column;
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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_cashshop_category")
public class XyCashShopCategoryDO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Integer id;
    private String name;
    private Integer parentId;
    private Integer sort;
    private Integer enabled;
    private String clickType;
    private String clickParam;
    private Integer gateItemId;
    private Integer isHot;
    private Integer legacyTab;
    private Integer legacyCategory;
    private String remark;
    /** insertBatch binds all columns; null would bypass MySQL DEFAULT. */
    @Column(onInsertValue = "now()", onUpdateValue = "now()")
    private Date updatedAt;
}
