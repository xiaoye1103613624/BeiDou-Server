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
public class AssetEnsureRtnDTO {
    private String category;
    private boolean force;
    private int requested;
    private int cached;
    private int skipped;
    private int failed;
    private long durationMs;
    private String root;
    private String message;
    /** Per-id detail (capped for large batches). */
    private List<IconCacheRtnDTO> details;
}
