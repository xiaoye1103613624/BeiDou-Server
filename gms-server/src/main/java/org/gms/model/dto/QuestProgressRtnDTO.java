package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestProgressRtnDTO {
    private Long questStatusId;
    private Integer characterId;
    private String characterName;
    private Integer questId;
    private String questName;
    private Integer status;
    private String statusLabel;
    private Integer time;
    private Integer forfeited;
    private Integer completed;
    private Boolean online;
}
