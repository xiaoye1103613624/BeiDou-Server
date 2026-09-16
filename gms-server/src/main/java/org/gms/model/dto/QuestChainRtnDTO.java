package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 以某任务为种子展开的完整连通链路。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestChainRtnDTO {
    private Integer seedQuestId;
    private List<QuestChainNodeDTO> nodes;
    private List<QuestChainEdgeDTO> edges;
    private List<String> warnings;
}
