package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TamingMobPoseListRtnDTO {
    private Long total;
    private List<TamingMobPoseItemDTO> items;
}
