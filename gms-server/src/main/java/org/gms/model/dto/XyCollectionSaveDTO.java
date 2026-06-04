package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class XyCollectionSaveDTO {
    private Long id;
    private String typeName;
    private String description;
    private Integer sortOrder;
    private Integer enabled;
    private String rewardType;
    private Integer rewardAmount;
    private List<StageDTO> stages;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class StageDTO {
        private Long id;
        private String stageName;
        private Integer sortOrder;
        private String rewardType;
        private Integer rewardAmount;
        private List<ItemDTO> items;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ItemDTO {
        private Long id;
        private Integer itemId;
        private Integer quantity;
        private Integer sortOrder;
    }
}
