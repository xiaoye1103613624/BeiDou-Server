package org.gms.model.dto;

import lombok.Getter;
import lombok.Setter;

/**
 * 在线角色列表查询请求DTO
 * <p>用于查询当前在线玩家列表的筛选条件</p>
 */
@Getter
@Setter
public class ChrOnlineListReqDTO extends BasePageDTO {
    /** 角色ID */
    private Integer id;
    /** 角色名称 */
    private String name;
    /** 所在地图ID */
    private Integer map;
    /** 所属世界ID */
    private int world;
}
