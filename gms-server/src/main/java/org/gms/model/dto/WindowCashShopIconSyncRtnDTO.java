package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WindowCashShopIconSyncRtnDTO {
    private String mode;
    private String iconDir;
    private int requested;
    private int updated;
    private int skipped;
    private int filesWritten;
    private int failed;
    private String message;
}
