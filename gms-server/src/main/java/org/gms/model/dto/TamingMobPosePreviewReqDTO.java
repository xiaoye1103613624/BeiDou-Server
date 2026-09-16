package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TamingMobPosePreviewReqDTO {
    private Integer mobId;
    private String action;
    private Integer frameIndex;
}
