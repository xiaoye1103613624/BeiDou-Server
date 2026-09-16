package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestScriptReqDTO {
    private Integer questId;
    /** start | end */
    private String phase;
    private String content;
    /** true 写 scripts-zh-CN，否则 scripts */
    private Boolean localized;
}
