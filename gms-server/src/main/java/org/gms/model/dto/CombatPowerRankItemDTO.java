package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CombatPowerRankItemDTO {
    private Integer rank;
    private Integer characterId;
    private String name;
    private Integer world;
    private Integer job;
    private String jobName;
    private Integer jobNiche;
    private String jobNicheName;
    private Integer level;
    private Long combatPower;
    private Integer baseDamage;
}
