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
public class ReloadScriptsByPathsResultDTO {
    @Builder.Default
    private List<String> reloaded = new ArrayList<>();
    @Builder.Default
    private List<SkippedPathDTO> skipped = new ArrayList<>();

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkippedPathDTO {
        private String path;
        /** WZ_NOT_SUPPORTED / UNKNOWN_TYPE / INVALID_PATH / NOT_SCRIPT */
        private String reason;
    }
}
