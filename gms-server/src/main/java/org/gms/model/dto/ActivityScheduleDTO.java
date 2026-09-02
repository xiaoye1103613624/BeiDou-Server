package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityScheduleDTO {
    private Long id;
    private String activityCode;
    private Integer worldId;
    private Integer channelId;
    /** ONCE / DAILY / WEEKLY */
    private String scheduleType;
    /** ONCE：yyyy-MM-dd HH:mm:ss */
    private String startAt;
    /** DAILY/WEEKLY：HH:mm:ss */
    private String cronTime;
    /** WEEKLY：1-7 逗号分隔 */
    private String daysOfWeek;
    private Integer maxPlayers;
    private Integer notifyMinutes;
    private Integer notifyIntervalSec;
    private Integer prewarpMinutes;
    private Boolean enabled;
    private String nextRunAt;
}
