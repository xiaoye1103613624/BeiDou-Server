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
public class IconCacheReqDTO {
    private String category;
    private Integer id;
    /** When set, batch-cache these refs (category+id). */
    private List<IconRef> items;
    /** Re-download from CDN even if local file exists. */
    private Boolean force;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IconRef {
        private String category;
        private Integer id;
    }
}
