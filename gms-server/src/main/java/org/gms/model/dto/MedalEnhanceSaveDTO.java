package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 勋章强化完整配置DTO（含等级和消耗道具）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MedalEnhanceSaveDTO {
    /** 配置ID（更新时必填） */
    private Long id;
    /** 最大强化等级 */
    private Integer maxEnhance;
    /** 是否启用 */
    private Integer enabled;
    /** 各等级配置 */
    private List<LevelDTO> levels;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class LevelDTO {
        private Long id;
        private Integer enhanceLevel;
        private Integer successRate;
        private Integer destroyOnFail;
        private Integer mesoCost;
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
        /** 消耗道具列表 */
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
