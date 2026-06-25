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
 * 每日活跃-角色每日任务进度实体
 * <p>
 * 采用"懒重置"方案(与 bosslog/onetimelog 表一致的约定)：每次读写时比对 logDate 是否还是今天，
 * 不是则视为0并刷新日期，不需要额外的每日0点清空定时任务。
 * </p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_daily_active_progress")
public class DailyActiveProgressDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    /** 角色ID */
    private Integer characterId;

    /** 任务标识，对应 xy_daily_active_task.task_key */
    private String taskKey;

    /** 当日累计进度次数(跨天后懒重置为0) */
    private Integer progress;

    /** 本条进度对应的自然日，用于懒重置判断 */
    private LocalDate logDate;

    /** 当日该任务奖励是否已领取：0=未领取 1=已领取(随logDate懒重置) */
    private Integer claimed;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;
}
