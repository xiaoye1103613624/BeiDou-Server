package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * CDK兑换请求DTO（供NPC脚本和REST测试调用）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CdkRedeemReqDTO {

    /** CDK兑换码 */
    private String code;

    /** 兑换玩家ID（NPC脚本传入，用于关联角色） */
    private Integer playerId;
}
