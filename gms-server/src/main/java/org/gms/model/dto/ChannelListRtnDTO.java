package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 频道列表返回DTO
 * <p>用于返回服务器频道的基本信息</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChannelListRtnDTO {
    /** 频道ID */
    private Integer id;
    /** 所属世界ID */
    private Integer worldId;
}
