package org.gms.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SetItemPreviewDTO {
    private String tooltipText;
    private String bonusSummary;
    private Integer finalDamageDisplayPercent;
    private Double finalDamageMultiplier;
}
