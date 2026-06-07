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
 * 装备强化配置实体（主表）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_equip_enhance_config")
public class EquipEnhanceConfigDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 可强化的装备物品ID */
    private Integer itemId;

    /** 装备名称 */
    private String itemName;

    /** 每角色仅限强化一件（0=不限 1=限制） */
    private Integer uniquePerChar;

    /** 最大强化等级 */
    private Integer maxEnhance;

    /** 是否启用（0=禁用 1=启用） */
    private Integer enabled;

    private Date createTime;

    private Date updateTime;
}
