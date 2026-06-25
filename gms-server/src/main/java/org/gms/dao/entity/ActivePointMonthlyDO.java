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
 * 每日活跃度积分-月度累计与阶梯领取记录实体
 * <p>
 * 每次玩家领取一个每日积分阶梯奖励时，把该阶梯的点数阈值(1/5/10/20)叠加进 totalPoints。
 * 懒重置以 yearMonth(yyyy-MM) 是否为当前月份判断，跨月后清零重新开始累计。
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_active_point_monthly")
public class ActivePointMonthlyDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 角色ID */
    private Integer characterId;

    /** 所属年月，格式yyyy-MM，用于跨月懒重置 */
    private String yearMonth;

    /** 本月累计活跃度积分 */
    private Integer totalPoints;

    /** 月度积分阶梯1(200点)是否已领取：0=未领取 1=已领取 */
    private Integer tier1Claimed;

    /** 月度积分阶梯2(300点)是否已领取：0=未领取 1=已领取 */
    private Integer tier2Claimed;

    /** 月度积分阶梯3(500点)是否已领取：0=未领取 1=已领取 */
    private Integer tier3Claimed;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;
}
