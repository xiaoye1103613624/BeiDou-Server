package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 【类型】WorldListRtnDTO（class），包 `org.gms.model.dto`。
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class WorldListRtnDTO {
    private Integer id;
    private Float expRate;
    private Float dropRate;
    private Float mesoRate;
    private Float bossDropRate;
    private Float questRate;
    private Float travelRate;
    private Float fishingRate;
}
