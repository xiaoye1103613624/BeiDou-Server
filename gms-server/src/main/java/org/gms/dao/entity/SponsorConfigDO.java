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
 * 赞助档位配置
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_sponsor_config")
public class SponsorConfigDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Integer id;

    /** 档位名称 */
    private String name;

    /** 达标所需总赞助 */
    private Integer amount;

    /** 1启用 0停用 */
    private Integer enabled;

    /** 排序 */
    private Integer sortOrder;

    private Date createTime;
    private Date updateTime;
}
