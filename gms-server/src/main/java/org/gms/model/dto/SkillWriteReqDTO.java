package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 写 Skill.wz 技能节点；null 字段表示不改。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillWriteReqDTO {
    private Integer skillId;
    private Integer masterLevel;
    private Boolean invisible;
    private Map<Integer, Integer> req;
    private List<SkillLevelDTO> levels;
}
