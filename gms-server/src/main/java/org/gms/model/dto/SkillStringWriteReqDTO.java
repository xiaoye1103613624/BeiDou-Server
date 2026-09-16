package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 写 String.wz/Skill.img 文案；null 字段表示不改。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillStringWriteReqDTO {
    private Integer skillId;
    private String name;
    private String desc;
    private Map<String, String> hs;
}
