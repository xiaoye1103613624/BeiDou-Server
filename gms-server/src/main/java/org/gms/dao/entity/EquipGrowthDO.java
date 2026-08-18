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

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_equip_growth")
public class EquipGrowthDO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;
    private Integer itemId;
    private String itemName;
    private Integer enabled;
    private Integer maxLevel;
    private Integer sortOrder;
    private String remark;
    private String levelsJson;
    private String skillsJson;
    private String source;
    private Date createTime;
    private Date updateTime;
}
