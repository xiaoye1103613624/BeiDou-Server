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
public class ChairPosePatchRtnDTO {
    private Boolean dryRun;
    private Boolean applied;
    private Boolean patcherAvailable;
    private String clientDataPath;
    private String clientEnPath;
    private String exportDir;
    private List<String> files;
    private List<String> warnings;
    private String message;
}
