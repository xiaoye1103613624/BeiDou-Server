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
public class AssetInfoRtnDTO {
    private String root;
    private String legacyItemIconsDir;
    private boolean clientDataConfigured;
    private String clientDataPath;
    private List<ProviderInfo> providers;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProviderInfo {
        private String name;
        private boolean enabled;
    }
}
