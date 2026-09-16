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
public class SkillJobLineDTO {
    private String lineage;
    private String lineId;
    private String nameKey;
    /** 已解析的展示名（服务端 i18n） */
    private String name;
    private List<SkillJobStageDTO> stages;
}
