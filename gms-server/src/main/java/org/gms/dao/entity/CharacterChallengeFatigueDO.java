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
 * 角色挑战副本次数（普通/进阶/团队各自独立）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("xy_character_challenge_fatigue")
public class CharacterChallengeFatigueDO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id(keyType = KeyType.Auto)
    private Long id;

    private Integer characterId;

    /** 1=普通 2=进阶 3=团队 */
    private Integer challengeType;

    private Integer remaining;

    private Date lastResetDate;

    private Date createTime;

    private Date updateTime;
}
