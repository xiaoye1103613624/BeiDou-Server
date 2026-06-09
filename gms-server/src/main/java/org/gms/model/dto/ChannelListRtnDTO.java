package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 频道列表返回参数
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