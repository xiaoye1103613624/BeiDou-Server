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
 * 玩家赞助记录实体（角色级隔离）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_sponsor_record")
public class SponsorRecordDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 角色ID */
    private Integer playerId;

    /** 角色名称 */
    private String playerName;

    /** 账号ID */
    private Integer accountId;

    /** 账号名称 */
    private String accountName;

    /** 累计赞助金额 */
    private Integer totalSponsor;

    /** 首次赞助时间（创建时间） */
    private Date createTime;

    /** 最后更新时间 */
    private Date updateTime;
}
