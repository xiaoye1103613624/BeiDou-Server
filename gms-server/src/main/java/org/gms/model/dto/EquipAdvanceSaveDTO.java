package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 装备进阶完整配置DTO（含路线、阶段和消耗道具）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EquipAdvanceSaveDTO {
    /** 路线ID（更新时必填） */
    private Long id;
    /** 职业群（warrior/archer/mage/thief/pirate） */
    private String jobGroup;
    /** 路线名称 */
    private String routeName;
    /** 是否启用（0=禁用 1=启用） */
    private Integer enabled;
    /** 各阶段配置 */
    private List<StageDTO> stages;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class StageDTO {
        private Long id;
        /** 阶段顺序（0=初始装备，1=一阶...） */
        private Integer stageOrder;
        /** 目标装备ID */
        private Integer targetItemId;
        /** 目标装备名称 */
        private String targetItemName;
        /** 金币消耗 */
        private Integer mesoCost;
        /** 点卷消耗 */
        private Integer cashCost;
        /** 抵用券消耗 */
        private Integer creditCost;
        private Integer strAdd;
        private Integer dexAdd;
        private Integer intAdd;
        private Integer lukAdd;
        private Integer hpAdd;
        private Integer mpAdd;
        private Integer watkAdd;
        private Integer matkAdd;
        private Integer wdefAdd;
        private Integer mdefAdd;
        private Integer accAdd;
        private Integer avoidAdd;
        private Integer speedAdd;
        private Integer jumpAdd;
        /** 消耗材料列表 */
        private List<CostDTO> costs;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CostDTO {
        private Long id;
        private Integer itemId;
        private Integer count;
    }
}
