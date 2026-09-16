package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillJobStageDTO {
    /** 0 新手 / 1 一转 / 2 二转 / 3 三转 / 4 四转 */
    private Integer branch;
    private Integer jobId;
    private String nameKey;
    /** 已解析的职业展示名（服务端 i18n） */
    private String name;
}
