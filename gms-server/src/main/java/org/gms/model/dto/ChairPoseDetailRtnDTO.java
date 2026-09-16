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
public class ChairPoseDetailRtnDTO {
    private Integer itemId;
    private String name;
    private String desc;
    private String iconUrl;
    private String sourceFile;
    private Boolean editorEnabled;
    private List<ChairPoseEffectDTO> effects;
}
