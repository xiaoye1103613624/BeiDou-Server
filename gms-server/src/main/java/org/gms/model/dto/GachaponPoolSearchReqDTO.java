package org.gms.model.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;


/**
 * 转蛋池搜索请求参数
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class GachaponPoolSearchReqDTO extends BasePageDTO {
    /** 转蛋池ID */
    private Integer gachaponId;
}