package org.gms.model.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 在线角色列表请求参数
 * 支持按ID、名称、地图和世界进行过滤
 */
@Getter
@Setter
public class ChrOnlineListReqDTO extends BasePageDTO {
    /** 角色ID */
    private Integer id;
    /** 角色名称 */
    private String name;
    /** 地图ID */
    private Integer map;
    /** 世界ID */
    private int world;
}