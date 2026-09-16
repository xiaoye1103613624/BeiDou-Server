package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IconResolveRtnDTO {
    private String category;
    private Integer id;
    /** Preferred display URL (local path or CDN). */
    private String url;
    private String cdnUrl;
    private boolean local;
    /** local | cdn | none */
    private String source;
}
