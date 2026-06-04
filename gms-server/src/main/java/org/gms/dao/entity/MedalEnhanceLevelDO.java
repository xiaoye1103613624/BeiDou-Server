package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_medal_enhance_level")
public class MedalEnhanceLevelDO implements Serializable {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Long configId;

    private Integer enhanceLevel;

    private Integer successRate;

    private Integer destroyOnFail;

    private Integer mesoCost;

    private Integer strAdd;

    private Integer dexAdd;

    private Integer intAdd;

    private Integer lukAdd;

    private Integer hpAdd;

    private Integer mpAdd;

    private Integer watkAdd;

    private Integer matkAdd;

    private Integer wdefAdd;

    private Integer mdefAdd;

    private Integer accAdd;

    private Integer avoidAdd;

    private Integer speedAdd;

    private Integer jumpAdd;

    private Date createTime;

    private Date updateTime;
}
