package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * 任务详情（结构化摘要 + 警告）。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestDetailRtnDTO {
    private Integer questId;
    private String name;
    private String parentName;
    private String text0;
    private String text1;
    private String text2;
    private Integer area;
    private Integer order;
    private Boolean autoStart;
    private Boolean autoPreComplete;
    private Boolean autoComplete;

    private Integer startNpcId;
    private String startNpcName;
    private Integer endNpcId;
    private String endNpcName;
    private Integer minLevel;
    private Integer maxLevel;
    private String startScript;
    private String endScript;

    @Builder.Default
    private List<QuestItemNodeDTO> startItems = new ArrayList<>();
    @Builder.Default
    private List<QuestItemNodeDTO> endItems = new ArrayList<>();
    @Builder.Default
    private List<QuestMobNodeDTO> startMobs = new ArrayList<>();
    @Builder.Default
    private List<QuestMobNodeDTO> endMobs = new ArrayList<>();
    @Builder.Default
    private List<QuestLinkNodeDTO> upstreamQuests = new ArrayList<>();
    private Integer nextQuestId;
    @Builder.Default
    private List<QuestItemNodeDTO> startRewards = new ArrayList<>();
    @Builder.Default
    private List<QuestItemNodeDTO> endRewards = new ArrayList<>();
    private Integer startExp;
    private Integer endExp;
    private Integer startMeso;
    private Integer endMeso;
    @Builder.Default
    private List<QuestMapMetaDTO> maps = new ArrayList<>();
    @Builder.Default
    private List<String> warnings = new ArrayList<>();
}
