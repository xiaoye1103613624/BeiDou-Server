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
public class TamingMobPoseDetailRtnDTO {
    private Integer mobId;
    private String name;
    private String desc;
    private String iconUrl;
    private String sourceFile;
    private String defaultAction;
    private Boolean editorEnabled;
    private List<String> actions;
    private List<TamingMobPoseFrameDTO> frames;
}
