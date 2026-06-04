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
import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_boss_config")
public class BossConfigDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Integer mobId;
    private String bossName;
    private BigDecimal hpMultiplier;
    private BigDecimal expMultiplier;
    private BigDecimal damageMultiplier;
    private Integer enabled;
    private Integer level;
    private Integer hp;
    private Integer mp;
    private Integer exp;
    private Integer pdd;
    private Integer mdd;
    private Integer acc;
    private Integer eva;
    private Date createTime;
    private Date updateTime;
}
