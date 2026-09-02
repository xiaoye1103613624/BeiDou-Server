package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityActionDTO {
    private String code;
    private Integer worldId;
    private Integer channelId;
    private Integer maxPlayers;
    /** 手动开赛时可选：计划开始时间（ISO 或 yyyy-MM-dd HH:mm:ss），空则立即进入报名 */
    private String plannedStartAt;
    /** 启用/禁用活动目录项 */
    private Boolean enabled;
}
