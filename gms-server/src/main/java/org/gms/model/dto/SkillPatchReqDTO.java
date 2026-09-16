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
public class SkillPatchReqDTO {
    /** 默认 true（dry-run） */
    private Boolean dryRun;
    /** 空=导出已启用目录全部 jobId */
    private List<Integer> jobIds;
}
