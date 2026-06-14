package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 师徒出师奖励配置 DTO
 * 用于前后端数据传输，映射 xy_mentor_graduation_reward 表及其关联道具
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MentorGraduationRewardDTO {

    /** 主键ID（更新时必填） */
    private Long id;

    /** 奖励类型（0=师父奖励 1=徒弟奖励） */
    private Integer rewardType;

    /** 金币奖励数量 */
    private Integer meso;

    /** 点卷奖励（NX_CREDIT=1） */
    private Integer nxCredit;

    /** 抵用券奖励（MAPLE_POINT=2） */
    private Integer maplePoint;

    /** 信用券奖励（NX_PREPAID=4） */
    private Integer nxPrepaid;

    /** 是否启用（0=禁用 1=启用） */
    private Integer enabled;

    /** 关联的道具奖励列表 */
    private List<ItemDTO> items;

    /**
     * 出师奖励道具 DTO
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
        private Integer quantity;
    }
}
