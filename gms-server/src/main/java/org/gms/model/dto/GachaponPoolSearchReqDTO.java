package org.gms.model.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;


/**
 * 【类型】GachaponPoolSearchReqDTO（class），包 `org.gms.model.dto`。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class GachaponPoolSearchReqDTO extends BasePageDTO {
    private Integer gachaponId;
}
