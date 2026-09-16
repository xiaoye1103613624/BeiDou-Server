package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillLineageDTO {
    private String code;
    private String nameKey;
    /** 已解析的展示名（服务端 i18n） */
    private String name;
    private Boolean enabled;
    private String remark;
}
