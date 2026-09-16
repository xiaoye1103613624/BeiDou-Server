package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestSyncClientReqDTO {
    private Boolean dryRun;
    @Builder.Default
    private List<Integer> questIds = new ArrayList<>();
}
