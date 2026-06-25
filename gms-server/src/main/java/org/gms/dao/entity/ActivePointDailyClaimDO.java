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
import java.time.LocalDate;
import java.util.Date;

/**
 * 每日活跃度积分-每日阶梯领取记录实体
 * <p>
 * 积分本身=当日 xy_daily_active_progress 按角色+当日求和得出，不在本表重复存储。
 * 本表只记录每日4个积分阶梯(1/5/10/20点)的领取状态，懒重置约定与 DailyActiveProgressDO 一致。
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_active_point_daily_claim")
public class ActivePointDailyClaimDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 角色ID */
    private Integer characterId;

    /** 记录日期，用于跨天懒重置 */
    private LocalDate logDate;

    /** 每日积分阶梯1(1点)是否已领取：0=未领取 1=已领取 */
    private Integer tier1Claimed;

    /** 每日积分阶梯2(5点)是否已领取：0=未领取 1=已领取 */
    private Integer tier2Claimed;

    /** 每日积分阶梯3(10点)是否已领取：0=未领取 1=已领取 */
    private Integer tier3Claimed;

    /** 每日积分阶梯4(20点)是否已领取：0=未领取 1=已领取 */
    private Integer tier4Claimed;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;
}
