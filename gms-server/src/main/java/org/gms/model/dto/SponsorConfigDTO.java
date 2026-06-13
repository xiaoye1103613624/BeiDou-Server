package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 赞助配置DTO（用于Web管理端）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SponsorConfigDTO {

    /** 主键（新增时为空） */
    private Long id;

    /** 配置名称（如"初级赞助""高级赞助"） */
    private String name;

    /** 赞助金额阈值 */
    private Integer amount;

    /** 奖励列表 */
    private List<RewardItem> rewards;

    /** 是否启用（0=禁用 1=启用） */
    private Integer enabled;

    /** 备注说明 */
    private String comment;

    /** 创建时间 */
    private String createTime;

    /**
     * 单项奖励
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RewardItem {
        /** 奖励类型：item / nx / meso */
        private String type;
        /** 物品ID（type=item时使用） */
        private Integer id;
        /** 数量 */
        private Integer qty;
    }
}
