package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GuildListRtnDTO {
    private Long guildid;
    private String name;
    private String leaderName;
    private Long leaderId;
    private Long gp;
    private Long capacity;
    private String notice;
    private Long allianceId;
    private String allianceName;
    private Integer memberCount;
    private Integer logo;
    private Integer logoColor;
    private Long logoBG;
    private Integer logoBGColor;
}
