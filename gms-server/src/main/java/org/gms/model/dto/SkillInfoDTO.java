package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 技能简要信息（后台查技能名/最大等级）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillInfoDTO {
    private int skillId;
    private String name;
    private int maxLevel;
    private boolean exists;
}
