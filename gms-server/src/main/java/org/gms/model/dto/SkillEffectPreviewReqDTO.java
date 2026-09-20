package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillEffectPreviewReqDTO {
    private Integer skillId;
    /** true 时强制重抽各帧 PNG。 */
    private Boolean refresh;
}
