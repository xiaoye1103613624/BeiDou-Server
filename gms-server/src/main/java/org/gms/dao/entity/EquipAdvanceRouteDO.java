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
 * 装备进阶路线配置实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_equip_advance_route")
public class EquipAdvanceRouteDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 职业群（warrior/archer/mage/thief/pirate） */
    private String jobGroup;

    /** 路线名称 */
    private String routeName;

    /** 是否启用（0=禁用 1=启用） */
    private Integer enabled;

    private Date createTime;

    private Date updateTime;
}