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
public class AssetEnsureReqDTO {
    /** item | mob | npc | skill | map | quest */
    private String category;
    /** Explicit ids to ensure. */
    private List<Integer> ids;
    /** Inclusive range when ids empty. */
    private Integer idFrom;
    private Integer idTo;
    /**
     * When true and ids/range empty: pull ids from known DB tables for the category
     * (currently xy_cash_shop_item for item).
     */
    private Boolean fromCatalog;
    /** Re-download even if local file exists. */
    private Boolean force;
}
