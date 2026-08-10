package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 宠物成长链预览（按 chainCode 分组）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PetGrowthPreviewDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String chainCode;
    private boolean safe;
    private String warning;
    @Builder.Default
    private List<PetGrowthStageDTO> stages = new ArrayList<>();
}
