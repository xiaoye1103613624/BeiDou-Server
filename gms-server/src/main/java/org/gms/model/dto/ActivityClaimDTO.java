package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityClaimDTO {
    private Long id;
    private Long sessionId;
    private String tierCode;
    private String status;
    private Long mesos;
    private Integer exp;
    private Integer itemId;
    private Integer itemQty;
    private Integer item2Id;
    private Integer item2Qty;
}
