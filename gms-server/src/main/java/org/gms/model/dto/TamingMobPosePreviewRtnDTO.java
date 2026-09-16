package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TamingMobPosePreviewRtnDTO {
    /** CLIENT_PNG | ICON_FALLBACK | NONE */
    private String mode;
    private String imageUrl;
    private String iconUrl;
    private String action;
    private Integer frameIndex;
    private Integer navelX;
    private Integer navelY;
    private Integer originX;
    private Integer originY;
    private Integer canvasWidth;
    private Integer canvasHeight;
    private String message;
}
