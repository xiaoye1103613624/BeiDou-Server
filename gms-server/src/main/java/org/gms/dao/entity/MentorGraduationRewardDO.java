package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.io.Serial;

/**
 * 出师奖励配置实体类
 * <p>
 * 配置师徒出师时师父和徒弟分别可获得的奖励。
 * reward_type=0 为师父奖励，reward_type=1 为徒弟奖励。
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_mentor_graduation_reward")
public class MentorGraduationRewardDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 主键 */
    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 奖励类型（0=师父奖励 1=徒弟奖励） */
    private Integer rewardType;

    /** 金币奖励数量 */
    private Integer meso;

    /** 点卷奖励（NX_CREDIT=1） */
    private Integer nxCredit;

    /** 抵用券奖励（MAPLE_POINT=2） */
    private Integer maplePoint;

    /** 信用券奖励（NX_PREPAID=4） */
    private Integer nxPrepaid;

    /** 是否启用（0=禁用 1=启用） */
    private Integer enabled;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
