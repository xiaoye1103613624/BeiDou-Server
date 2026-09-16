package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 任务列表行。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestListRtnDTO {
    private Integer questId;
    private String name;
    private String parentName;
    private Integer startNpcId;
    private String startNpcName;
    private Integer endNpcId;
    private String endNpcName;
    private Integer minLevel;
    private Boolean hasStartScript;
    private Boolean hasEndScript;
    private Integer nextQuestId;
}
