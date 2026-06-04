package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuildMemberRtnDTO {
    private Integer charId;
    private String name;
    private Integer level;
    private Integer jobId;
    private String jobName;
    private Integer guildRank;
    private String rankTitle;
    private Boolean online;
    private Integer allianceRank;
}
