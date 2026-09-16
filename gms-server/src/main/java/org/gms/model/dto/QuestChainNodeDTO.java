package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 任务链路图节点。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestChainNodeDTO {
    private Integer questId;
    private String name;
    private String parentName;
    private Integer startNpcId;
    private String startNpcName;
    private Integer minLevel;
    /** 是否为本次请求的种子任务 */
    private Boolean current;
}
