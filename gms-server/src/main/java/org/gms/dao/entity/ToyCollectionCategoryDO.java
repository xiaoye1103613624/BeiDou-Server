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
 * 玩具收集分类配置实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_toy_collection_category")
public class ToyCollectionCategoryDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 分类名称 */
    private String name;

    /** 图标标识 */
    private String icon;

    /** 排序序号（升序） */
    private Integer sortOrder;

    /** 是否启用（0=禁用 1=启用） */
    private Integer enabled;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;
}
