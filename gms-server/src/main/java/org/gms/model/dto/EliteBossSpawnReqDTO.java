package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 精英BOSS召唤请求DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EliteBossSpawnReqDTO {
    /** 配置ID */
    private Long configId;
    /** 目标大区ID列表（空或含-1表示所有大区） */
    private List<Integer> worldIds;
    /** 目标频道ID列表（空或含-1表示所有频道） */
    private List<Integer> channelIds;
    /** 召唤数量 */
    private Integer count;
    /** 是否同时召唤伴生BOSS（默认true） */
    private Boolean spawnCompanion;
}
