package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillEnsureIconsRtnDTO {
    private Integer requested;
    private Integer cached;
    private Integer failed;
    private Map<Integer, String> urls;
}
