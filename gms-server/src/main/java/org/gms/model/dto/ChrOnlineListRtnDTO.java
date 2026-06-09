package org.gms.model.dto;

import lombok.*;

import java.util.List;

/**
 * 在线角色列表返回参数
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChrOnlineListRtnDTO {
    /** 世界ID */
    private int world;
    /** 角色ID */
    private int id;
    /** 角色名称 */
    private String name;
    /** 地图ID */
    private int map;
    /** 职业ID */
    private int job;
    /** 职业名称 */
    private String jobName;
    /** 等级 */
    private int level;
    /** GM等级 */
    private int gm;
}