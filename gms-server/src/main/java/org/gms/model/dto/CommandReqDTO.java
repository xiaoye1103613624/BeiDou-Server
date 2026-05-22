package org.gms.model.dto;


import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * GM指令查询请求DTO
 * <p>用于查询和管理GM指令的条件参数</p>
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class CommandReqDTO extends BasePageDTO {

    /** 指令ID */
    private Integer id;

    /** 权限等级（新增/修改时使用） */
    private Integer level;
    /** 权限等级列表（多选查询条件） */
    private List<Integer> levelList;

    /** 游戏内实际输入的指令语法 */
    private String syntax;

    /** 默认权限等级（新增/修改时使用） */
    private Integer defaultLevel;
    /** 默认权限等级列表（查询时使用） */
    private List<Integer> defaultLevelList;

    /** 指令所属类名 */
    private String clazz;
    /** 指令描述（支持模糊查询） */
    private String description;

    /** 是否启用（支持精确查询） */
    private Boolean enabled;

}
