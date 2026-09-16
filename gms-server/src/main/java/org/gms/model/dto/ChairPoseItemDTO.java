package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChairPoseItemDTO {
    private Integer itemId;
    private String name;
    private Boolean hasEffect;
    private Boolean hasEffect2;
    private String iconUrl;
    private String sourceFile;
}
