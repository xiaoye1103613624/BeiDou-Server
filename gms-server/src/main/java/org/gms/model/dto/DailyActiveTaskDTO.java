package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 每日活跃任务配置DTO（管理后台增删改查用）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyActiveTaskDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long id;

    /** 任务标识(唯一)，供代码/脚本按key调用，如 zhuangbei_zhajuan */
    private String taskKey;

    /** 任务展示名称，如 装备砸卷 */
    private String taskName;

    /** 完成该任务所需的进度次数 */
    private Integer targetCount;

    /** 达成target_count后可领取的金币奖励 */
    private Long rewardMeso;

    /** 达成后可领取的奖励物品ID，0表示无物品奖励 */
    private Integer rewardItemId;

    /** 奖励物品数量 */
    private Integer rewardItemCount;

    /** 任务专用扩展配置(纯文本)，如野外精英任务在此存逗号分隔的怪物ID列表 */
    private String extraConfig;

    /** 菜单显示顺序，越小越靠前 */
    private Integer sortOrder;

    /** 是否启用：0=禁用 1=启用 */
    private Integer enabled;
}
