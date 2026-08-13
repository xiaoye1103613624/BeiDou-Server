package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameIconSyncRtnDTO {
    private int version;
    private String region;
    private int requested;
    private int success;
    private int skipped;
    private int failed;
    private String message;
}
