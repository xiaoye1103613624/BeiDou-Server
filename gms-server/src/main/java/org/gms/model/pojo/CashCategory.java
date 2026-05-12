package org.gms.model.pojo;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.gms.model.dto.BasePageDTO;

/**
 * 【类型】CashCategory（class），包 `org.gms.model.pojo`。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class CashCategory extends BasePageDTO {
    private Integer id;
    private String name;
    private Integer subId;
    private String subName;
    private Boolean onSale;
    private Integer itemId;
}
