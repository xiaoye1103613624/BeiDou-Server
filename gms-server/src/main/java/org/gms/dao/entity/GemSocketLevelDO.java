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
 * 宝石镶嵌配置实体（每等级宝石对应一组镶嵌消耗与属性加成，永久写入装备属性）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_gem_socket_level")
public class GemSocketLevelDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 对应宝石等级，关联 xy_gem_level.gem_level */
    private Integer gemLevel;

    /** 镶嵌所需属性水晶物品ID */
    private Integer crystalItemId;

    /** 镶嵌所需属性水晶数量 */
    private Integer crystalCount;

    /** 镶嵌所需金币 */
    private Long mesoCost;

    private Integer strAdd;
    private Integer dexAdd;
    private Integer intAdd;
    private Integer lukAdd;
    private Integer watkAdd;
    private Integer matkAdd;

    /** 是否启用：0=禁用 1=启用 */
    private Integer enabled;

    private Date createTime;

    private Date updateTime;
}
