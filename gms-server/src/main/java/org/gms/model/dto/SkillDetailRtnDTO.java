package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillDetailRtnDTO {
    private Integer skillId;
    private Integer jobId;
    private String name;
    private String desc;
    private Boolean invisible;
    private Integer masterLevel;
    private String iconUrl;
    private Map<Integer, Integer> req;
    private List<SkillLevelDTO> levels;
    /** String.wz 中 h1/h2… 等级说明 */
    private Map<String, String> hs;
    private Boolean editorEnabled;
}
