package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * CDK兑换码配置DTO（用于Web管理端和JS脚本交互）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CdkConfigDTO {

    /** 主键（新增时为空） */
    private Long id;

    /** CDK兑换码 */
    private String code;

    /** 批次号 */
    private String batchNo;

    /** CDK类型（1=普通 2=批量生成） */
    private Integer type;

    /** 点券数量 */
    private Integer nxCredit;

    /** 抵用券数量 */
    private Integer nxPrepaid;

    /** 金币数量 */
    private Integer meso;

    /** 赞助金额（预留） */
    private Integer sponsor;

    /** 最大使用次数 */
    private Integer maxUseCount;

    /** 已使用次数（只读，由服务端维护） */
    private Integer usedCount;

    /** 过期时间（字符串格式，null=永不过期） */
    private String expireTime;

    /** 是否启用（0=禁用 1=启用） */
    private Integer enabled;

    /** 备注说明 */
    private String comment;

    /** 创建时间（只读） */
    private String createTime;

    /** 更新时间（只读） */
    private String updateTime;

    /** 道具奖励列表 */
    private List<CdkItemDTO> items;

    /**
     * CDK道具奖励DTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CdkItemDTO {

        /** 主键（新增时为空） */
        private Long id;

        /** 道具ID */
        private Integer itemId;

        /** 道具名称（服务端通过WZ数据填充，前端只读展示） */
        private String itemName;

        /** 发放数量 */
        private Integer quantity;
    }
}
