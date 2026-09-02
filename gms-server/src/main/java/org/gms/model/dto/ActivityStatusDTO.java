package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityStatusDTO {
    private String code;
    private String nameZh;
    private String nameEn;
    private String category;
    private Integer lobbyMapId;
    private Integer eventMapId;
    private Boolean teamEvent;
    private Boolean supportsMapStart;
    private Boolean enabled;
    private Integer defaultMaxPlayers;
    private Integer sortOrder;

    /** IDLE / NOTIFYING / REGISTERING / PREWARP / RUNNING */
    private String status;
    private Long sessionId;
    private Integer worldId;
    private Integer channelId;
    private Integer maxPlayers;
    private Integer registeredCount;
    private Integer lobbyCount;
    private Integer arenaCount;
    private String plannedStartAt;
    private String extraInfo;
}
