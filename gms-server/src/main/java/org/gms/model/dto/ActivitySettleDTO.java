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
public class ActivitySettleDTO {
    private Long sessionId;
    private String code;
    private Integer worldId;
    private Integer channelId;
    /** 手工成绩；空则自动采集 */
    private List<ActivityParticipantResultDTO> results;
}
