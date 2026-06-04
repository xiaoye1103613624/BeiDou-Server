package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 精英BOSS状态返回DTO（含各频道存活状态）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EliteBossStatusRtnDTO {
    /** 配置ID */
    private Long id;
    /** 地图ID */
    private Integer mapId;
    /** 地图名称 */
    private String mapName;
    /** 怪物ID */
    private Integer bossId;
    /** BOSS名称 */
    private String bossName;
    /** 伴生BOSS怪物ID */
    private Integer companionBossId;
    /** BOSS等级 */
    private Integer bossLevel;
    /** BOSS最大HP */
    private Long bossMaxHp;
    /** BOSS经验 */
    private Long bossExp;
    /** 刷新时间（分钟） */
    private Integer bossTime;
    /** 脚本名称 */
    private String scriptName;
    /** 是否启用 */
    private Integer enabled;
    /** 各频道存活状态 */
    private List<ChannelBossStatus> channelStatuses;
    /** 存活总数 */
    private Integer aliveCount;
    /** 频道总数 */
    private Integer totalChannelCount;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class ChannelBossStatus {
        /** 大区ID */
        private Integer worldId;
        /** 频道ID */
        private Integer channelId;
        /** 是否存活 */
        private Boolean alive;
        /** 存活数量 */
        private Integer count;
    }
}
