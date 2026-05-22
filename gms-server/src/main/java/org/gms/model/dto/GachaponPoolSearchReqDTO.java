package org.gms.model.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;


/**
 * 百宝箱奖池查询请求DTO
 * <p>用于查询指定百宝箱的奖池列表</p>
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class GachaponPoolSearchReqDTO extends BasePageDTO {
    /** 百宝箱NPC ID */
    private Integer gachaponId;
}
