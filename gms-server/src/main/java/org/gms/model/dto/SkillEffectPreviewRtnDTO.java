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
public class SkillEffectPreviewRtnDTO {
    private Integer skillId;
    private Boolean hasEffect;
    /** 实际选用的缓存层名。 */
    private String layer;
    private List<SkillEffectFrameDTO> frames;
    private Integer dumped;
    private Integer failed;
    private String message;
}
