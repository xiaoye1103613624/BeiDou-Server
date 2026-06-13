package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 等级奖励完整配置 DTO（含道具列表）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LevelRewardSaveDTO {

    /** 配置ID（更新时必填） */
    private Long id;

    /** 要求等级 */
    private Integer level;

    /** 金币奖励 */
    private Integer meso;

    /** 点卷（NX_CREDIT=1） */
    private Integer nxCredit;

    /** 抵用券（MAPLE_POINT=2） */
    private Integer maplePoint;

    /** 信用券（NX_PREPAID=4） */
    private Integer nxPrepaid;

    /** 是否启用（0=禁用 1=启用） */
    private Integer enabled;

    /** 道具奖励列表 */
    private List<ItemDTO> items;

    /**
     * 道具奖励 DTO
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ItemDTO {
        /** 主键ID（更新时必填） */
        private Long id;

        /** 道具ID */
        private Integer itemId;

        /** 发放数量 */
        private Integer count;
    }
}
