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
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("activity_session")
public class ActivitySessionDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Long scheduleId;

    private String activityCode;

    private Integer worldId;

    private Integer channelId;

    private String status;

    private Integer maxPlayers;

    private Date plannedStartAt;

    private Date openedAt;

    private Date startedAt;

    private Date endedAt;

    private String extraInfo;

    private Date createdAt;
}
