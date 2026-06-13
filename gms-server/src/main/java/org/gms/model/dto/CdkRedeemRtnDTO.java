package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CDK兑换结果DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CdkRedeemRtnDTO {

    /** 是否兑换成功 */
    private Boolean success;

    /** 结果消息（成功时为奖励摘要，失败时为错误原因） */
    private String message;

    /** 兑换物品详情（成功时记录发放的物品列表，用于前端展示） */
    private String detailJson;
}
