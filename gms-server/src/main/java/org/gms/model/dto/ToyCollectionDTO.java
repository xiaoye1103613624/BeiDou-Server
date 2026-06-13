package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 玩具收集系统 DTO
 */
public class ToyCollectionDTO {

    /**
     * 分类 DTO（含物品列表）
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CategoryDTO {
        /** 主键ID */
        private Long id;
        /** 分类名称 */
        private String name;
        /** 图标标识 */
        private String icon;
        /** 排序序号 */
        private Integer sortOrder;
        /** 是否启用（0=禁用 1=启用） */
        private Integer enabled;
        /** 该分类下的收集物品列表 */
        private List<ItemDTO> items;
    }

    /**
     * 收集物品 DTO
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ItemDTO {
        /** 主键ID */
        private Long id;
        /** 所属分类ID */
        private Long categoryId;
        /** 收集物品ID */
        private Integer itemId;
        /** 物品名称（服务端解析，展示用） */
        private String itemName;
        /** 需要收集的数量 */
        private Integer requiredQuantity;
        /** 奖励物品ID（0=无奖励） */
        private Integer rewardItemId;
        /** 奖励物品名称（服务端解析，展示用） */
        private String rewardItemName;
        /** 奖励物品数量 */
        private Integer rewardQuantity;
        /** 排序序号 */
        private Integer sortOrder;
        /** 是否启用（0=禁用 1=启用） */
        private Integer enabled;
    }

    /**
     * 收集进度 DTO
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ProgressDTO {
        /** 主键ID */
        private Long id;
        /** 角色ID */
        private Integer characterId;
        /** 角色名称 */
        private String characterName;
        /** 关联物品配置ID */
        private Long itemConfigId;
        /** 收集物品ID */
        private Integer itemId;
        /** 需要收集的数量 */
        private Integer requiredQuantity;
        /** 已提交数量 */
        private Integer submittedQuantity;
        /** 奖励是否已领取 */
        private Integer rewardClaimed;
    }
}
