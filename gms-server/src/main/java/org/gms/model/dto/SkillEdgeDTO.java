package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillEdgeDTO {
    private Integer fromSkillId;
    private Integer toSkillId;
    private Integer reqLevel;
    /** 前置不在当前 job 文件内 */
    private Boolean external;
}
