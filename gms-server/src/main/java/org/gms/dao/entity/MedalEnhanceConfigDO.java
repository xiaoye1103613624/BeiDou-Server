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
@Table("xy_medal_enhance_config")
public class MedalEnhanceConfigDO implements Serializable {

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Integer maxEnhance;

    private Integer enabled;

    private Date createTime;

    private Date updateTime;
}
