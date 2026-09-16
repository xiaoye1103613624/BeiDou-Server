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
public class QuestSyncClientRtnDTO {
    private Boolean dryRun;
    private Boolean applied;
    private Boolean patcherAvailable;
    private String clientDataPath;
    private String message;
    @Builder.Default
    private List<String> files = new ArrayList<>();
    @Builder.Default
    private List<String> warnings = new ArrayList<>();
    private String exportDir;
}
