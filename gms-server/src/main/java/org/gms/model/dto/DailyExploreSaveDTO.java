package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 每日探索保存DTO（含嵌套的地图池、随机奖励、完成奖励）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyExploreSaveDTO {

    /** 主键ID（更新时必填） */
    private Long id;

    /** 目标地图ID */
    private Integer mapId;

    /** 排序顺序 */
    private Integer sortOrder;

    /** 是否启用(0=禁用 1=启用) */
    private Integer enabled;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;

    // ==================== 嵌套DTO ====================

    /**
     * 每轮随机奖励DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RewardDTO {
        private Long id;
        /** 奖励道具ID(0=金币) */
        private Integer itemId;
        /** 物品名称（服务端解析，前端展示用） */
        private String itemName;
        /** 最小随机数量 */
        private Integer minQuantity;
        /** 最大随机数量 */
        private Integer maxQuantity;
        /** 选中权重 */
        private Integer weight;
        /** 排序顺序 */
        private Integer sortOrder;
        /** 是否启用 */
        private Integer enabled;
    }

    /**
     * 完成奖励DTO（里程碑式）
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FinalRewardDTO {
        private Long id;
        /** 完成第几次探索时触发 */
        private Integer exploreCount;
        /** 奖励描述 */
        private String rewardDesc;
        /** 奖励道具ID(0=金币) */
        private Integer itemId;
        /** 物品名称（服务端解析，前端展示用） */
        private String itemName;
        /** 奖励数量 */
        private Integer quantity;
        /** 排序顺序 */
        private Integer sortOrder;
    }
}
