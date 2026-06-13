package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * CDK批量生成请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CdkBatchGenReqDTO {

    /** 生成数量 */
    private Integer count;

    /** 兑换码长度（6-16位） */
    private Integer length;

    /** 兑换码前缀（可选，便于分类识别） */
    private String prefix;

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

    /** 过期时间（字符串格式，null=永不过期） */
    private String expireTime;

    /** 是否启用（0=禁用 1=启用） */
    private Integer enabled;

    /** 备注说明 */
    private String comment;

    /** 道具奖励列表（所有生成的CDK共用） */
    private List<CdkConfigDTO.CdkItemDTO> items;
}
