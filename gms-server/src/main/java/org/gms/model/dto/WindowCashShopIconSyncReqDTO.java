package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WindowCashShopIconSyncReqDTO {
    /** fillEmpty | force */
    private String mode;
    /** Optional explicit item ids */
    private List<Integer> itemIds;
    /** Optional: only items linked to this category */
    private Integer categoryId;
}
