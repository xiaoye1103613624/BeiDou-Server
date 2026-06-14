package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 每日Boss配置 DTO（含里程碑奖励列表）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyBossSaveDTO {

    /** 配置ID（更新时必填） */
    private Long id;

    /** Boss键值（脚本唯一标识） */
    private String bossKey;

    /** Boss显示名称 */
    private String bossName;

    /** Boss怪物ID */
    private Integer bossMobId;

    /** 排序顺序（升序） */
    private Integer sortOrder;

    /** 是否启用（0=禁用 1=启用） */
    private Integer enabled;

    /** 里程碑奖励列表 */
    private List<RewardDTO> rewards;

    /**
     * 里程碑奖励 DTO
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class RewardDTO {
        /** 主键ID（更新时必填） */
        private Long id;

        /** 需完成次数 */
        private Integer completeCount;

        /** 里程碑描述 */
        private String rewardDesc;

        /** 奖励道具ID */
        private Integer itemId;

        /** 发放数量 */
        private Integer quantity;

        /** 排序 */
        private Integer sortOrder;
    }
}
