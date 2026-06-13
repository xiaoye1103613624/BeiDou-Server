package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * 跑环完整配置 DTO（含物品池和里程碑奖励）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaohuanSaveDTO {

    /** 配置ID（更新时必填） */
    private Long id;

    /** 要求的物品ID */
    private Integer itemId;

    /** 物品名称（展示用，非持久化字段） */
    private String itemName;

    /** 要求的数量 */
    private Integer quantity;

    /** 物品掉落地图ID（0=未知，用于VIP传送） */
    private Integer dropMapId;

    /** 排序顺序 */
    private Integer sortOrder;

    /** 是否启用 */
    private Integer enabled;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;

    /** 里程碑奖励列表 */
    private List<RewardDTO> rewards;

    /**
     * 每环随机奖励 DTO
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class RingRewardDTO {
        /** 主键ID */
        private Long id;
        /** 奖励道具ID(0=金币) */
        private Integer itemId;
        /** 物品名称（展示用，非持久化字段） */
        private String itemName;
        /** 最小随机数量 */
        private Integer minQuantity;
        /** 最大随机数量 */
        private Integer maxQuantity;
        /** 权重 */
        private Integer weight;
        /** 排序顺序 */
        private Integer sortOrder;
        /** 是否启用 */
        private Integer enabled;
    }

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

        /** 完成第几环时触发 */
        private Integer ringCount;

        /** 奖励描述 */
        private String rewardDesc;

        /** 奖励道具ID(0=金币) */
        private Integer itemId;

        /** 物品名称（展示用，非持久化字段） */
        private String itemName;

        /** 奖励数量 */
        private Integer quantity;

        /** 同环内排序 */
        private Integer sortOrder;
    }
}
