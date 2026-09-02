package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityRewardTierDTO {
    private Long id;
    private String activityCode;
    private String tierCode;
    private String tierName;
    private Integer priority;
    private String exclusiveGroup;
    private String matchJson;
    private String grantMode;
    private Long mesos;
    private Integer exp;
    private Integer itemId;
    private Integer itemQty;
    private Integer item2Id;
    private Integer item2Qty;
    private Boolean announceName;
    private String announceTpl;
    private Boolean enabled;
    private String remark;
}
