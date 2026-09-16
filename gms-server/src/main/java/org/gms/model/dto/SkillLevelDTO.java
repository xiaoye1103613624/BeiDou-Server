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
public class SkillLevelDTO {
    private Integer level;
    /** level 节点下 int/string 属性名 → 值 */
    private Map<String, String> attrs;
}
