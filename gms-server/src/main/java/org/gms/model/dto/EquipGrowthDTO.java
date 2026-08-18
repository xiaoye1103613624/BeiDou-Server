package org.gms.model.dto;

import lombok.Data;

@Data
public class EquipGrowthDTO {
    private Long id;
    private Integer itemId;
    private String itemName;
    private Integer enabled;
    private Integer maxLevel;
    private Integer sortOrder;
    private String remark;
    private String levelsJson;
    private String skillsJson;
    private String source;
    private Integer levelCount;
    private String tipPreview;
}
