package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 新建/更新任务写盘载荷（结构化字段；列表 null=不改，空列表=清空）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestWriteReqDTO {
    private Integer questId;
    private String name;
    private String parentName;
    private String nameZh;
    private String text0;
    private String text1;
    private String text2;
    private Integer area;
    private Integer order;
    private Boolean autoStart;
    private Boolean autoPreComplete;
    private Boolean autoComplete;

    private Integer startNpcId;
    private Integer endNpcId;
    private Integer minLevel;
    private Integer maxLevel;
    private String startScript;
    private String endScript;
    private Integer nextQuestId;
    private Integer endExp;
    private Integer endMeso;
    private List<QuestItemNodeDTO> startItems;
    private List<QuestItemNodeDTO> endItems;
    private List<QuestMobNodeDTO> endMobs;
    private List<QuestLinkNodeDTO> upstreamQuests;
    private List<QuestItemNodeDTO> endRewards;
}
