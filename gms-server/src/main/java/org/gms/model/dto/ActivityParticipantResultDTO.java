package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityParticipantResultDTO {
    private Integer characterId;
    private String characterName;
    private Integer teamId;
    private Integer rankNo;
    private Integer score;
    private Long finishTimeMs;
    private String outcome;
    private String tags;
}
