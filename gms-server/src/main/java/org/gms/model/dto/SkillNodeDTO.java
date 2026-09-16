package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillNodeDTO {
    private Integer skillId;
    private Integer jobId;
    private String name;
    private Integer maxLevel;
    private Boolean invisible;
    private Integer masterLevel;
    private String iconUrl;
    /** 前置技能 skillId → 需求等级 */
    private Map<Integer, Integer> req;
}
