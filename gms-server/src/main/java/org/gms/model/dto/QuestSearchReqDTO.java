package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * 任务列表查询条件。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class QuestSearchReqDTO extends BasePageDTO {
    private Integer questId;
    private String name;
    private Integer npcId;
    private Integer itemId;
    private Boolean hasScript;
}
