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
 * 宝石等级配置实体（1~16级，每级2个低级宝石合成1个高级宝石）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_gem_level")
public class GemLevelDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 宝石等级，1~16级 */
    private Integer gemLevel;

    /** 该等级宝石的物品ID */
    private Integer itemId;

    /** 该等级宝石名称（仅展示用） */
    private String itemName;

    /** 由该等级合成下一等级所需金币 */
    private Long synthesisMesoCost;

    /** 是否启用：0=禁用 1=启用 */
    private Integer enabled;

    private Date createTime;

    private Date updateTime;
}
