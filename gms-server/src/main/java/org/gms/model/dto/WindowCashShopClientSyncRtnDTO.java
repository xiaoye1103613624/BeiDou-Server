package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WindowCashShopClientSyncRtnDTO {
    private String clientDataPath;
    private int categoriesCreated;
    private int categoriesUpdated;
    private int itemsUpserted;
    private int linksUpserted;
    private int iconsFilled;
    private int scanned;
    private int skipped;
    /** Empty obsolete / client-sync SHOW_ITEMS categories removed. */
    private int categoriesPruned;
    /** 误挂关联已改到正确 kCats（含 170xxxx 从帽子挪到武器）。 */
    private int linksMigrated;
    private boolean catalogReloaded;
    private String catalogSource;
    private int catalogSize;
    /** Wall-clock ms for the whole sync. */
    private long durationMs;
    /** Extra hint when scanned/upserted is 0 (Chinese). */
    private String emptyReason;
    private String message;
}
