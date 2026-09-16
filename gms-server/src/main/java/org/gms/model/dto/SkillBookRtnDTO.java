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
public class SkillBookRtnDTO {
    private Integer jobId;
    private List<SkillNodeDTO> skills;
    private List<SkillEdgeDTO> edges;
}
