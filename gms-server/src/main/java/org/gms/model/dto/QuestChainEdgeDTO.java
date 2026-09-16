package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 任务链路图边。type: next（完成引导下一任务）| prereq（前置要求）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestChainEdgeDTO {
    private Integer from;
    private Integer to;
    private String type;
}
