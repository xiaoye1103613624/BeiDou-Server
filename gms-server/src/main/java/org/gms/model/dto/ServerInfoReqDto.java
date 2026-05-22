package org.gms.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 服务器信息查询请求DTO
 * <p>用于查询指定世界的服务器状态信息</p>
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServerInfoReqDto {
    /** 世界ID列表 */
    private List<Integer> worldIdList;

}
