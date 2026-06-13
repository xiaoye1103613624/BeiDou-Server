package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 每日副本配置 DTO（含里程碑奖励列表）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DailyDungeonSaveDTO {

    /** 配置ID（更新时必填） */
    private Long id;

    /** 副本键值（脚本唯一标识） */
    private String dungeonKey;

    /** 副本显示名称 */
    private String dungeonName;

    /** 副本地图ID */
    private Integer mapId;

    /** 地图名称（WZ自动解析，展示用） */
    private String mapName;

    /** 每日需完成次数 */
    private Integer completeCount;

    /** 物品名称（服务端解析，前端展示用） */
    private String itemName;

    /** 扫荡券道具ID（0=不可扫荡） */
    private Integer sweepItemId;

    /** 单次扫荡消耗道具数量 */
    private Integer sweepItemCost;

    /** 每日扫荡上限（0=不可扫荡） */
    private Integer maxSweep;

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

        /** 物品名称（服务端解析，展示用） */
        private String itemName;

        /** 发放数量 */
        private Integer quantity;

        /** 排序 */
        private Integer sortOrder;
    }

    /**
     * 每日奖励 DTO（所有副本完成后可领取）
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class DailyRewardDTO {
        /** 主键ID */
        private Long id;
        /** 奖励道具ID（0=金币） */
        private Integer itemId;
        /** 物品名称（服务端解析，展示用） */
        private String itemName;
        /** 奖励数量 */
        private Integer quantity;
        /** 奖励描述 */
        private String rewardDesc;
        /** 排序 */
        private Integer sortOrder;
    }

    /**
     * VIP物品配置 DTO
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class VipConfigDTO {
        /** 主键ID */
        private Long id;
        /** VIP物品ID */
        private Integer itemId;
        /** 物品名称（服务端解析，展示用） */
        private String itemName;
        /** VIP功能描述 */
        private String description;
        /** 是否启用（0=禁用 1=启用） */
        private Integer enabled;
        /** 排序 */
        private Integer sortOrder;
    }
}
