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
@Table("activity_participant_result")
public class ActivityParticipantResultDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Long sessionId;

    private Integer characterId;

    private String characterName;

    private Integer teamId;

    private Integer rankNo;

    private Integer score;

    private Long finishTimeMs;

    private String outcome;

    private String tags;

    private Date createdAt;
}
