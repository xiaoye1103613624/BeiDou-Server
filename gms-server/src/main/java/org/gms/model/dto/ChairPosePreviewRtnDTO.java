package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChairPosePreviewRtnDTO {
    /** CLIENT_PNG | ICON_FALLBACK | NONE */
    private String mode;
    private String imageUrl;
    private String iconUrl;
    private Integer originX;
    private Integer originY;
    private Integer canvasWidth;
    private Integer canvasHeight;
    private String message;
}
