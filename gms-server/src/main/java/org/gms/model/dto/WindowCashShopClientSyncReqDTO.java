package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WindowCashShopClientSyncReqDTO {
    /** Default true: fill icon_url only when local/legacy PNG already exists (no CDN in bulk sync) */
    private Boolean fillIcons;
    /** Default price for newly created items */
    private Integer defaultPrice;
    /** Only cash equips + cash inventory (5xxxxxx); default true */
    private Boolean cashOnly;
}
