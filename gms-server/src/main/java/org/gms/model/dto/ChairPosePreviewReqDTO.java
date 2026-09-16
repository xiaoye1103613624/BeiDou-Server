package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChairPosePreviewReqDTO {
    private Integer itemId;
    /** effect | effect2，默认 effect */
    private String layer;
}
