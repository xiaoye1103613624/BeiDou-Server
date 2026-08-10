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
 * 成长勋章进度（按角色）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_medal_growth")
public class MedalGrowthDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Integer characterId;

    /** 当前幻化外观，0 = 默认 1142747 */
    private Integer illusionMedalId;

    private String regionFlags;
    private String eliteFlags;
    private String expedFlags;
    private String poolJson;

    private Integer statStr;
    private Integer statDex;
    private Integer statInt;
    private Integer statLuk;
    private Integer statWatk;
    private Integer statMatk;

    private Date updateTime;
}
