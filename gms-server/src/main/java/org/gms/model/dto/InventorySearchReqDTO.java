package org.gms.model.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * 【类型】InventorySearchReqDTO（class），包 `org.gms.model.dto`。
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class InventorySearchReqDTO extends BasePageDTO {
    private Byte inventoryType;
    private Integer characterId;
    private String characterName;
    private boolean onlineStatus;
}
