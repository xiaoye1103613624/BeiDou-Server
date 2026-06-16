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

/**
 * 独立掉落怪物配置实体类
 * 映射 xy_independent_drop_config 表
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_independent_drop_config")
public class IndependentDropConfigDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /** BOSS怪物ID */
    private Integer mobId;

    /** 怪物名称（备注） */
    private String mobName;

    /** 是否启用（0=禁用 1=启用） */
    private Integer enabled;
}
