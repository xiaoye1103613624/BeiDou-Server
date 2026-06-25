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
 * 角色级炼金师实体，经验只增不减，升级不重置，独立于炼药师经验池
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_character_alchemy")
public class AlchemyDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 所属角色ID */
    private Integer characterId;

    /** 累计经验值 */
    private Long exp;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;
}
