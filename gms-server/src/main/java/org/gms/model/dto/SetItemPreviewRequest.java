package org.gms.model.dto;

import lombok.Data;

@Data
public class SetItemPreviewRequest {
    private Integer setId;
    private Integer equippedCount;
    private Integer jobId;
}
