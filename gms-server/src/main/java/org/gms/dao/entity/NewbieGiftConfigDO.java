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
 * 新手礼包配置实体（主表）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_newbie_gift_config")
public class NewbieGiftConfigDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 礼包名称 */
    private String giftName;

    /** 最低领取等级 */
    private Integer minLevel;

    /** 最高领取等级 */
    private Integer maxLevel;

    /** 是否启用（0=禁用 1=启用） */
    private Integer enabled;

    private Date createTime;

    private Date updateTime;
}
