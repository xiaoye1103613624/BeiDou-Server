package org.gms.model.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * 游戏配置查询请求DTO
 * <p>用于查询游戏配置参数的筛选条件</p>
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class GameConfigReqDTO extends BasePageDTO {
    /**
     * 参数类型
     */
    private String type;

    /**
     * 参数子类型
     */
    private String subType;

    /**
     * 搜索文本：名称、描述
     */
    private String filter;
}
