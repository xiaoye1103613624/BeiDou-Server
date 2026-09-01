package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("activity_schedule")
public class ActivityScheduleDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    private String activityCode;

    private Integer worldId;

    private Integer channelId;

    private String scheduleType;

    private Date startAt;

    private Time cronTime;

    private String daysOfWeek;

    private Integer maxPlayers;

    private Integer notifyMinutes;

    private Integer notifyIntervalSec;

    private Integer prewarpMinutes;

    private Integer enabled;

    private Date nextRunAt;

    private Date createdAt;

    private Date updatedAt;
}
