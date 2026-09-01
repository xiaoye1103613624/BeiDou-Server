package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 洗炼词条配置表实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_reforge_affix")
public class ReforgeAffixDO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private String code;          // 词条代码 HP/DEF/WAR/...
    private String nameZh;        // 中文名 血/防/战/...
    private Integer maxPrefix;    // 最高前缀等级 血防=1 其余=5
    private String baseJson;      // ①级基值JSON
    private Integer weight;       // 抽取权重
    private Integer enabled;      // 是否启用
    private Date createTime;
    private Date updateTime;
}
