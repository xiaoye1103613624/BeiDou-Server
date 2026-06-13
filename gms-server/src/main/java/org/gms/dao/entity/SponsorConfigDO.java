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
 * 赞助奖励配置实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_sponsor_config")
public class SponsorConfigDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 配置名称（如"初级赞助""高级赞助"，用于游戏内展示） */
    private String name;

    /** 赞助金额阈值（累计达到此金额后可领取） */
    private Integer amount;

    /** 奖励JSON：[{type:"item"|"nx"|"meso", id:物品ID, qty:数量}] */
    private String rewardsJson;

    /** 是否启用（0=禁用 1=启用） */
    private Integer enabled;

    /** 备注说明 */
    private String comment;

    /** 创建时间 */
    private Date createTime;
}
