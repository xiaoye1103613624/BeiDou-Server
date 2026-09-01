package org.gms.dao.entity;

import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Timestamp;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("ranking_combat_power")
public class RankingCombatPowerDO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    private Integer characterId;
    private Integer world;
    private String name;
    private Integer job;
    private Integer jobNiche;
    private Integer level;
    private Long combatPower;
    private Integer baseDamage;
    private Timestamp updatedAt;
}
