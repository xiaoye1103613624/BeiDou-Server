package org.gms.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class SetItemWzImportRequest {
    private List<Integer> setIds;
    /** NEW_ONLY | MERGE | OVERWRITE */
    private String mode;
}
