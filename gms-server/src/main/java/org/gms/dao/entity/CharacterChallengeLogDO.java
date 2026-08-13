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

/**
 * 角色挑战副本操作日志
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_character_challenge_log")
public class CharacterChallengeLogDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Integer characterId;

    private Integer accountId;

    private Integer challengeType;

    /** ENTER / RESTORE */
    private String actionType;

    private String bossName;

    private Integer mapId;

    private String mobIds;

    private Integer itemId;

    private Integer remainingAfter;

    private Date createTime;
}
