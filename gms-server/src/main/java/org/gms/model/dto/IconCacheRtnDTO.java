package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IconCacheRtnDTO {
    private String category;
    private Integer id;
    private boolean cached;
    private String url;
    /** local | cdn | none */
    private String source;
    private String message;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IconRef {
        private String category;
        private Integer id;
    }
}
