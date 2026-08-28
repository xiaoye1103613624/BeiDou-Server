package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SetItemDetailDTO {
    private Long id;
    private Integer setId;
    private String setName;
    private Integer completeCount;
    private String itemIds;
    private Integer enabled;
    private Integer sortOrder;
    private String remark;
    private String tiersJson;
    private String source;
    private Boolean fromWz;
    private Boolean fromDb;
    private Integer tierCount;
    private Integer itemCount;
}
